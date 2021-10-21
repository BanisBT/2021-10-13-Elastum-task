package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.model.Kinship;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BirthDateServiceTest {

    private final LocalDate date = LocalDate.of(2000, 1, 1);

    @InjectMocks
    private BirthDateService dateService;

    @Test
    void testGetDatesForRelativeSearchingKinshipHusband() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.HUSBAND);

        assertEquals(date.minusYears(15), dates.get(0));
        assertEquals(date.plusYears(15), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipWife() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.WIFE);

        assertEquals(date.minusYears(15), dates.get(0));
        assertEquals(date.plusYears(15), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipBrother() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.BROTHER);

        assertEquals(date.minusYears(15), dates.get(0));
        assertEquals(date.plusYears(15), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipSister() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.SISTER);

        assertEquals(date.minusYears(15), dates.get(0));
        assertEquals(date.plusYears(15), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipFather() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.FATHER);

        assertEquals(date.minusYears(40), dates.get(0));
        assertEquals(date.minusYears(15).minusDays(1), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipMother() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.MOTHER);

        assertEquals(date.minusYears(40), dates.get(0));
        assertEquals(date.minusYears(15).minusDays(1), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipSon() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.SON);

        assertEquals(date.plusYears(15).plusDays(1), dates.get(0));
        assertEquals(date.plusYears(40), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipDaughter() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.DAUGHTER);

        assertEquals(date.plusYears(15).plusDays(1), dates.get(0));
        assertEquals(date.plusYears(40), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipGrandson() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.GRANDSON);

        assertEquals(date.plusYears(40).plusDays(1), dates.get(0));
        assertEquals(date.plusYears(999), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipGranddaughter() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.GRANDDAUGHTER);

        assertEquals(date.plusYears(40).plusDays(1), dates.get(0));
        assertEquals(date.plusYears(999), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipGrandfather() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.GRANDFATHER);

        assertEquals(date.minusYears(999), dates.get(0));
        assertEquals(date.minusYears(40).minusDays(1), dates.get(1));
    }

    @Test
    void testGetDatesForRelativeSearchingKinshipGrandmother() {
        List<LocalDate> dates = dateService.getDatesForRelativeSearching(date, Kinship.GRANDMOTHER);

        assertEquals(date.minusYears(999), dates.get(0));
        assertEquals(date.minusYears(40).minusDays(1), dates.get(1));
    }
}
