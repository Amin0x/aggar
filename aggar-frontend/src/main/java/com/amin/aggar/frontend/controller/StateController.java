package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.StateDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

public class StateController {
    private final String apiUrl = "http://localhost:8080/api";
    private final RestTemplate restTemplate;

    public StateController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/states/search")
    public ResponseEntity<List<StateDto>> search(@RequestParam String q){
        String url = UriComponentsBuilder.fromUriString(apiUrl + "/states")
                .queryParam("q", q)
                .toUriString();

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
    }

    @GetMapping("/states/list")
    public String list(Model model){
        ParameterizedTypeReference<List<StateDto>> typeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<StateDto>> resp = restTemplate.exchange(
                apiUrl + "/states", HttpMethod.GET, null, typeReference);

        if (resp.getStatusCode().is2xxSuccessful()){
            model.addAttribute("states", resp.getBody());
        } else {
            model.addAttribute("states", Collections.emptyList());
        }
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        return "state-list";
    }

    @GetMapping("/states/add")
    public String addState(Model model){
        model.addAttribute("state", new StateDto());
        model.addAttribute("listingTypes", Collections.emptyList());
        model.addAttribute("cities", Collections.emptyList());
        return "add-state";
    }

    @PostMapping("/states/add")
    public String create(@ModelAttribute StateDto stateDto, Model model){
        String url = UriComponentsBuilder.fromUriString(apiUrl + "/states")
                .toUriString();

        ResponseEntity<StateDto> resp = restTemplate.postForEntity(url, stateDto, StateDto.class);

        if (!resp.getStatusCode().is2xxSuccessful()){

        }

        model.addAttribute("state", resp.getBody());
        return "add-state";
    }

    @PostMapping("/cities/delete")
    public String delete(@RequestParam() Integer id){
        return "redirect:/cities/list";
    }
}
