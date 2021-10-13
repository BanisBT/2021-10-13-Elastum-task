package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.entity.Person;
import com.tbarauskas.elastumtask.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAllPersons(){
        return personRepository.findAll();
    }
}
