package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.CityDto;
import com.amin.aggar.frontend.dto.StateDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class CityController {

    private static final Logger log = LoggerFactory.getLogger(CityController.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CityController(
            RestTemplate restTemplate,
            @Value("${external.api.url:http://localhost:8080/api}") String apiUrl,
            MessageSource messageSource) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.messageSource = messageSource;
    }

    @GetMapping("/cities/list")
    public String list(@RequestParam(value = "q", required = false) String q, Model model, Locale locale) {
        try {
            List<CityDto> cities = fetchAllCities();
            if (q != null && !q.isBlank()) {
                String query = q.trim().toLowerCase(Locale.ROOT);
                cities = cities.stream()
                        .filter(city -> containsIgnoreCase(city.getName(), query)
                                || containsIgnoreCase(city.getStateName(), query)
                                || containsIgnoreCase(city.getStateNameAr(), query))
                        .toList();
            }
            model.addAttribute("cities", cities);
        } catch (Exception ex) {
            log.error("Failed to fetch cities from API", ex);
            model.addAttribute("cities", Collections.emptyList());
            model.addAttribute("error", messageSource.getMessage("city.error.load", null, locale));
        }
        return "admin/city-list";
    }

    private List<CityDto> fetchAllCities() throws IOException {
        List<CityDto> cities = new ArrayList<>();
        for (int page = 0; ; page++) {
            URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                    .pathSegment("cities")
                    .queryParam("page", page)
                    .queryParam("size", 100)
                    .build()
                    .toUri();
            String response = restTemplate.getForObject(uri, String.class);
            if (response == null) {
                throw new IllegalStateException("Cities API returned an empty response");
            }

            JsonNode pageResponse = objectMapper.readTree(response);
            JsonNode content = pageResponse.path("content");
            JsonNode last = pageResponse.path("last");
            if (!content.isArray() || !last.isBoolean()) {
                throw new IllegalStateException("Cities API returned an invalid page response");
            }
            cities.addAll(objectMapper.convertValue(content, new TypeReference<List<CityDto>>() {}));
            if (last.booleanValue()) {
                return cities;
            }
        }
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    @GetMapping("/cities/add")
    public String addForm(Model model) {
        try {
            ResponseEntity<List<StateDto>> resp = restTemplate.exchange(
                    apiUrl + "/states", HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});
            List<StateDto> states = resp.getBody() != null ? resp.getBody() : Collections.emptyList();
            model.addAttribute("states", states);
        } catch (Exception ex) {
            log.error("Failed to fetch states from API", ex);
            model.addAttribute("states", Collections.emptyList());
        }
        model.addAttribute("city", new CityDto());
        return "admin/add-city";
    }

    @PostMapping("/cities/add")
    public String create(@ModelAttribute CityDto cityDto, Model model,
                         RedirectAttributes redirectAttributes, Locale locale) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CityDto> request = new HttpEntity<>(cityDto, headers);
            ResponseEntity<CityDto> resp = restTemplate.exchange(
                    apiUrl + "/cities",
                    HttpMethod.POST,
                    request,
                    CityDto.class
            );

            if (resp.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addAttribute("success", messageSource.getMessage("city.success.add", null, locale));
                return "redirect:/cities/list";
            } else {
                model.addAttribute("error", messageSource.getMessage("city.error.add", null, locale));
                addStatesToModel(model);
                model.addAttribute("city", cityDto);
                return "admin/add-city";
            }
        } catch (Exception ex) {
            log.error("Failed to create city", ex);
            model.addAttribute("error", messageSource.getMessage("city.error.add", null, locale));
            addStatesToModel(model);
            model.addAttribute("city", cityDto);
            return "admin/add-city";
        }
    }

    @GetMapping("/cities/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model,
                           RedirectAttributes redirectAttributes, Locale locale) {
        try {
            ResponseEntity<CityDto> resp = restTemplate.exchange(
                    apiUrl + "/cities/" + id,
                    HttpMethod.GET,
                    null,
                    CityDto.class
            );

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                model.addAttribute("city", resp.getBody());
                model.addAttribute("cityId", id);
                addStatesToModel(model);
                return "admin/edit-city";
            } else {
                redirectAttributes.addAttribute("error", messageSource.getMessage("city.error.not-found", null, locale));
                return "redirect:/cities/list";
            }
        } catch (Exception ex) {
            log.error("Failed to fetch city {} from API", id, ex);
            redirectAttributes.addAttribute("error", messageSource.getMessage("city.error.load-one", null, locale));
            return "redirect:/cities/list";
        }
    }

    @PostMapping("/cities/edit/{id}")
    public String update(@PathVariable("id") Integer id, @ModelAttribute CityDto cityDto, Model model,
                         RedirectAttributes redirectAttributes, Locale locale) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CityDto> request = new HttpEntity<>(cityDto, headers);
            ResponseEntity<CityDto> resp = restTemplate.exchange(
                    apiUrl + "/cities/" + id,
                    HttpMethod.PUT,
                    request,
                    CityDto.class
            );

            if (resp.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addAttribute("success", messageSource.getMessage("city.success.update", null, locale));
                return "redirect:/cities/list";
            } else {
                model.addAttribute("error", messageSource.getMessage("city.error.update", null, locale));
                addStatesToModel(model);
                model.addAttribute("city", cityDto);
                model.addAttribute("cityId", id);
                return "admin/edit-city";
            }
        } catch (Exception ex) {
            log.error("Failed to update city {}", id, ex);
            model.addAttribute("error", messageSource.getMessage("city.error.update", null, locale));
            addStatesToModel(model);
            model.addAttribute("city", cityDto);
            model.addAttribute("cityId", id);
            return "admin/edit-city";
        }
    }

    @PostMapping("/cities/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            restTemplate.exchange(
                    apiUrl + "/cities/" + id,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            redirectAttributes.addAttribute("success", messageSource.getMessage("city.success.delete", null, locale));
            return "redirect:/cities/list";
        } catch (Exception ex) {
            log.error("Failed to delete city {}", id, ex);
            redirectAttributes.addAttribute("error", messageSource.getMessage("city.error.delete", null, locale));
            return "redirect:/cities/list";
        }
    }

    private void addStatesToModel(Model model) {
        try {
            ResponseEntity<List<StateDto>> resp = restTemplate.exchange(
                    apiUrl + "/states", HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});
            List<StateDto> states = resp.getBody() != null ? resp.getBody() : Collections.emptyList();
            model.addAttribute("states", states);
        } catch (Exception ex) {
            log.error("Failed to fetch states", ex);
            model.addAttribute("states", Collections.emptyList());
        }
    }
}