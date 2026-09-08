package com.corepayments.accountservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // disables the synthetic data seeder for a clean test DB
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAndFetchAccount_endToEnd() throws Exception {
        String createBody = """
                {
                  "customerName": "Integration Test User",
                  "accountType": "SAVINGS",
                  "openingBalance": 1000
                }
                """;

        mockMvc.perform(post("/accounts")
                        .contentType("application/json")
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("Integration Test User"))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void debit_beyondBalance_returnsUnprocessableEntity() throws Exception {
        String createBody = """
                {
                  "customerName": "Low Balance User",
                  "accountType": "CURRENT",
                  "openingBalance": 50
                }
                """;

        String response = mockMvc.perform(post("/accounts")
                        .contentType("application/json")
                        .content(createBody))
                .andReturn().getResponse().getContentAsString();

        Long id = com.jayway.jsonpath.JsonPath.read(response, "$.id").toString().equals("null")
                ? null : Long.valueOf(com.jayway.jsonpath.JsonPath.read(response, "$.id").toString());

        mockMvc.perform(post("/accounts/" + id + "/debit")
                        .contentType("application/json")
                        .content("{\"amount\": 200}"))
                .andExpect(status().isUnprocessableEntity());
    }
}