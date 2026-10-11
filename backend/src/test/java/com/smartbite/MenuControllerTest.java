package com.smartbite;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMenuItemsShouldReturnSeededItems() throws Exception {
        mockMvc.perform(get("/api/menu")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(16))))
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].price").isNumber())
                .andExpect(jsonPath("$[0].isVeg").isBoolean());
    }

    @Test
    void getMenuItemsByCategoryShouldFilterCorrectly() throws Exception {
        mockMvc.perform(get("/api/menu")
                .param("category", "breakfast")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].category").value("breakfast"));
    }

    @Test
    void getMenuItemByIdShouldReturnSingleItem() throws Exception {
        mockMvc.perform(get("/api/menu/sb-b1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("sb-b1"))
                .andExpect(jsonPath("$.name").value("Crispy Masala Dosa"))
                .andExpect(jsonPath("$.price").value(50.0))
                .andExpect(jsonPath("$.isVeg").value(true));
    }

    @Test
    void getMenuItemByIdNotFoundShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/menu/unknown-item-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
