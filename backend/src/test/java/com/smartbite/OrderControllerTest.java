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

import java.util.Collections;
import java.util.List;

import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "21CS101", roles = "STUDENT")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOrderSuccessfullyWithCalculatedTotal() throws Exception {
        // sb-b1 is Crispy Masala Dosa (price: 50.0)
        // sb-v2 is Special Masala Cutting Chai (price: 20.0)
        // Total should be (50.0 * 2) + (20.0 * 1) = 120.0
        OrderRequest request = new OrderRequest(
                "21CS101",
                List.of(
                        new OrderItemRequest("sb-b1", 2),
                        new OrderItemRequest("sb-v2", 1)
                )
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.pickupToken", notNullValue()))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.customerIdentifier", is("21CS101")))
                .andExpect(jsonPath("$.grandTotal", is(120.0)))
                .andExpect(jsonPath("$.items.length()", is(2)))
                .andExpect(jsonPath("$.items[0].itemName", is("Crispy Masala Dosa")))
                .andExpect(jsonPath("$.items[0].unitPrice", is(50.0)))
                .andExpect(jsonPath("$.items[0].quantity", is(2)))
                .andExpect(jsonPath("$.items[0].lineTotal", is(100.0)))
                .andExpect(jsonPath("$.items[1].itemName", is("Special Masala Cutting Chai")))
                .andExpect(jsonPath("$.items[1].unitPrice", is(20.0)))
                .andExpect(jsonPath("$.items[1].quantity", is(1)))
                .andExpect(jsonPath("$.items[1].lineTotal", is(20.0)));
    }

    @Test
    void shouldRejectOrderWithEmptyItems() throws Exception {
        OrderRequest request = new OrderRequest("21CS101", Collections.emptyList());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectOrderWithUnknownMenuItem() throws Exception {
        OrderRequest request = new OrderRequest(
                "21CS101",
                List.of(new OrderItemRequest("invalid-item-xyz", 1))
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectOrderWithZeroOrNegativeQuantity() throws Exception {
        OrderRequest request = new OrderRequest(
                "21CS101",
                List.of(new OrderItemRequest("sb-b1", 0))
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectOrderWithExcessiveQuantity() throws Exception {
        OrderRequest request = new OrderRequest(
                "21CS101",
                List.of(new OrderItemRequest("sb-b1", 999))
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRetrieveOrderByIdAndToken() throws Exception {
        // Place an order first
        OrderRequest request = new OrderRequest(
                "21CS102",
                List.of(new OrderItemRequest("sb-b4", 1)) // Indori Poha price: 30.0
        );

        String responseBody = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(responseBody);
        long orderId = jsonNode.get("id").asLong();
        String token = jsonNode.get("pickupToken").asText();

        // Retrieve by ID
        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) orderId)))
                .andExpect(jsonPath("$.grandTotal", is(30.0)));

        // Retrieve by Token
        mockMvc.perform(get("/api/orders/token/" + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pickupToken", is(token)))
                .andExpect(jsonPath("$.grandTotal", is(30.0)));
    }

    @Test
    void shouldRetrieveOrdersByCustomerPathAndQueryParam() throws Exception {
        String testStudent = "21CS999";
        OrderRequest request = new OrderRequest(
                testStudent,
                List.of(new OrderItemRequest("sb-s1", 1)) // Veg Cheese Grilled Sandwich (60.0)
        );

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Test path lookup: /api/orders/customer/{customerIdentifier}
        mockMvc.perform(get("/api/orders/customer/" + testStudent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].customerIdentifier", is(testStudent)))
                .andExpect(jsonPath("$[0].grandTotal", is(60.0)));

        // Test case-insensitive lookup
        mockMvc.perform(get("/api/orders/customer/21cs999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].customerIdentifier", is(testStudent)));

        // Test query param lookup: /api/orders?customerIdentifier=...
        mockMvc.perform(get("/api/orders").param("customerIdentifier", testStudent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].customerIdentifier", is(testStudent)));
    }

    @Test
    void shouldReturnEmptyListForCustomerWithNoOrders() throws Exception {
        mockMvc.perform(get("/api/orders/customer/NO_ORDERS_STUDENT_XYZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    void shouldReturnBadRequestForInvalidOrderId() throws Exception {
        mockMvc.perform(get("/api/orders/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForNonExistentOrderAndToken() throws Exception {
        mockMvc.perform(get("/api/orders/9999999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/orders/token/TOKEN_NOT_EXIST_XYZ"))
                .andExpect(status().isNotFound());
    }
}
