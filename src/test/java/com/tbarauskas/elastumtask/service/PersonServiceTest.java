package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.entity.Person;
import com.tbarauskas.elastumtask.exception.PersonNotFoundException;
import com.tbarauskas.elastumtask.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private Person person;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @Test
    void testGetAllPersons() {
        personService.getAllPersons();

        verify(personRepository, times(1)).findAll();
    }

    @Test
    void testGetPersonById() {
        when(personRepository.getPersonById(2L)).thenReturn(Optional.of(person));

        personService.getPersonById(2L);

        verify(personRepository, times(1)).getPersonById(2L);
    }

    @Test
    void testGetPersonByIdIfNotExist() {
        when(personRepository.getPersonById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.getPersonById(101L));
    }
}
