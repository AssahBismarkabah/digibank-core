package com.digibank.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityHeadersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void gatewayResponsesIncludeContentTypeProtectionHeader() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    @Test
    void businessApiRequiresAuthenticationAndDoesNotCreateSession() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    @Test
    void userCanAccessBusinessApiButCannotAccessCompliance() throws Exception {
        mockMvc.perform(get("/api/customers").with(httpBasic("user", "changeit-user")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/compliance").with(httpBasic("user", "changeit-user")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessComplianceAndResponsesAreNotCacheable() throws Exception {
        mockMvc.perform(get("/api/compliance").with(httpBasic("admin", "changeit-admin")))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store, private"));
    }
}
