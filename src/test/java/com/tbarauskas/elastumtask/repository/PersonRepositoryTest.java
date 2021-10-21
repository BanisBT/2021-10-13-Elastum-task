package com.tbarauskas.elastumtask.repository;

import com.tbarauskas.elastumtask.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void testGetPersonById() {
        Person person = personRepository.getPersonById(1L).orElse(null);

        assert person != null;
        assertEquals(1L, person.getId());
    }

    @Test
    void testGetPersonByBirthDateBetweenAndSurname() {
        LocalDate date = LocalDate.of(2077, 1, 1);
        Person person = personRepository.getPersonByBirthDateBetweenAndSurname(date, date.plusMonths(11), "right")
                .orElse(null);

        assert person != null;
        assertEquals("right", person.getSurname());
        assertTrue(date.isBefore(person.getBirthDate()));
        assertTrue(date.plusMonths(11).isAfter(person.getBirthDate()));
    }

    @Test
    void testGetPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase() {
        LocalDate date = LocalDate.of(2077, 1, 1);
        List<Person> personList = personRepository.getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(date,
                date.plusMonths(10), "right");

        assertEquals(2, personList.size());
    }

    @Test
    void testGetPersonsByBirthDateBetweenAndSurname() {
        LocalDate date = LocalDate.of(2077, 1, 1);
        List<Person> personList = personRepository.getPersonsByBirthDateBetweenAndSurname(date,
                date.plusMonths(10), "right");

        assertEquals(1, personList.size());
    }
}
