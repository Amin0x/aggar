package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.CityDto;
import com.amin.aggar.frontend.dto.StateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class CityController {

    private static final Logger log = LoggerFactory.getLogger(CityController.class);

    private final String apiUrl = "http://localhost:8080/api";
    private final RestTemplate restTemplate;

    public CityController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/cities/search")
    public ResponseEntity<List<CityDto>> search(@RequestParam String q) {
        String url = UriComponentsBuilder.fromUriString(apiUrl + "/cities")
                .queryParamIfPresent("q", Optional.ofNullable(q))
                .toUriString();

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
    }

    @GetMapping("/cities/list")
    public String list(@RequestParam(required = false) String q, Model model) {
        ParameterizedTypeReference<List<CityDto>> typeReference = new ParameterizedTypeReference<>() {};

        String url = UriComponentsBuilder.fromUriString(apiUrl + "/cities")
                .queryParamIfPresent("q", Optional.ofNullable(q))
                .toUriString();

        try {
            ResponseEntity<List<CityDto>> resp = restTemplate.exchange(
                    url, HttpMethod.GET, null, typeReference);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                model.addAttribute("cities", resp.getBody());
            } else {
                model.addAttribute("cities", Collections.emptyList());
                model.addAttribute("error", "Failed to load cities");
            }
        } catch (Exception ex) {
            log.error("Failed to fetch cities from API", ex);
            model.addAttribute("cities", Collections.emptyList());
            model.addAttribute("error", "Failed to load cities. Please try again.");
        }

        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", model.getAttribute("cities"));

        return "city-list";
    }

    @GetMapping("/cities/add")
    public String addCityForm(Model model) {
        try {
            String url = UriComponentsBuilder.fromUriString(apiUrl + "/states")
                    .toUriString();

            ResponseEntity<List<StateDto>> resp = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            List<StateDto> states = resp.getBody() != null ? resp.getBody() : Collections.emptyList();
            model.addAttribute("states", states);

        } catch (Exception ex) {
            log.error("Failed to fetch states from API", ex);
            model.addAttribute("states", Collections.emptyList());
        }

        model.addAttribute("city", new CityDto());
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());

        return "add-city";
    }

    @PostMapping("/cities/add")
    public String create(@ModelAttribute CityDto cityDto, Model model) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CityDto> request = new HttpEntity<>(cityDto, headers);

            ResponseEntity<CityDto> resp = restTemplate.postForEntity(
                    apiUrl + "/cities",
                    request,
                    CityDto.class
            );

            if (resp.getStatusCode().is2xxSuccessful()) {
                return "redirect:/cities/list?success=City added successfully";
            } else {
                model.addAttribute("error", "Failed to add city. Please try again.");
                model.addAttribute("city", cityDto);
                model.addAttribute("states", Collections.emptyList());
                return "add-city";
            }

        } catch (Exception ex) {
            log.error("Failed to create city", ex);
            model.addAttribute("error", "An error occurred while adding the city. Please try again.");
            model.addAttribute("city", cityDto);
            model.addAttribute("states", Collections.emptyList());
            return "add-city";
        }
    }

    @PostMapping("/cities/delete")
    public String delete(@RequestParam Integer id) {
        // TODO: Implement actual delete via API
        return "redirect:/cities/list";
    }
}
