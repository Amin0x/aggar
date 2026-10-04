package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.NeighborhoodDto;
import com.amin.aggar.frontend.dto.CityDto;
import com.amin.aggar.frontend.form.NeighborhoodForm;
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
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class NeighborhoodController {

    private static final Logger log = LoggerFactory.getLogger(NeighborhoodController.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public NeighborhoodController(
            RestTemplate restTemplate,
            @Value("${external.api.url:http://localhost:8080/api}") String apiUrl,
            MessageSource messageSource) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.messageSource = messageSource;
    }

    @GetMapping("/neighborhoods/list")
    public String list(@RequestParam(value = "q", required = false) String q, Model model, Locale locale) {
        try {
            List<NeighborhoodDto> neighborhoods = fetchAllNeighborhoods();
            if (q != null && !q.isBlank()) {
                String query = q.trim().toLowerCase(Locale.ROOT);
                neighborhoods = neighborhoods.stream()
                        .filter(neighborhood -> containsIgnoreCase(neighborhood.name(), query)
                                || containsIgnoreCase(neighborhood.cityName(), query))
                        .toList();
            }
            model.addAttribute("neighborhoods", neighborhoods);
        } catch (Exception ex) {
            log.error("Failed to fetch neighborhoods from API", ex);
            model.addAttribute("neighborhoods", Collections.emptyList());
            model.addAttribute("error", messageSource.getMessage("neighborhood.error.load", null, locale));
        }
        return "admin/neighborhood-list";
    }

    private List<NeighborhoodDto> fetchAllNeighborhoods() throws IOException {
        List<NeighborhoodDto> neighborhoods = new ArrayList<>();
        for (int page = 0; ; page++) {
            URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                    .pathSegment("neighborhoods")
                    .queryParam("page", page)
                    .queryParam("size", 100)
                    .build()
                    .toUri();
            String response = restTemplate.getForObject(uri, String.class);
            if (response == null) {
                throw new IllegalStateException("Neighborhoods API returned an empty response");
            }

            JsonNode pageResponse = objectMapper.readTree(response);
            JsonNode content = pageResponse.path("content");
            JsonNode last = pageResponse.path("last");
            if (!content.isArray() || !last.isBoolean()) {
                throw new IllegalStateException("Neighborhoods API returned an invalid page response");
            }
            neighborhoods.addAll(objectMapper.convertValue(
                    content, new TypeReference<List<NeighborhoodDto>>() {}));
            if (last.booleanValue()) {
                return neighborhoods;
            }
        }
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    @GetMapping("/neighborhoods/add")
    public String addForm(Model model) {
        addCitiesToModel(model);
        model.addAttribute("neighborhood", new NeighborhoodForm());
        return "admin/add-neighborhood";
    }

    @PostMapping("/neighborhoods/add")
    public String create(@ModelAttribute NeighborhoodForm form, Model model,
                         RedirectAttributes redirectAttributes, Locale locale) {
        NeighborhoodDto dto = new NeighborhoodDto(null, form.getCityId(), form.getName(), null);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<NeighborhoodDto> request = new HttpEntity<>(dto, headers);
            ResponseEntity<NeighborhoodDto> resp = restTemplate.exchange(
                    apiUrl + "/neighborhoods",
                    HttpMethod.POST,
                    request,
                    NeighborhoodDto.class
            );

            if (resp.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addAttribute("success", messageSource.getMessage("neighborhood.success.add", null, locale));
                return "redirect:/neighborhoods/list";
            } else {
                model.addAttribute("error", messageSource.getMessage("neighborhood.error.add", null, locale));
                addCitiesToModel(model);
                model.addAttribute("neighborhood", form);
                return "admin/add-neighborhood";
            }
        } catch (Exception ex) {
            log.error("Failed to create neighborhood", ex);
            model.addAttribute("error", messageSource.getMessage("neighborhood.error.add", null, locale));
            addCitiesToModel(model);
            model.addAttribute("neighborhood", form);
            return "admin/add-neighborhood";
        }
    }

    @GetMapping("/neighborhoods/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model,
                           RedirectAttributes redirectAttributes, Locale locale) {
        try {
            ResponseEntity<NeighborhoodDto> resp = restTemplate.exchange(
                    apiUrl + "/neighborhoods/" + id,
                    HttpMethod.GET,
                    null,
                    NeighborhoodDto.class
            );

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                NeighborhoodDto dto = resp.getBody();
                model.addAttribute("neighborhood", new NeighborhoodForm(dto.cityId(), dto.name()));
                model.addAttribute("neighborhoodId", id);
                addCitiesToModel(model);
                return "admin/edit-neighborhood";
            } else {
                redirectAttributes.addAttribute("error", messageSource.getMessage("neighborhood.error.not-found", null, locale));
                return "redirect:/neighborhoods/list";
            }
        } catch (Exception ex) {
            log.error("Failed to fetch neighborhood {} from API", id, ex);
            redirectAttributes.addAttribute("error", messageSource.getMessage("neighborhood.error.load-one", null, locale));
            return "redirect:/neighborhoods/list";
        }
    }

    @PostMapping("/neighborhoods/edit/{id}")
    public String update(@PathVariable("id") Integer id, @ModelAttribute NeighborhoodForm form, Model model,
                         RedirectAttributes redirectAttributes, Locale locale) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            NeighborhoodDto dto = new NeighborhoodDto(id, form.getCityId(), form.getName(), null);
            HttpEntity<NeighborhoodDto> request = new HttpEntity<>(dto, headers);
            ResponseEntity<NeighborhoodDto> resp = restTemplate.exchange(
                    apiUrl + "/neighborhoods/" + id,
                    HttpMethod.PUT,
                    request,
                    NeighborhoodDto.class
            );

            if (resp.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addAttribute("success", messageSource.getMessage("neighborhood.success.update", null, locale));
                return "redirect:/neighborhoods/list";
            } else {
                model.addAttribute("error", messageSource.getMessage("neighborhood.error.update", null, locale));
                addCitiesToModel(model);
                model.addAttribute("neighborhood", form);
                model.addAttribute("neighborhoodId", id);
                return "admin/edit-neighborhood";
            }
        } catch (Exception ex) {
            log.error("Failed to update neighborhood {}", id, ex);
            model.addAttribute("error", messageSource.getMessage("neighborhood.error.update", null, locale));
            addCitiesToModel(model);
            model.addAttribute("neighborhood", form);
            model.addAttribute("neighborhoodId", id);
            return "admin/edit-neighborhood";
        }
    }

    @PostMapping("/neighborhoods/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            restTemplate.exchange(
                    apiUrl + "/neighborhoods/" + id,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            redirectAttributes.addAttribute("success", messageSource.getMessage("neighborhood.success.delete", null, locale));
            return "redirect:/neighborhoods/list";
        } catch (Exception ex) {
            log.error("Failed to delete neighborhood {}", id, ex);
            redirectAttributes.addAttribute("error", messageSource.getMessage("neighborhood.error.delete", null, locale));
            return "redirect:/neighborhoods/list";
        }
    }

    private void addCitiesToModel(Model model) {
        try {
            model.addAttribute("cities", fetchAllCities());
        } catch (Exception ex) {
            log.error("Failed to fetch cities", ex);
            model.addAttribute("cities", Collections.emptyList());
        }
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
}