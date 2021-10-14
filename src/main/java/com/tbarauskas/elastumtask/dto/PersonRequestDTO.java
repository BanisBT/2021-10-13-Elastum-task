package com.tbarauskas.elastumtask.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class PersonRequestDTO {

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Name can be just from letters and space if it's double name")
    @Size(min = 2, max = 50, message = "Letters interval for name is from 2 to 50")
    private final String name;

    @Pattern(regexp = "^[a-zA-Z-]+$", message = "Surname can be just from letters and symbol '-' if it's double surname")
    @Size(min = 2, max = 50, message = "Letters interval for surname is from 2 to 50")
    private final String surname;

    private final LocalDate birthDate;
}
