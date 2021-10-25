package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.entity.Person;
import com.tbarauskas.elastumtask.exception.PersonNotFoundException;
import com.tbarauskas.elastumtask.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private Person person;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private NameSymbolService nameService;

    @InjectMocks
    private PersonService personService;

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

    @Test
    void testGetAllPersonsNoParam() {
        personService.getAllPersons(null);

        verify(personRepository, times(1)).findAll();
    }

    @Test
    void testGetAllPersonsParamName() {
        personService.getAllPersons("name");

        verify(personRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "name"));
        verify(personRepository, times(0)).findAll(Sort.by(Sort.Direction.ASC, "surname"));
        verify(personRepository, times(0)).findAll(Sort.by(Sort.Direction.ASC, "birthDate"));
        verify(personRepository, times(0)).findAll();
    }

    @Test
    void testGetAllPersonsParamSurname() {
        personService.getAllPersons("surname");

        verify(personRepository, times(0)).findAll(Sort.by(Sort.Direction.ASC, "name"));
        verify(personRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "surname"));
        verify(personRepository, times(0)).findAll(Sort.by(Sort.Direction.ASC, "birthDate"));
        verify(personRepository, times(0)).findAll();
    }

    @Test
    void testGetAllPersonsParamBirthDate() {
        personService.getAllPersons("birthDate");

        verify(personRepository, times(0)).findAll(Sort.by(Sort.Direction.ASC, "name"));
        verify(personRepository, times(0)).findAll(Sort.by(Sort.Direction.ASC, "surname"));
        verify(personRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "birthDate"));
        verify(personRepository, times(0)).findAll();
    }

    @Test
    void testCreatePersonValidNameAndSurname() {
        personService.createPerson(person);

        verify(personRepository, times(1)).save(person);
    }

    @Test
    void updatePerson() {
        Person tempPerson = new Person();
        when(personRepository.getPersonById(11L)).thenReturn(Optional.of(person));

        personService.updatePerson(11L, tempPerson);

        verify(personRepository, times(1)).save(person);
    }
}
