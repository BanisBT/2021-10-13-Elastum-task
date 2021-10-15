package com.tbarauskas.elastumtask.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonCreateResponseDTO {

    private Long id;

    private String name;

    private String surname;

    private LocalDate birthDate;
}
