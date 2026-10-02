package com.amin.aggar.frontend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StateTemplateRenderingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void localizedStateAddPageRendersFromAdminTemplate() throws Exception {
        mockMvc.perform(get("/ar/states/add"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"stateForm\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("action=\"/ar/states/add\"")));
    }
}
