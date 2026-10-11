package com.smartbite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbite.dto.OrderItemRequest;
import com.smartbite.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TableAndDemoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnCanteenConfigWithTablesAndDemoMode() throws Exception {
        mockMvc.perform(get("/api/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.demoMode", is(true)))
                .andExpect(jsonPath("$.tables", hasItem("T01")))
                .andExpect(jsonPath("$.tables", hasItem("T02")))
                .andExpect(jsonPath("$.tables", hasItem("T10")));
    }

    @Test
    void shouldAllowUnauthenticatedCustomerToPlaceTableOrderInDemoMode() throws Exception {
        OrderRequest request = new OrderRequest(
                "Guest Student",
                "T01",
                List.of(new OrderItemRequest("sb-b1", 1)) // Crispy Masala Dosa 50.0
        );

        MvcResult result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.pickupToken", notNullValue()))
                .andExpect(jsonPath("$.tableId", is("T01")))
                .andExpect(jsonPath("$.customerIdentifier", containsString("Table T01")))
                .andExpect(jsonPath("$.grandTotal", is(50.0)))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("pickupToken").asText();

        // Customer can track order by token unauthenticated
        mockMvc.perform(get("/api/orders/token/" + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickupToken", is(token)))
                .andExpect(jsonPath("$.tableId", is("T01")));
    }

    @Test
    void shouldRejectInvalidTableIdWith400() throws Exception {
        OrderRequest request = new OrderRequest(
                "Guest Student",
                "T99", // Not in T01-T10
                List.of(new OrderItemRequest("sb-b1", 1))
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAllowDemoAdminLoginAndAccessAdminEndpoints() throws Exception {
        MvcResult authResult = mockMvc.perform(post("/api/auth/demo/admin-login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("ADMIN")))
                .andReturn();

        String token = objectMapper.readTree(authResult.getResponse().getContentAsString()).get("token").asText();

        // Access admin dashboard orders with demo token
        mockMvc.perform(get("/api/admin/orders")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Access admin dashboard stats with demo token
        mockMvc.perform(get("/api/admin/stats")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.totalSales", greaterThanOrEqualTo(0.0)));
    }
}
