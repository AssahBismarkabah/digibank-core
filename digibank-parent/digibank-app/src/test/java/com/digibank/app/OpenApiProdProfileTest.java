package com.digibank.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "prod"})
class OpenApiProdProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldNotExposeSwaggerUiInProdProfile() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotExposeOpenApiMetadataInProdProfile() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isNotFound());
    }
}
