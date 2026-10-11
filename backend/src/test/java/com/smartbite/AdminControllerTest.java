package com.smartbite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbite.dto.AvailabilityUpdateRequest;
import com.smartbite.dto.MenuItemRequest;
import com.smartbite.dto.OrderItemRequest;
import com.smartbite.dto.OrderRequest;
import com.smartbite.dto.OrderStatusUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithAnonymousUser
    void shouldRejectUnauthenticatedAccessToAdminOrdersWith401() throws Exception {
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void shouldRejectUnauthenticatedAccessToAdminStatsWith401() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "student@smartbite.campus", roles = "STUDENT")
    void shouldRejectStudentAccessToAdminOrdersWith403() throws Exception {
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "student@smartbite.campus", roles = "STUDENT")
    void shouldRejectStudentUpdatingOrderStatusWith403() throws Exception {
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest("PREPARING");
        mockMvc.perform(patch("/api/admin/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "student@smartbite.campus", roles = "STUDENT")
    void shouldRejectStudentUpdatingMenuAvailabilityWith403() throws Exception {
        AvailabilityUpdateRequest request = new AvailabilityUpdateRequest("OUT_OF_STOCK");
        mockMvc.perform(patch("/api/admin/menu/sb-b1/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@smartbite.campus", roles = "ADMIN")
    void shouldReturnRecentOrdersListForAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin@smartbite.campus", roles = "ADMIN")
    void shouldReturnDashboardStatsForAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.pendingOrders", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.preparingOrders", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.readyOrders", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.todayOrders", greaterThanOrEqualTo(0)));
    }

    @Test
    @WithMockUser(username = "admin@smartbite.campus", roles = "ADMIN")
    void shouldUpdateOrderStatusSuccessfully() throws Exception {
        // First create an order
        OrderRequest orderReq = new OrderRequest("21CS202", List.of(new OrderItemRequest("sb-b1", 1)));
        MvcResult createResult = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        long orderId = objectMapper.readTree(responseJson).get("id").asLong();

        // Advance to PREPARING
        OrderStatusUpdateRequest prepRequest = new OrderStatusUpdateRequest("PREPARING");
        mockMvc.perform(patch("/api/admin/orders/" + orderId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prepRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) orderId)))
                .andExpect(jsonPath("$.status", is("PREPARING")));

        // Advance to READY
        OrderStatusUpdateRequest readyRequest = new OrderStatusUpdateRequest("READY");
        mockMvc.perform(patch("/api/admin/orders/" + orderId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(readyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) orderId)))
                .andExpect(jsonPath("$.status", is("READY")));

        // Advance to COMPLETED
        OrderStatusUpdateRequest completedRequest = new OrderStatusUpdateRequest("COMPLETED");
        mockMvc.perform(patch("/api/admin/orders/" + orderId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) orderId)))
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    @WithMockUser(username = "admin@smartbite.campus", roles = "ADMIN")
    void shouldRejectInvalidOrderStatus() throws Exception {
        // Create an order
        OrderRequest orderReq = new OrderRequest("21CS203", List.of(new OrderItemRequest("sb-v2", 1)));
        MvcResult createResult = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andReturn();

        long orderId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Attempt invalid status
        OrderStatusUpdateRequest invalidReq = new OrderStatusUpdateRequest("FLYING_TO_MOON");
        mockMvc.perform(patch("/api/admin/orders/" + orderId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@smartbite.campus", roles = "ADMIN")
    void shouldReturnNotFoundForNonExistentOrderIdOnStatusUpdate() throws Exception {
        OrderStatusUpdateRequest req = new OrderStatusUpdateRequest("READY");
        mockMvc.perform(patch("/api/admin/orders/999999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@smartbite.campus", roles = "ADMIN")
    void shouldUpdateMenuAvailabilityAndPreventOrderingOutOfStockItem() throws Exception {
        String testItemId = "sb-b3"; // Fluffy Poori Bhaji

        // 1. Mark as Out of Stock
        AvailabilityUpdateRequest outOfStockReq = new AvailabilityUpdateRequest("OUT_OF_STOCK");
        mockMvc.perform(patch("/api/admin/menu/" + testItemId + "/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(outOfStockReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testItemId)))
                .andExpect(jsonPath("$.availability", is("Out of Stock")));

        // 2. Check public menu reflection
        mockMvc.perform(get("/api/menu/" + testItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availability", is("Out of Stock")));

        // 3. Attempt placing order with this unavailable item -> should be rejected with 400 Bad Request
        OrderRequest unavailableOrderReq = new OrderRequest("21CS999", List.of(new OrderItemRequest(testItemId, 1)));
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unavailableOrderReq)))
                .andExpect(status().isBadRequest());

        // 4. Mark back as Available
        AvailabilityUpdateRequest availableReq = new AvailabilityUpdateRequest("AVAILABLE");
        mockMvc.perform(patch("/api/admin/menu/" + testItemId + "/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(availableReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testItemId)))
                .andExpect(jsonPath("$.availability", is("Available")));

        // 5. Placing order now succeeds
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unavailableOrderReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pickupToken", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin@smartbite.campus", roles = "ADMIN")
    void shouldRejectInvalidMenuAvailabilityStatus() throws Exception {
        AvailabilityUpdateRequest invalidReq = new AvailabilityUpdateRequest("MAYBE_TOMORROW");
        mockMvc.perform(patch("/api/admin/menu/sb-b1/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@smartbite.campus", roles = "ADMIN")
    void shouldReturnNotFoundForNonExistentMenuItemAvailability() throws Exception {
        AvailabilityUpdateRequest req = new AvailabilityUpdateRequest("OUT_OF_STOCK");
        mockMvc.perform(patch("/api/admin/menu/non-existent-menu-id/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@smartbite.campus", roles = "ADMIN")
    void shouldAllowAdminToAddEditAndDeleteMenuItem() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis() % 10000);
        String customId = "sb-test-" + uniqueSuffix;

        // 1. Admin adds a new menu item
        MenuItemRequest addRequest = new MenuItemRequest(
                customId,
                "Paneer Kathi Roll",
                "snacks",
                "Snacks",
                65.0,
                true,
                "Available",
                4.8,
                "5-7 mins",
                "Warm flaky paratha wrapped with spiced marinated paneer chunks and mint chutney.",
                "https://example.com/paneer-roll.jpg"
        );

        MvcResult createResult = mockMvc.perform(post("/api/admin/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(customId)))
                .andExpect(jsonPath("$.name", is("Paneer Kathi Roll")))
                .andExpect(jsonPath("$.price", is(65.0)))
                .andExpect(jsonPath("$.isVeg", is(true)))
                .andExpect(jsonPath("$.availability", is("Available")))
                .andReturn();

        // 2. Verify item is publicly visible via GET /api/menu/{id}
        mockMvc.perform(get("/api/menu/" + customId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Paneer Kathi Roll")));

        // 3. Admin edits the menu item details and price
        MenuItemRequest updateRequest = new MenuItemRequest(
                customId,
                "Special Jumbo Paneer Roll",
                "snacks",
                "Snacks",
                75.0,
                true,
                "Available",
                4.9,
                "6-8 mins",
                "Extra jumbo paratha stuffed with double paneer and cheese.",
                "https://example.com/paneer-roll-jumbo.jpg"
        );

        mockMvc.perform(put("/api/admin/menu/" + customId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Special Jumbo Paneer Roll")))
                .andExpect(jsonPath("$.price", is(75.0)));

        // 4. Admin deletes the menu item
        mockMvc.perform(delete("/api/admin/menu/" + customId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Menu item deleted successfully")));

        // 5. Verify item no longer exists (404)
        mockMvc.perform(get("/api/menu/" + customId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "student@smartbite.campus", roles = "STUDENT")
    void shouldRejectStudentAddingEditingOrDeletingMenuItemWith403() throws Exception {
        MenuItemRequest req = new MenuItemRequest(
                "sb-hack-1",
                "Hacked Dish",
                "snacks",
                "Snacks",
                1.0,
                true,
                "Available",
                5.0,
                "1 min",
                "Unauthorized modification attempt",
                ""
        );

        // Student attempting POST /api/admin/menu -> 403 Forbidden
        mockMvc.perform(post("/api/admin/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        // Student attempting PUT /api/admin/menu/sb-b1 -> 403 Forbidden
        mockMvc.perform(put("/api/admin/menu/sb-b1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        // Student attempting DELETE /api/admin/menu/sb-b1 -> 403 Forbidden
        mockMvc.perform(delete("/api/admin/menu/sb-b1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void shouldRejectUnauthenticatedAccessToMenuMutationsWith401() throws Exception {
        MenuItemRequest req = new MenuItemRequest(
                "sb-anon-1",
                "Anon Dish",
                "snacks",
                "Snacks",
                10.0,
                true,
                "Available",
                4.5,
                "5 mins",
                "Unauthenticated test",
                ""
        );

        // Unauthenticated POST /api/admin/menu -> 401 Unauthorized
        mockMvc.perform(post("/api/admin/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());

        // Unauthenticated PUT /api/admin/menu/sb-b1 -> 401 Unauthorized
        mockMvc.perform(put("/api/admin/menu/sb-b1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());

        // Unauthenticated DELETE /api/admin/menu/sb-b1 -> 401 Unauthorized
        mockMvc.perform(delete("/api/admin/menu/sb-b1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@smartbite.campus", roles = "ADMIN")
    void shouldValidateAndSanitizeMenuItemInputsOnServer() throws Exception {
        // 1. Missing name -> 400 Bad Request
        MenuItemRequest missingName = new MenuItemRequest(
                null,
                "",
                "snacks",
                "Snacks",
                50.0,
                true,
                "Available",
                4.5,
                "5 mins",
                "Desc",
                ""
        );
        mockMvc.perform(post("/api/admin/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingName)))
                .andExpect(status().isBadRequest());

        // 2. Negative price -> 400 Bad Request
        MenuItemRequest negativePrice = new MenuItemRequest(
                null,
                "Negative Price Item",
                "snacks",
                "Snacks",
                -25.0,
                true,
                "Available",
                4.5,
                "5 mins",
                "Desc",
                ""
        );
        mockMvc.perform(post("/api/admin/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(negativePrice)))
                .andExpect(status().isBadRequest());

        // 3. HTML tags in name and description are sanitized safely
        MenuItemRequest xssAttempt = new MenuItemRequest(
                null,
                "Crispy Puri <script>alert('xss')</script>",
                "snacks",
                "Snacks",
                40.0,
                true,
                "Available",
                4.5,
                "5 mins",
                "Tasty snack with <b>bold</b> flavours <img src=x onerror=alert(1)>",
                ""
        );
        MvcResult sanitizedResult = mockMvc.perform(post("/api/admin/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(xssAttempt)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Crispy Puri alert('xss')")))
                .andReturn();

        // Clean up created sanitized item
        String createdId = objectMapper.readTree(sanitizedResult.getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(delete("/api/admin/menu/" + createdId)).andExpect(status().isOk());
    }
}
