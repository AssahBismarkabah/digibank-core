package com.digibank.gateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;

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

    @Autowired
    private RestClient.Builder restClientBuilder;

    private MockRestServiceServer downstream;

    @BeforeEach
    void mockDownstreamServices() {
        downstream = MockRestServiceServer.bindTo(restClientBuilder).build();
    }

    private void expectCustomerRead() {
        downstream.expect(requestTo("http://localhost:8081/api/customers"))
                .andExpect(method(GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
    }

    private void expectComplianceRead() {
        downstream.expect(requestTo("http://localhost:8084/api/compliance"))
                .andExpect(method(GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
    }

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
        expectCustomerRead();
        mockMvc.perform(get("/api/customers").with(httpBasic("user", "changeit-user")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/compliance").with(httpBasic("user", "changeit-user")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessComplianceAndResponsesAreNotCacheable() throws Exception {
        expectComplianceRead();
        mockMvc.perform(get("/api/compliance").with(httpBasic("admin", "changeit-admin")))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store, private"));
    }
}
