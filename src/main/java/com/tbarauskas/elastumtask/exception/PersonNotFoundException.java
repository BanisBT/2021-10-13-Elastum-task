package com.tbarauskas.elastumtask.exception;

import lombok.Data;

@Data
public class PersonNotFoundException extends RuntimeException{

    private final Long id;
}
