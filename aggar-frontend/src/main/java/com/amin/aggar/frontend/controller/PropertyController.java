package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.CityDto;
import com.amin.aggar.frontend.dto.PropertyCommentDto;
import com.amin.aggar.frontend.dto.PropertyDto;
import com.amin.aggar.frontend.dto.StateDto;
import com.amin.aggar.frontend.form.PropertyForm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Optional;

@Controller
public class PropertyController {

    private static final Logger log = LoggerFactory.getLogger(PropertyController.class);

    private final RestTemplate restTemplate;
    private final String propertiesApiUrl;
    private final String locationApiUrl;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public PropertyController(RestTemplate restTemplate,
                              @Value("${external.api.properties-url:http://localhost:8080/api/properties}") String propertiesApiUrl,
                              @Value("${external.api.location-url:http://localhost:8080/api}") String locationApiUrl,
                              MessageSource messageSource) {
        this.restTemplate = restTemplate;
        this.propertiesApiUrl = propertiesApiUrl;
        this.locationApiUrl = locationApiUrl;
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.messageSource = messageSource;
    }

    @GetMapping("/properties/{identifier}")
    public String details(@PathVariable("identifier") String identifier, Model model,
                          HttpServletResponse response, Locale locale) {
        try {
            PropertyDto property = null;

            // Try to fetch by slug first, then by ID
            try {
                String url = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                        .pathSegment("view")
                        .pathSegment(identifier)
                        .toUriString();

                ResponseEntity<PropertyDto> resp = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        PropertyDto.class
                );

                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    property = resp.getBody();
                }
            } catch (Exception e) {
                log.debug("Property not found by slug: {}, trying by ID", identifier);
            }

            // Fallback: try by ID if slug lookup failed
            if (property == null) {
                try {
                    Long id = Long.parseLong(identifier);
                    String url = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                            .pathSegment(id.toString())
                            .toUriString();

                    ResponseEntity<PropertyDto> resp = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            PropertyDto.class
                    );

                    if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                        property = resp.getBody();
                    }
                } catch (NumberFormatException e) {
                    log.debug("Identifier is not a valid ID: {}", identifier);
                }
            }


            if (property == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                model.addAttribute("listingTypes", Collections.emptyList());
                model.addAttribute("cities", Collections.emptyList());
                return "error/404";
            }

            model.addAttribute("property", property);
            try {
                PropertyComments comments = fetchComments(property.id());
                model.addAttribute("comments", comments.content());
                model.addAttribute("commentsTotal", comments.totalElements());
            } catch (Exception ex) {
                log.error("Failed to fetch comments for property {}", property.id(), ex);
                model.addAttribute("comments", Collections.emptyList());
                model.addAttribute("commentLoadError", messageSource.getMessage(
                        "property.comments.load.error", null, locale));
            }

            try {
                model.addAttribute("similarProperties", fetchSimilarProperties(property));
            } catch (Exception ex) {
                log.warn("Failed to fetch similar properties for property {}", property.id(), ex);
                model.addAttribute("similarProperties", Collections.emptyList());
            }

            // Add required attributes for header fragment
            model.addAttribute("listingTypes", Collections.emptyList());
            model.addAttribute("cities", Collections.emptyList());

            return "property-detail";
        } catch (Exception ex) {
            log.error("Failed to fetch property {} from API {}", identifier, propertiesApiUrl, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            model.addAttribute("listingTypes", Collections.emptyList());
            model.addAttribute("cities", Collections.emptyList());
            return "error/500";
        }
    }

    @PostMapping("/properties/{identifier}/comments")
    public String addComment(@PathVariable("identifier") String identifier,
                             @RequestParam("propertyId") Long propertyId,
                             @RequestParam("content") String content,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        String redirectPath = "/properties/" + identifier;
        Object sessionUser = session.getAttribute(
                com.amin.aggar.frontend.config.GlobalControllerAdvice.SESSION_USER_KEY);
        String accessToken = sessionUser instanceof Map<?, ?> user
                && user.get("accessToken") instanceof String token ? token : null;

        if (accessToken == null || accessToken.isBlank()) {
            String loginUrl = UriComponentsBuilder.fromPath("/login")
                    .queryParam("redirect", redirectPath)
                    .build()
                    .encode()
                    .toUriString();
            return "redirect:" + loginUrl;
        }

        if (content == null || content.isBlank() || content.length() > 2000) {
            redirectAttributes.addFlashAttribute("commentError",
                    messageSource.getMessage("property.comments.validation.error", null, locale));
            return "redirect:" + redirectPath;
        }

        try {
            PropertyCommentDto comment = new PropertyCommentDto(null, null, null, null, content.trim(), null);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.exchange(
                    propertiesApiUrl + "/" + propertyId + "/comments",
                    HttpMethod.POST,
                    new HttpEntity<>(comment, headers),
                    PropertyCommentDto.class);
            redirectAttributes.addFlashAttribute("commentSuccess",
                    messageSource.getMessage("property.comments.success", null, locale));
        } catch (Exception ex) {
            log.error("Failed to add comment for property {}", propertyId, ex);
            redirectAttributes.addFlashAttribute("commentError",
                    messageSource.getMessage("property.comments.submit.error", null, locale));
        }
        return "redirect:" + redirectPath;
    }

    @PostMapping("/properties/{identifier}/messages")
    public String sendPropertyMessage(@PathVariable("identifier") String identifier,
                                      @RequestParam("propertyId") Long propertyId,
                                      @RequestParam("subject") String subject,
                                      @RequestParam("content") String content,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes,
                                      Locale locale) {
        if (!(session.getAttribute(
                com.amin.aggar.frontend.config.GlobalControllerAdvice.SESSION_USER_KEY) instanceof Map<?, ?> user)
                || !(user.get("accessToken") instanceof String token) || token.isBlank()) {
            String loginUrl = UriComponentsBuilder.fromPath("/login")
                    .queryParam("redirect", "/properties/" + identifier)
                    .build()
                    .encode()
                    .toUriString();
            return "redirect:" + loginUrl;
        }
        if (subject == null || subject.isBlank() || subject.length() > 255
                || content == null || content.isBlank()) {
            redirectAttributes.addFlashAttribute("commentError",
                    messageSource.getMessage("property.message.validation.error", null, locale));
            return "redirect:/properties/" + identifier;
        }

        try {
            URI uri = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                    .pathSegment(propertyId.toString(), "messages")
                    .queryParam("subject", subject.trim())
                    .queryParam("content", content.trim())
                    .build()
                    .encode()
                    .toUri();
            restTemplate.exchange(uri, HttpMethod.POST, HttpEntity.EMPTY, String.class);
            redirectAttributes.addFlashAttribute("commentSuccess",
                    messageSource.getMessage("property.message.success", null, locale));
        } catch (Exception ex) {
            log.error("Failed to send message for property {}", propertyId, ex);
            redirectAttributes.addFlashAttribute("commentError",
                    messageSource.getMessage("property.message.submit.error", null, locale));
        }
        return "redirect:/properties/" + identifier;
    }

    private PropertyComments fetchComments(Long propertyId) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                .pathSegment(propertyId.toString(), "comments")
                .queryParam("size", 50)
                .queryParam("sort", "createdAt,desc")
                .build()
                .toUri();
        String response = restTemplate.getForObject(uri, String.class);
        if (response == null) {
            throw new IllegalStateException("Comments API returned an empty response");
        }
        JsonNode pageResponse = objectMapper.readTree(response);
        JsonNode content = pageResponse.path("content");
        JsonNode totalElements = pageResponse.path("totalElements");
        if (!content.isArray() || !totalElements.isIntegralNumber()) {
            throw new IllegalStateException("Comments API returned an invalid page response");
        }
        return new PropertyComments(
                objectMapper.convertValue(content, new TypeReference<List<PropertyCommentDto>>() {}),
                totalElements.longValue());
    }

    private List<PropertyDto> fetchSimilarProperties(PropertyDto property) throws IOException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                .queryParam("page", 0)
                .queryParam("size", 12);
        if (property.listingType() != null && !property.listingType().isBlank()) {
            uriBuilder.queryParam("listingType", property.listingType());
        }
        if (property.category() != null && !property.category().isBlank()) {
            uriBuilder.queryParam("category", property.category());
        }

        String response = restTemplate.getForObject(uriBuilder.build().toUri(), String.class);
        if (response == null) {
            throw new IllegalStateException("Properties API returned an empty similar-properties response");
        }

        JsonNode pageResponse = objectMapper.readTree(response);
        JsonNode content = pageResponse.path("content");
        if (!content.isArray()) {
            throw new IllegalStateException("Properties API returned an invalid similar-properties response");
        }

        return objectMapper.convertValue(content, new TypeReference<List<PropertyDto>>() {}).stream()
                .filter(candidate -> !property.id().equals(candidate.id()))
                .limit(4)
                .toList();
    }

    private record PropertyComments(List<PropertyCommentDto> content, long totalElements) {}

    @GetMapping("/properties")
    public String listProperties(
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "listing", required = false) String listing,
            @RequestParam(value = "minPrice", required = false) Integer minPrice,
            @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "12") int size,
            Model model,
            Locale locale) {

        // Fetch states
        List<StateDto> states = new ArrayList<>();
        try {
            String statesUrl = UriComponentsBuilder.fromUriString(locationApiUrl)
                    .pathSegment("states")
                    .toUriString();
            ResponseEntity<List<StateDto>> statesResp = restTemplate.exchange(
                    statesUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<StateDto>>() {});
            if (statesResp.getStatusCode().is2xxSuccessful() && statesResp.getBody() != null) {
                states = statesResp.getBody();
            }
        } catch (Exception ex) {
            log.error("Failed to fetch states", ex);
        }

        // Fetch all cities (for JavaScript filtering by state)
        List<CityDto> allCities = new ArrayList<>();
        try {
            allCities = fetchAllCities();
        } catch (Exception ex) {
            log.error("Failed to fetch cities", ex);
        }

        // Fetch cities for the selected state (for server-side filtering)
        List<CityDto> cities = new ArrayList<>();
        if (state != null && !state.isEmpty()) {
            cities = allCities.stream()
                    .filter(candidate -> candidate.stateId() != null
                            && candidate.stateId().toString().equals(state))
                    .toList();
        }

        // Fetch properties with filters
        List<PropertyDto> properties = new ArrayList<>();
        int totalElements = 0;
        int totalPages = 0;
        int currentPage = page;

        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                    .queryParamIfPresent("state", Optional.ofNullable(state))
                    .queryParamIfPresent("city", Optional.ofNullable(city))
                    .queryParamIfPresent("category", Optional.ofNullable(category))
                    .queryParamIfPresent("listingType", Optional.ofNullable(listing))
                    .queryParamIfPresent("minPrice", Optional.ofNullable(minPrice))
                    .queryParamIfPresent("maxPrice", Optional.ofNullable(maxPrice))
                    .queryParamIfPresent("order", Optional.ofNullable(order))
                    .queryParamIfPresent("q", Optional.ofNullable(q))
                    .queryParam("page", page)
                    .queryParam("size", size);

            ResponseEntity<String> resp = restTemplate.getForEntity(uriBuilder.toUriString(), String.class);

            String responseBody = resp.getBody();
            if (responseBody != null) {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode.has("content") && !jsonNode.get("content").isNull()) {
                    properties = objectMapper.convertValue(jsonNode.get("content"),
                            new TypeReference<List<PropertyDto>>() {});
                }
                if (jsonNode.has("totalElements")) {
                    totalElements = jsonNode.get("totalElements").asInt();
                }
                if (jsonNode.has("totalPages")) {
                    totalPages = jsonNode.get("totalPages").asInt();
                }
                if (jsonNode.has("number")) {
                    currentPage = jsonNode.get("number").asInt();
                }
            }
        } catch (Exception ex) {
            log.error("Failed to fetch properties from API {}", propertiesApiUrl, ex);
            model.addAttribute("error", messageSource.getMessage("property.error.load", null, locale));
        }

        // Get state and city names for display
        String selectedStateName = null;
        String selectedCityName = null;

        if (state != null && !states.isEmpty()) {
            selectedStateName = states.stream()
                    .filter(s -> s.id() != null && s.id().toString().equals(state))
                    .map(s -> "ar".equals(locale.getLanguage())
                            && s.nameAr() != null && !s.nameAr().isBlank()
                            ? s.nameAr()
                            : s.name())
                    .findFirst()
                    .orElse(null);
        }

        if (city != null && !allCities.isEmpty()) {
            selectedCityName = allCities.stream()
                    .filter(c -> c.id() != null && c.id().toString().equals(city))
                    .map(CityDto::name)
                    .findFirst()
                    .orElse(null);
        }

        // Build listing types from properties
        List<String> listingTypes = properties.stream()
                .map(PropertyDto::listingType)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("properties", properties);
        model.addAttribute("states", states);
        model.addAttribute("cities", cities);
        model.addAttribute("allCities", allCities);
        model.addAttribute("listingTypes", listingTypes.isEmpty() ? Collections.emptyList() : listingTypes);

        // Selected filters
        model.addAttribute("selectedState", state);
        model.addAttribute("selectedStateName", selectedStateName);
        model.addAttribute("selectedCity", city);
        model.addAttribute("selectedCityName", selectedCityName);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedListing", listing);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("selectedOrder", order);
        model.addAttribute("q", q);

        // Pagination
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);

        return "properties";
    }

    private List<CityDto> fetchAllCities() throws java.io.IOException {
        List<CityDto> cities = new ArrayList<>();
        for (int page = 0; ; page++) {
            String citiesUrl = UriComponentsBuilder.fromUriString(locationApiUrl)
                    .pathSegment("cities")
                    .queryParam("page", page)
                    .queryParam("size", 100)
                    .toUriString();
            String response = restTemplate.getForObject(citiesUrl, String.class);
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

    @GetMapping("/properties/add")
    public String addPropertyForm(Model model) {
        model.addAttribute("propertyForm", new PropertyForm());
        populateLocationOptions(model, null);
        model.addAttribute("listingTypes", Collections.emptyList());
        return "add-property";
    }

    @GetMapping("/properties/cities")
    @ResponseBody
    public List<CityDto> citiesForState(@RequestParam("stateId") Integer stateId) {
        String url = UriComponentsBuilder.fromUriString(locationApiUrl)
                .pathSegment("cities", "by-state", stateId.toString())
                .toUriString();
        ResponseEntity<List<CityDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    @PostMapping("/properties/add")
    public String addProperty(@ModelAttribute("propertyForm") PropertyForm property,
                             BindingResult result,
                             @RequestParam(value = "images", required = false) MultipartFile[] images,
                             Model model,
                             Locale locale) {
        try {
            List<MultipartFile> selectedImages = images == null ? Collections.emptyList()
                    : java.util.Arrays.stream(images).filter(image -> !image.isEmpty()).toList();
            if (selectedImages.size() > 8) {
                return showPropertyCreationError(property, model, "property.images.too.many", locale);
            }
            if (selectedImages.stream().anyMatch(image -> image.getSize() > 10L * 1024 * 1024)) {
                return showPropertyCreationError(property, model, "property.images.too.large", locale);
            }
            if (selectedImages.stream().anyMatch(image -> !isSupportedImage(image.getContentType()))) {
                return showPropertyCreationError(property, model, "property.images.invalid.type", locale);
            }

            // Basic validation
            if (property.getTitle() == null || property.getTitle().trim().isEmpty()) {
                result.rejectValue("title", "error.property",
                        messageSource.getMessage("property.validation.title", null, locale));
            }
            if (property.getListingType() == null) {
                result.rejectValue("listingType", "error.property",
                        messageSource.getMessage("property.validation.listing-type", null, locale));
            }
            if (property.getPrice() == null || property.getPrice().doubleValue() <= 0) {
                result.rejectValue("price", "error.property",
                        messageSource.getMessage("property.validation.price", null, locale));
            }
            if (property.getStateId() == null) {
                result.rejectValue("stateId", "error.property",
                        messageSource.getMessage("property.validation.state", null, locale));
            }
            if (property.getCityId() == null) {
                result.rejectValue("cityId", "error.property",
                        messageSource.getMessage("property.validation.city", null, locale));
            }

            if (result.hasErrors()) {
                populateLocationOptions(model, property.getStateId());
                return "add-property";
            }

            convertNeighborhoodNameToId(property);

            // Force status to "pending" for new properties (admin approval required)
            property.setStatus("pending");

            // Create headers for JSON request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create request entity
            HttpEntity<PropertyDto> request = new HttpEntity<>(property.toDto(), headers);

            // Call backend API to create property
            ResponseEntity<PropertyDto> response = restTemplate.exchange(
                    propertiesApiUrl,
                    HttpMethod.POST,
                    request,
                    PropertyDto.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                PropertyDto createdProperty = response.getBody();
                if (createdProperty == null
                        || ((createdProperty.slug() == null || createdProperty.slug().isBlank())
                        && createdProperty.id() == null)) {
                    log.error("Property API returned success without a property identifier");
                    return showPropertyCreationError(
                            property, model, "property.error.add", locale);
                }
                String redirectIdentifier = createdProperty.slug() != null
                        && !createdProperty.slug().isBlank()
                        ? createdProperty.slug()
                        : createdProperty.id().toString();
                if (!selectedImages.isEmpty()) {
                    if (createdProperty.id() == null) {
                        log.error("Created property has no ID; images cannot be uploaded");
                        return showPropertyCreationError(
                                property, model, "property.error.images.upload", locale);
                    }
                    try {
                        uploadPropertyImages(createdProperty.id(), selectedImages);
                    } catch (RestClientException ex) {
                        log.error("Property {} was created but image upload failed",
                                createdProperty.id(), ex);
                        String language = locale.getLanguage();
                        model.addAttribute("createdPropertyUrl",
                                "/" + (language.equals("en") ? "en" : "ar") + "/properties/" + redirectIdentifier);
                        return showPropertyCreationError(
                                property, model, "property.error.images.upload", locale);
                    }
                }
                return "redirect:/properties/" + redirectIdentifier;
            } else {
                return showPropertyCreationError(
                        property, model, "property.error.add", locale);
            }

        } catch (RestClientException ex) {
            log.error("Failed to create property", ex);
            return showPropertyCreationError(
                    property, model, "property.error.service-unavailable", locale);
        }
    }

    private void uploadPropertyImages(Long propertyId, List<MultipartFile> images) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        images.forEach(image -> body.add("files", image.getResource()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        String uploadUrl = UriComponentsBuilder.fromUriString(locationApiUrl)
                .pathSegment("property-images", "upload")
                .queryParam("propertyId", propertyId)
                .toUriString();
        restTemplate.exchange(uploadUrl, HttpMethod.POST, request, new ParameterizedTypeReference<List<String>>() {});
    }

    private boolean isSupportedImage(String contentType) {
        return contentType != null && switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg", "image/png", "image/gif", "image/webp", "image/avif", "image/bmp" -> true;
            default -> false;
        };
    }

    private String showPropertyCreationError(PropertyForm property, Model model, String messageKey, Locale locale) {
        model.addAttribute("error", messageSource.getMessage(messageKey, null, locale));
        model.addAttribute("listingTypes", Collections.emptyList());
        try {
            populateLocationOptions(model, property.getStateId());
        } catch (RestClientException ex) {
            log.warn("Unable to reload location options after property creation failed", ex);
            model.addAttribute("states", Collections.emptyList());
            model.addAttribute("cities", Collections.emptyList());
        }
        return "add-property";
    }

    private void convertNeighborhoodNameToId(PropertyForm property) {
        if (property.getNeighborhood() != null && !property.getNeighborhood().trim().isEmpty()) {
            switch (property.getNeighborhood().toLowerCase()) {
                case "beverly hills": property.setNeighborhoodId(1); break;
                case "manhattan": property.setNeighborhoodId(2); break;
                case "brooklyn": property.setNeighborhoodId(3); break;
                default: property.setNeighborhoodId(null); // Optional field
            }
        }

        if (property.getStatus() == null || property.getStatus().trim().isEmpty()) {
            property.setStatus("pending");
        }
    }

    private void populateLocationOptions(Model model, Integer selectedStateId) {
        String statesUrl = UriComponentsBuilder.fromUriString(locationApiUrl)
                .pathSegment("states")
                .toUriString();
        ResponseEntity<List<StateDto>> statesResponse = restTemplate.exchange(
                statesUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        model.addAttribute("states", statesResponse.getBody() != null
                ? statesResponse.getBody() : Collections.emptyList());

        List<CityDto> cities = selectedStateId == null
                ? Collections.emptyList()
                : citiesForState(selectedStateId);
        model.addAttribute("cities", cities);
    }

    // Admin: List pending properties for approval
    @GetMapping("/admin/properties")
    public String listPendingProperties(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model,
            Locale locale) {

        try {
            String url = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                    .pathSegment("admin", "pending")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .toUriString();

            ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
            List<PropertyDto> properties = new ArrayList<>();
            int totalElements = 0;
            int totalPages = 0;

            if (resp.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(resp.getBody());
                if (jsonNode.has("content") && !jsonNode.get("content").isNull()) {
                    properties = objectMapper.convertValue(jsonNode.get("content"),
                            new TypeReference<List<PropertyDto>>() {});
                }
                if (jsonNode.has("totalElements")) {
                    totalElements = jsonNode.get("totalElements").asInt();
                }
                if (jsonNode.has("totalPages")) {
                    totalPages = jsonNode.get("totalPages").asInt();
                }
            }

            model.addAttribute("properties", properties);
            model.addAttribute("totalElements", totalElements);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", page);

        } catch (Exception ex) {
            log.error("Failed to fetch pending properties from API", ex);
            model.addAttribute("properties", Collections.emptyList());
            model.addAttribute("error", messageSource.getMessage("admin.properties.error.load", null, locale));
        }

        return "admin/properties";
    }

    // Admin: Approve a property
    @PostMapping("/admin/properties/{id}/approve")
    public String approveProperty(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            String url = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                    .pathSegment("admin")
                    .pathSegment(id.toString())
                    .pathSegment("approve")
                    .toUriString();

            ResponseEntity<PropertyDto> response = restTemplate.exchange(
                    url, HttpMethod.PUT, null, PropertyDto.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("success",
                        messageSource.getMessage("admin.properties.success.approve", null, locale));
            } else {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("admin.properties.error.approve", null, locale));
            }
        } catch (Exception ex) {
            log.error("Failed to approve property {}", id, ex);
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("admin.properties.error.approve", null, locale));
        }
        return "redirect:/admin/properties";
    }

    // Admin: Reject a property
    @PostMapping("/admin/properties/{id}/reject")
    public String rejectProperty(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            String url = UriComponentsBuilder.fromUriString(propertiesApiUrl)
                    .pathSegment("admin")
                    .pathSegment(id.toString())
                    .pathSegment("reject")
                    .toUriString();

            ResponseEntity<PropertyDto> response = restTemplate.exchange(
                    url, HttpMethod.PUT, null, PropertyDto.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("success",
                        messageSource.getMessage("admin.properties.success.reject", null, locale));
            } else {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("admin.properties.error.reject", null, locale));
            }
        } catch (Exception ex) {
            log.error("Failed to reject property {}", id, ex);
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("admin.properties.error.reject", null, locale));
        }
        return "redirect:/admin/properties";
    }
}
