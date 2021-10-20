package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.model.Kinship;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BirthDateService {

    public List<LocalDate> getDatesForRelativeSearching(LocalDate birtDate, Kinship kinship) {
        List<LocalDate> fromTo = new ArrayList<>();

        if (kinship.isSameGeneration()) {
            fromTo.add(0, birtDate.minusYears(15));
            fromTo.add(1, birtDate.plusYears(15));
            return fromTo;
        } else if (kinship.isNextGeneration()) {
            fromTo.add(0, birtDate.plusYears(15).plusDays(1));
            fromTo.add(1, birtDate.plusYears(40));
            return fromTo;
        } else if (kinship.isGrandNextGeneration()) {
            fromTo.add(0, birtDate.plusYears(40).plusDays(1));
            fromTo.add(1, birtDate.plusYears(999));
            return fromTo;
        } else if (kinship.isPreviousGeneration()) {
            fromTo.add(0, birtDate.minusYears(40));
            fromTo.add(1, birtDate.minusYears(15).minusDays(1));
            return fromTo;
        } else if (kinship.isGrandPreviousGeneration()) {
            fromTo.add(0, birtDate.minusYears(999));
            fromTo.add(1, birtDate.minusYears(40).minusDays(1));
            return fromTo;
        }

        throw new RuntimeException();
    }
}
