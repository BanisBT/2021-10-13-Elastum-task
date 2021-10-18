package com.tbarauskas.elastumtask.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbarauskas.elastumtask.dto.PersonCreateResponseDTO;
import com.tbarauskas.elastumtask.dto.PersonRequestDTO;
import com.tbarauskas.elastumtask.dto.PersonResponseDTO;
import com.tbarauskas.elastumtask.entity.Person;
import com.tbarauskas.elastumtask.model.ErrorHandler;
import com.tbarauskas.elastumtask.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonControllerTest {

    private final LocalDate date = LocalDate.of(1999, 1, 14);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    @Test
    void testGetAllPersons() throws Exception {
        MvcResult result = mockMvc.perform(get("/persons/list"))
                .andExpect(status().isOk())
                .andReturn();

        List<PersonResponseDTO> personDTOList = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertEquals(19, personDTOList.size());
    }

    @Test
    void testGetPerson() throws Exception {
        MvcResult result = mockMvc.perform(get("/persons/{id}", 4L))
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

    @Test
    void testCreatePerson() throws Exception {
        PersonRequestDTO personDTO = new PersonRequestDTO("Aldona", "Gervuogiene", date);

        MvcResult result = mockMvc.perform(post("/persons/create")
                .content(objectMapper.writeValueAsString(personDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        PersonCreateResponseDTO person = objectMapper.readValue(result.getResponse().getContentAsString(),
                PersonCreateResponseDTO.class);

        Person personFromDb = personRepository.getPersonById(person.getId()).orElse(null);

        assert personFromDb != null;
        assertEquals("Aldona", personFromDb.getName());
        assertEquals("Gervuogiene", personFromDb.getSurname());
        assertEquals(date, personFromDb.getBirthDate());
    }

    @Test
    void testCreatePersonNotValidNameBySymbols() throws Exception {
        PersonRequestDTO personDTO = new PersonRequestDTO("Jurgis8", "Jurgelenas", date);

        MvcResult result = mockMvc.perform(post("/persons/create")
                .content(objectMapper.writeValueAsString(personDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorHandler error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorHandler.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Name can be just from letters and space if it's double name", error.getMessage());
    }

    @Test
    void testCreatePersonNotValidNameByLength() throws Exception {
        PersonRequestDTO personDTO = new PersonRequestDTO("J", "Jurgelenas", date);

        MvcResult result = mockMvc.perform(post("/persons/create")
                .content(objectMapper.writeValueAsString(personDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorHandler error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorHandler.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Letters interval for name is from 2 to 50", error.getMessage());
    }

    @Test
    void testCreatePersonNotValidSurnameBySymbols() throws Exception {
        PersonRequestDTO personDTO = new PersonRequestDTO("Jurgita", "Jurgelenaite Meidiene", date);

        MvcResult result = mockMvc.perform(post("/persons/create")
                .content(objectMapper.writeValueAsString(personDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorHandler error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorHandler.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Surname can be just from letters and symbol '-' if it's double surname", error.getMessage());
    }

    @Test
    void testCreatePersonNotValidSurnameBySymbolsNumber() throws Exception {
        PersonRequestDTO personDTO = new PersonRequestDTO("Jurgita", "Jurgelenaite-Meidiene-Justa", date);

        MvcResult result = mockMvc.perform(post("/persons/create")
                .content(objectMapper.writeValueAsString(personDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorHandler error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorHandler.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Surname can't be more then two words and no '-' symbol allowed in front or end of surname",
                error.getMessage());
    }

    @Test
    void testCreatePersonNotValidNameBySymbolsNumber() throws Exception {
        PersonRequestDTO personDTO = new PersonRequestDTO("Jurgita Paulina Matilda",
                "Jurgelenaite-Meidiene", date);

        MvcResult result = mockMvc.perform(post("/persons/create")
                .content(objectMapper.writeValueAsString(personDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorHandler error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorHandler.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), error.getStatus());
        assertEquals("Name can't be more then two words and no spacing allowed in front or end of name",
                error.getMessage());
    }
}
