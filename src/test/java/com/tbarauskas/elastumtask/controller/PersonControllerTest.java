package com.tbarauskas.elastumtask.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbarauskas.elastumtask.dto.PersonResponseDTO;
import com.tbarauskas.elastumtask.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
                .andExpect(status().isNoContent())
                .andReturn();

        List<PersonResponseDTO> personDTOList = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertEquals(4, personDTOList.size());
    }
}
