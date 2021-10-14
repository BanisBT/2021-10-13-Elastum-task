package com.tbarauskas.elastumtask.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbarauskas.elastumtask.dto.PersonResponseDTO;
import com.tbarauskas.elastumtask.model.ErrorHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllPersons() throws Exception {
        MvcResult result = mockMvc.perform(get("/persons/"))
                .andExpect(status().isOk())
                .andReturn();

        List<PersonResponseDTO> personDTOList = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertEquals(4, personDTOList.size());
    }

    @Test
    void testGetPerson() throws Exception {
        MvcResult result = mockMvc.perform(get("/persons/{id}", 3L))
                .andExpect(status().isOk())
                .andReturn();

        PersonResponseDTO person = objectMapper.readValue(result.getResponse().getContentAsString(),
                PersonResponseDTO.class);

        assertEquals("Nijole", person.getName());
        assertEquals("Kliutis", person.getSurname());
    }

    @Test
    void testGetPersonIfNotExist() throws Exception {
        MvcResult result = mockMvc.perform(get("/persons/{id}", 101L))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorHandler error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorHandler.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
        assertEquals("Person with id - 101 is not found", error.getMessage());
    }
}
