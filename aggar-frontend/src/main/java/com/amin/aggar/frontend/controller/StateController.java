package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.StateDto;
import com.amin.aggar.frontend.form.StateForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class StateController {
    private static final Logger log = LoggerFactory.getLogger(StateController.class);

    private final String apiUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public StateController(
            RestTemplate restTemplate,
            @Value("${external.api.url:http://localhost:8080/api}") String apiUrl,
            MessageSource messageSource) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
        this.messageSource = messageSource;
    }

    @GetMapping("/states/list")
    public String list(Model model, Locale locale) {
        try {
            ResponseEntity<List<StateDto>> resp = restTemplate.exchange(
                    apiUrl + "/states",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {});

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                model.addAttribute("states", resp.getBody());
            } else {
                model.addAttribute("states", Collections.emptyList());
            }
        } catch (Exception ex) {
            log.error("Failed to fetch states from API", ex);
            model.addAttribute("states", Collections.emptyList());
            model.addAttribute("error", messageSource.getMessage("state.error.load", null, locale));
        }
        return "admin/state-list";
    }

    @GetMapping("/states/add")
    public String addForm(Model model) {
        model.addAttribute("state", new StateForm());
        return "admin/add-state";
    }

    @PostMapping("/states/add")
    public String create(@ModelAttribute StateForm stateForm, Model model,
                         RedirectAttributes redirectAttributes, Locale locale) {
        StateDto stateDto = new StateDto(null, stateForm.getName(), stateForm.getNameAr(), stateForm.getCode());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<StateDto> request = new HttpEntity<>(stateDto, headers);
            ResponseEntity<StateDto> resp = restTemplate.exchange(
                    apiUrl + "/states",
                    HttpMethod.POST,
                    request,
                    StateDto.class
            );

            if (resp.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addAttribute("success", messageSource.getMessage("state.success.add", null, locale));
                return "redirect:/states/list";
            } else {
                model.addAttribute("error", messageSource.getMessage("state.error.add", null, locale));
                model.addAttribute("state", stateForm);
                return "admin/add-state";
            }
        } catch (Exception ex) {
            log.error("Failed to create state", ex);
            model.addAttribute("error", messageSource.getMessage("state.error.add", null, locale));
            model.addAttribute("state", stateForm);
            return "admin/add-state";
        }
    }

    @GetMapping("/states/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model,
                           RedirectAttributes redirectAttributes, Locale locale) {
        try {
            ResponseEntity<StateDto> resp = restTemplate.exchange(
                    apiUrl + "/states/" + id,
                    HttpMethod.GET,
                    null,
                    StateDto.class
            );

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                StateDto stateDto = resp.getBody();
                model.addAttribute("state", new StateForm(stateDto.name(), stateDto.nameAr(), stateDto.code()));
                model.addAttribute("stateId", id);
                return "admin/edit-state";
            } else {
                redirectAttributes.addAttribute("error", messageSource.getMessage("state.error.not-found", null, locale));
                return "redirect:/states/list";
            }
        } catch (Exception ex) {
            log.error("Failed to fetch state {} from API", id, ex);
            redirectAttributes.addAttribute("error", messageSource.getMessage("state.error.load-one", null, locale));
            return "redirect:/states/list";
        }
    }

    @PostMapping("/states/edit/{id}")
    public String update(@PathVariable("id") Integer id, @ModelAttribute StateForm stateForm, Model model,
                         RedirectAttributes redirectAttributes, Locale locale) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            StateDto stateDto = new StateDto(id, stateForm.getName(), stateForm.getNameAr(), stateForm.getCode());
            HttpEntity<StateDto> request = new HttpEntity<>(stateDto, headers);
            ResponseEntity<StateDto> resp = restTemplate.exchange(
                    apiUrl + "/states/" + id,
                    HttpMethod.PUT,
                    request,
                    StateDto.class
            );

            if (resp.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addAttribute("success", messageSource.getMessage("state.success.update", null, locale));
                return "redirect:/states/list";
            } else {
                model.addAttribute("error", messageSource.getMessage("state.error.update", null, locale));
                model.addAttribute("state", stateForm);
                model.addAttribute("stateId", id);
                return "admin/edit-state";
            }
        } catch (Exception ex) {
            log.error("Failed to update state {}", id, ex);
            model.addAttribute("error", messageSource.getMessage("state.error.update", null, locale));
            model.addAttribute("state", stateForm);
            model.addAttribute("stateId", id);
            return "admin/edit-state";
        }
    }

    @PostMapping("/states/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            restTemplate.exchange(
                    apiUrl + "/states/" + id,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            redirectAttributes.addAttribute("success", messageSource.getMessage("state.success.delete", null, locale));
            return "redirect:/states/list";
        } catch (Exception ex) {
            log.error("Failed to delete state {}", id, ex);
            redirectAttributes.addAttribute("error", messageSource.getMessage("state.error.delete", null, locale));
            return "redirect:/states/list";
        }
    }
}