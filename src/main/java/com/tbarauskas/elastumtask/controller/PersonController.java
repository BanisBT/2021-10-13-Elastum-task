package com.tbarauskas.elastumtask.controller;

import com.tbarauskas.elastumtask.dto.PersonCreateResponseDTO;
import com.tbarauskas.elastumtask.dto.PersonRelativeDTO;
import com.tbarauskas.elastumtask.dto.PersonRequestDTO;
import com.tbarauskas.elastumtask.dto.PersonResponseDTO;
import com.tbarauskas.elastumtask.entity.Person;
import com.tbarauskas.elastumtask.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    private final ModelMapper modelMapper;

    public PersonController(PersonService personService, ModelMapper modelMapper) {
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PersonResponseDTO getPerson(@PathVariable Long id){
        return modelMapper.map(personService.getPersonById(id), PersonResponseDTO.class);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<PersonResponseDTO> getAllPersons(){
        return personService.getAllPersons().stream()
                .map(person -> modelMapper.map(person, PersonResponseDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/relative/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<PersonRelativeDTO> getPersonsRelatives(@PathVariable Long id){
        return personService.getPersonsRelatives(id).stream()
                .map(person -> modelMapper.map(person, PersonRelativeDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonCreateResponseDTO createPerson(@Valid @RequestBody PersonRequestDTO personDTO){
        Person createdPerson = personService.createPerson(modelMapper.map(personDTO, Person.class));
        log.debug("Person - {} has been created", createdPerson);
        return modelMapper.map(createdPerson, PersonCreateResponseDTO.class);
    }
}
