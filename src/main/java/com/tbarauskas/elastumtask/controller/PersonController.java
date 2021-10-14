package com.tbarauskas.elastumtask.controller;

import com.tbarauskas.elastumtask.dto.PersonResponseDTO;
import com.tbarauskas.elastumtask.service.PersonService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
}
