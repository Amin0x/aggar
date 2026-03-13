package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.PropertyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class PropertyController {

    private static final Logger log = LoggerFactory.getLogger(PropertyController.class);

    private final RestTemplate restTemplate;
    private final String propertiesApiUrl;

    public PropertyController(RestTemplate restTemplate,
                              @Value("${external.api.properties-url:http://localhost:8080/api/properties}") String propertiesApiUrl) {
        this.restTemplate = restTemplate;
        this.propertiesApiUrl = propertiesApiUrl;
    }

    @GetMapping("/properties/{id}")
    public String details(@PathVariable("id") Long id, Model model) {
        try {
            // call backend detail endpoint if available: /api/properties/{id}
            String url = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                    .pathSegment(id.toString())
                    .toUriString();

            ResponseEntity<PropertyDto> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    PropertyDto.class
            );

            PropertyDto property = resp.getBody();
            if (property == null) {
                // fallback: call list endpoint and find by id
                ResponseEntity<List<PropertyDto>> listResp = restTemplate.exchange(
                        propertiesApiUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<PropertyDto>>() {
                        }
                );
                List<PropertyDto> props = listResp.getBody();
                if (props != null) {
                    property = props.stream().filter(p -> Optional.ofNullable(p.getId()).map(i -> i.equals(id)).orElse(false)).findFirst().orElse(null);
                }
            }

            if (property == null) {
                return "redirect:/";
            }

            model.addAttribute("property", property);
            
            // Add required attributes for header fragment
            model.addAttribute("listingTypes", Collections.emptyList());
            model.addAttribute("cities", Collections.emptyList());
            
            return "property-detail";
        } catch (Exception ex) {
            log.error("Failed to fetch property {} from API {}", id, propertiesApiUrl, ex);
            return "redirect:/";
        }
    }

    @GetMapping("/properties/add")
    public String addPropertyForm(Model model) {
        model.addAttribute("property", new PropertyDto());
        
        // Add required attributes for header fragment
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        
        return "add-property";
    }

    @PostMapping("/properties/add")
    public String addProperty(@ModelAttribute("property") PropertyDto property, 
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        try {
            // Basic validation
            if (property.getTitle() == null || property.getTitle().trim().isEmpty()) {
                result.rejectValue("title", "error.property", "Title is required");
            }
            if (property.getListingType() == null) {
                result.rejectValue("listingType", "error.property", "Listing type is required");
            }
            if (property.getPrice() == null || property.getPrice().doubleValue() <= 0) {
                result.rejectValue("price", "error.property", "Price must be greater than 0");
            }
            if (property.getCity() == null || property.getCity().trim().isEmpty()) {
                result.rejectValue("city", "error.property", "City is required");
            }

            if (result.hasErrors()) {
                return "add-property";
            }

            // Convert location names to IDs (simplified approach)
            // In a real application, you would query the backend API to resolve these
            convertLocationNamesToIds(property);

            // Create headers for JSON request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create request entity
            HttpEntity<PropertyDto> request = new HttpEntity<>(property, headers);

            // Call backend API to create property
            ResponseEntity<PropertyDto> response = restTemplate.exchange(
                    propertiesApiUrl,
                    HttpMethod.POST,
                    request,
                    PropertyDto.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("success", "Property added successfully!");
                return "redirect:/properties/" + response.getBody().getId();
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to add property. Please try again.");
                return "redirect:/properties/add";
            }

        } catch (Exception ex) {
            log.error("Failed to create property", ex);
            redirectAttributes.addFlashAttribute("error", "An error occurred while adding the property. Please try again.");
            return "redirect:/properties/add";
        }
    }

    private void convertLocationNamesToIds(PropertyDto property) {
        // Simplified location mapping for demo purposes
        // In production, you would call location resolution APIs
        
        // Default mappings (you should replace these with actual API calls)
        if (property.getState() != null) {
            // Map common state names to IDs (simplified)
            switch (property.getState().toLowerCase()) {
                case "california": property.setStateId(1); break;
                case "new york": property.setStateId(2); break;
                case "texas": property.setStateId(3); break;
                default: property.setStateId(1); // Default to California
            }
        }
        
        if (property.getCity() != null) {
            // Map common city names to IDs (simplified)
            switch (property.getCity().toLowerCase()) {
                case "los angeles": property.setCityId(1); break;
                case "new york": property.setCityId(2); break;
                case "houston": property.setCityId(3); break;
                default: property.setCityId(1); // Default to Los Angeles
            }
        }
        
        if (property.getNeighborhood() != null && !property.getNeighborhood().trim().isEmpty()) {
            // Map common neighborhood names to IDs (simplified)
            switch (property.getNeighborhood().toLowerCase()) {
                case "beverly hills": property.setNeighborhoodId(1); break;
                case "manhattan": property.setNeighborhoodId(2); break;
                case "brooklyn": property.setNeighborhoodId(3); break;
                default: property.setNeighborhoodId(null); // Optional field
            }
        }
        
        // Set default status if not provided
        if (property.getStatus() == null || property.getStatus().trim().isEmpty()) {
            property.setStatus("available");
        }
    }
}

