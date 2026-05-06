package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.PropertyDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final RestTemplate restTemplate;
    private final String propertiesApiUrl;
    private final ObjectMapper objectMapper;

    public HomeController(RestTemplate restTemplate,
                          @Value("${external.api.properties-url:http://localhost:8080/api/properties}") String propertiesApiUrl) {
        this.restTemplate = restTemplate;
        this.propertiesApiUrl = propertiesApiUrl;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @GetMapping("/search")
    public String search(Model model,
                        @RequestParam("q") String q) {
        List<PropertyDto> properties;
        try {
            String url = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                    .queryParam("q", q)
                    .queryParam("page", 0)
                    .queryParam("size", 50)
                    .toUriString();

            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            
            String responseBody = resp.getBody();
            if (responseBody != null) {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode.has("content") && !jsonNode.get("content").isNull()) {
                    properties = objectMapper.convertValue(jsonNode.get("content"),
                        new TypeReference<List<PropertyDto>>() {});
                } else {
                    properties = Collections.emptyList();
                }
            } else {
                properties = Collections.emptyList();
            }
        } catch (Exception ex) {
            log.error("Failed to fetch properties from API {}", propertiesApiUrl, ex);
            properties = Collections.emptyList();
        }

        model.addAttribute("properties", properties);
        model.addAttribute("listingTypes", new LinkedHashSet<String>());
        model.addAttribute("cities", new LinkedHashSet<String>());
        model.addAttribute("searchQuery", q);
        return "search";
    }

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(name = "listingType", required = false) String listingType,
                       @RequestParam(name = "city", required = false) String city,
                       @RequestParam(name = "category", required = false) String category,
                       @RequestParam(name = "q", required = false) String q,
                       Model model) {
        List<PropertyDto> properties;
        try {
            String url = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                    .queryParamIfPresent("listingType", Optional.ofNullable(listingType))
                    .queryParamIfPresent("city", Optional.ofNullable(city))
                    .queryParamIfPresent("category", Optional.ofNullable(category))
                    .queryParamIfPresent("q", Optional.ofNullable(q))
                    .queryParam("page", 0)
                    .queryParam("size", 50)
                    .toUriString();

            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            
            String responseBody = resp.getBody();
            if (responseBody != null) {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode.has("content") && !jsonNode.get("content").isNull()) {
                    properties = objectMapper.convertValue(jsonNode.get("content"),
                        new TypeReference<List<PropertyDto>>() {});
                } else {
                    properties = new ArrayList<>();
                }
            } else {
                properties = new ArrayList<>();
            }
        } catch (Exception ex) {
            log.error("Failed to fetch properties from API {}", propertiesApiUrl, ex);
            properties = new ArrayList<>();
        }

        // Compute categories from the returned properties
        List<String> listingTypes = properties.stream()
                .map(PropertyDto::getListingType)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> cities = properties.stream()
                .map(PropertyDto::getCity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Ensure collections are never null for Thymeleaf
        if (listingTypes == null) {
            listingTypes = new ArrayList<>();
        }
        if (cities == null) {
            cities = new ArrayList<>();
        }

        model.addAttribute("properties", properties);
        model.addAttribute("listingTypes", listingTypes);
        model.addAttribute("cities", cities);
        model.addAttribute("selectedListingType", null);
        model.addAttribute("selectedCity", city);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("q", q);
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        return "contact";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy(Model model) {
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        return "privacy-policy";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService(Model model) {
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        return "terms-of-service";
    }

    @GetMapping("/cookie-policy")
    public String cookiePolicy(Model model) {
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        return "cookie-policy";
    }
}
