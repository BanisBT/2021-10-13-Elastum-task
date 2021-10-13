package com.tbarauskas.elastumtask.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonResponseDTO {

    private String name;

    private String surname;

    private LocalDate birthDate;
}
