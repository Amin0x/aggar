package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.NeighborhoodDto;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NeighborhoodControllerTest {

    @Test
    void listReadsNeighborhoodsFromEveryApiPage() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenReturn("""
                        {"content":[{"id":1,"name":"North","cityName":"City A"}],"last":false}
                        """)
                .thenReturn("""
                        {"content":[{"id":2,"name":"South","cityName":"City B"}],"last":true}
                        """);
        NeighborhoodController controller = new NeighborhoodController(
                restTemplate, "http://localhost:8080/api", mock(MessageSource.class));
        ExtendedModelMap model = new ExtendedModelMap();

        controller.list(null, model, Locale.forLanguageTag("ar"));

        @SuppressWarnings("unchecked")
        List<NeighborhoodDto> neighborhoods = (List<NeighborhoodDto>) model.get("neighborhoods");
        assertEquals(List.of("North", "South"),
                neighborhoods.stream().map(NeighborhoodDto::name).toList());
    }
}
