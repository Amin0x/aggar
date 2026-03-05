package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.PropertyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final RestTemplate restTemplate;
    private final String propertiesApiUrl;

    public HomeController(RestTemplate restTemplate,
                          @Value("${external.api.properties-url:http://localhost:8080/api/properties}") String propertiesApiUrl) {
        this.restTemplate = restTemplate;
        this.propertiesApiUrl = propertiesApiUrl;
    }

    @GetMapping("/search")
    public String search(Model model,
                        @RequestParam String q) {
        List<PropertyDto> properties;
        try {
            String url = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                    .queryParam("q", q)
                    .toUriString();

            ResponseEntity<List<PropertyDto>> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PropertyDto>>() {
                    }
            );
            properties = resp.getBody();
            if (properties == null) properties = Collections.emptyList();
        } catch (Exception ex) {
            log.error("Failed to fetch properties from API {}", propertiesApiUrl, ex);
            properties = Collections.emptyList();
        }

        model.addAttribute("properties", properties);
        model.addAttribute("searchQuery", q);
        return "search";
    }

    @GetMapping({"/", "/home"})
    public String home(Model model,
                       @RequestParam(required = false) String listingType,
                       @RequestParam(required = false) String city,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String q) {
        List<PropertyDto> properties;
        try {
            String url = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                    .queryParamIfPresent("listingType", Optional.ofNullable(listingType))
                    .queryParamIfPresent("city", Optional.ofNullable(city))
                    .queryParamIfPresent("category", Optional.ofNullable(category))
                    .queryParamIfPresent("q", Optional.ofNullable(q))
                    .toUriString();

            ResponseEntity<List<PropertyDto>> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PropertyDto>>() {
                    }
            );
            properties = resp.getBody();
            if (properties == null) properties = Collections.emptyList();
        } catch (Exception ex) {
            log.error("Failed to fetch properties from API {}", propertiesApiUrl, ex);
            properties = Collections.emptyList();
        }

        // Compute categories from the returned properties
        Set<String> listingTypes = properties.stream()
                .map(PropertyDto::getListingType)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> cities = properties.stream()
                .map(PropertyDto::getCity)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        model.addAttribute("properties", properties);
        model.addAttribute("listingTypes", listingTypes);
        model.addAttribute("cities", cities);
        model.addAttribute("selectedListingType", listingType);
        model.addAttribute("selectedCity", city);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("q", q);
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "terms-of-service";
    }

    @GetMapping("/cookie-policy")
    public String cookiePolicy() {
        return "cookie-policy";
    }
}
