package com.amin.aggar.frontend.controller;

import com.amin.aggar.frontend.dto.PropertyDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PropertyCommentsTemplateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestTemplate restTemplate;

    private PropertyDto property(String category) {
        return new PropertyDto(
                12L, "Test Home", "test-home", null, java.math.BigDecimal.TEN, null, "USD", "sale",
                category, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null);
    }

    @Test
    void propertyDetailShowsCommentSectionAndRequiresSignInToPost() throws Exception {
        when(restTemplate.exchange(
                contains("/view/test-home"), eq(HttpMethod.GET), isNull(), eq(PropertyDto.class)))
                .thenReturn(ResponseEntity.ok(property(null)));
        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenReturn("{\"content\":[],\"totalElements\":0,\"last\":true}");

        mockMvc.perform(get("/en/properties/test-home"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Comments")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sign in to leave a comment.")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"comments\"")));
    }

    @Test
    void signedInUserCanSeeCommentForm() throws Exception {
        when(restTemplate.exchange(
                contains("/view/test-home"), eq(HttpMethod.GET), isNull(), eq(PropertyDto.class)))
                .thenReturn(ResponseEntity.ok(property(null)));
        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenReturn("{\"content\":[],\"totalElements\":0,\"last\":true}");

        mockMvc.perform(get("/en/properties/test-home")
                        .sessionAttr("loggedInUser", Map.of(
                                "id", 7L,
                                "name", "Jane",
                                "role", "user",
                                "accessToken", "test-token",
                                "expiresAt", Instant.now().plusSeconds(600).toString())))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"commentContent\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "action=\"/en/properties/test-home/comments\"")));
    }

    @Test
    void propertyDetailsShowsSimilarPropertiesBelowComments() throws Exception {
        when(restTemplate.exchange(
                contains("/view/test-home"), eq(HttpMethod.GET), isNull(), eq(PropertyDto.class)))
                .thenReturn(ResponseEntity.ok(property("house")));
        when(restTemplate.getForObject(any(URI.class), eq(String.class)))
                .thenReturn("{\"content\":[],\"totalElements\":0,\"last\":true}");
        when(restTemplate.getForObject(
                argThat(uri -> "/api/properties".equals(uri.getPath())), eq(String.class)))
                .thenReturn("""
                        {"content":[
                          {"id":12,"slug":"test-home","title":"Test Home","price":10,"currency":"USD"},
                          {"id":13,"slug":"related-home","title":"Related Home","price":20,"currency":"USD"}
                        ],"last":true}
                        """);

        String rendered = mockMvc.perform(get("/en/properties/test-home"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Similar properties")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Related Home")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "href=\"/en/properties/related-home\"")))
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertTrue(
                rendered.indexOf("id=\"comments\"") < rendered.indexOf("id=\"similar-properties\""));
    }

    @Test
    void anonymousCommentSubmissionRedirectsToSignIn() throws Exception {
        mockMvc.perform(post("/en/properties/test-home/comments")
                        .param("propertyId", "12")
                        .param("content", "A useful comment")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString("/en/login?redirect=")));
    }

    @Test
    void adminAreaRequiresAdminSessionRole() throws Exception {
        mockMvc.perform(get("/en/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString("/en/login?redirect=")));

        Map<String, Object> member = Map.of(
                "id", 7L, "name", "Jane", "role", "owner", "accessToken", "test-token",
                "expiresAt", Instant.now().plusSeconds(600).toString());
        mockMvc.perform(get("/en/admin").sessionAttr("loggedInUser", member))
                .andExpect(status().isForbidden());
    }
}
