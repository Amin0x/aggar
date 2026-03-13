package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.CityDto;
import com.amin.aggar.frontend.dto.StateDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class CityController {

    private String apiUrl = "http://localhost:8080/api";
    private final RestTemplate restTemplate;

    public CityController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/cities/search")
    public ResponseEntity<List<CityDto>> search(@RequestParam String q){
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
    public String list(@RequestParam(required = false) String q, Model model){
        ParameterizedTypeReference<List<CityDto>> typeReference = new ParameterizedTypeReference<>() {
        };

        String url = UriComponentsBuilder.fromUriString(apiUrl + "/cities")
                .queryParamIfPresent("q", Optional.ofNullable(q))
                .toUriString();
        ResponseEntity<List<CityDto>> resp = restTemplate.exchange(
                url, HttpMethod.GET, null, typeReference);

        if (resp.getStatusCode().is2xxSuccessful()){
            model.addAttribute("cities", resp.getBody());
            return "city-list";
        }

        return "city-list";
    }

    @GetMapping("/cities/add")
    public String addCity(Model model){
        String url = UriComponentsBuilder.fromUriString(apiUrl + "/states")
                .toUriString();

        ResponseEntity<List<StateDto>> resp = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        List<StateDto> states = resp.getBody();

        model.addAttribute("states", states);
        model.addAttribute("city", new CityDto());
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        return "add-city";
    }

    @PostMapping("/cities/add")
    public String create(@ModelAttribute CityDto cityDto, Model model){
        String url = UriComponentsBuilder.fromUriString(apiUrl + "/cities")
                .toUriString();

        ResponseEntity<CityDto> resp = restTemplate.postForEntity(url, cityDto, CityDto.class);

        if (!resp.getStatusCode().is2xxSuccessful()){

        }

        model.addAttribute("city", resp.getBody());
        return "add-city";
    }

    @PostMapping("/cities/delete")
    public String delete(@RequestParam() Integer id){
        return "redirect:/cities/list";
    }
}
