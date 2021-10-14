package com.tbarauskas.elastumtask.repository;

import com.tbarauskas.elastumtask.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
}
