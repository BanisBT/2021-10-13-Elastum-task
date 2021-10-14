package com.tbarauskas.elastumtask.model;

import lombok.Data;

@Data
public class ErrorHandler {

    private final Integer status;

    private final String message;
}
