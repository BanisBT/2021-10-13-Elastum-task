package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.exception.MoreThenOneAllowedSymbolInNameException;
import com.tbarauskas.elastumtask.exception.MoreThenOneAllowedSymbolInSurnameException;
import com.tbarauskas.elastumtask.model.Kinship;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NameSymbolServiceTest {

    private final String surnameMale = "Barauskas";

    private final String surnameWoman = "Barauskienė";

    @InjectMocks
    private NameSymbolService nameService;

    @Test
    void testGetSurnameForRelativeSearchIfFromMaleToFemaleWithHusbandSurnameHusbandWife() {
        String surname = nameService.getSurnameForRelativeSearch(surnameMale, Kinship.HUSBAND, Kinship.WIFE);

        assertEquals(surnameWoman, surname);
    }

    @Test
    void testGetSurnameForRelativeSearchIfFromMaleToFemaleWithHusbandSurnameWithExtraVowel() {
        String surname = nameService.getSurnameForRelativeSearch("Svinius", Kinship.HUSBAND, Kinship.WIFE);

        assertEquals("Svinienė", surname);
    }

    @Test
    void testGetSurnameForRelativeSearchIfFromMaleToFemaleWithHusbandSurnameFatherMother() {
        String surname = nameService.getSurnameForRelativeSearch(surnameMale, Kinship.FATHER, Kinship.MOTHER);

        assertEquals(surnameWoman, surname);
    }

    @Test
    void testGetSurnameForRelativeSearchIfFromMaleToFemaleWithFamilySurnameFatherDaughterYte() {
        String surname = nameService.getSurnameForRelativeSearch(surnameMale, Kinship.FATHER, Kinship.DAUGHTER);

        assertEquals("Barauskaitė", surname);
    }

    @Test
    void testGetSurnameForRelativeSearchIfFromMaleToFemaleWithFamilySurnameFatherDaughterAite() {
        String surname = nameService.getSurnameForRelativeSearch("Burokas", Kinship.FATHER, Kinship.GRANDDAUGHTER);

        assertEquals("Burokaitė", surname);
    }

    @Test
    void testGetSurnameForRelativeSearchIfFromMaleToFemaleWithFamilySurnameFatherDaughterUte() {
        String surname = nameService.getSurnameForRelativeSearch("Motiejus", Kinship.BROTHER, Kinship.SISTER);

        assertEquals("Motiejutė", surname);
    }

    @Test
    void testGetSurnameForRelativeSearchIfFromFemaleWithFamilySurnameToFemaleWithHusbandSurnameDaughterAiteMother() {
        String surname = nameService.getSurnameForRelativeSearch("Barauskaitė", Kinship.GRANDDAUGHTER,
                Kinship.MOTHER);

        assertEquals(surnameWoman, surname);
    }

    @Test
    void testGetSurnameForRelativeSearchIfFromFemaleWithFamilySurnameToFemaleWithHusbandSurnameDaughterYteMother() {
        String surname = nameService.getSurnameForRelativeSearch("Barauskytė", Kinship.DAUGHTER,
                Kinship.MOTHER);

        assertEquals(surnameWoman, surname);
    }

    @Test
    void testGetSurnameForRelativeSearchIfFromFemaleWithFamilySurnameToMaleDaughterAiteFather() {
        String surname = nameService.getSurnameForRelativeSearch("Barauskaitė", Kinship.GRANDDAUGHTER,
                Kinship.FATHER);

        assertEquals(surnameMale, surname);
    }

    @Test
    void testGetSurnameForRelativeSearchIfFromFemaleWithFamilySurnameToMaleDaughterYteBrother() {
        String surname = nameService.getSurnameForRelativeSearch("Laukaitytė", Kinship.SISTER,
                Kinship.BROTHER);

        assertEquals("Laukaitis", surname);
    }

    @Test
    void testGetNeededSurnameFromDoubleIsSurnameNotDouble() {
        String surname = nameService.getNeededSurnameFromDouble(surnameWoman, 2);

        assertEquals(surnameWoman, surname);
    }

    @Test
    void testGetNeededSurnamesFromDouble() {
        String surnameDouble = "Barauskaite-Juodiene";
        String surnameFirst = nameService.getNeededSurnameFromDouble(surnameDouble, 1);
        String surnameSecond = nameService.getNeededSurnameFromDouble(surnameDouble, 2);

        assertEquals("Barauskaite", surnameFirst);
        assertEquals("Juodiene", surnameSecond);
    }

    @Test
    void testIsCorrectNameAndSurnameThrowsNameExc() {
        String inCorrectName = "Tomas Domas Jonas";

        assertThrows(MoreThenOneAllowedSymbolInNameException.class,
                () -> nameService.isCorrectNameAndSurname(inCorrectName, surnameMale));
    }

    @Test
    void testIsCorrectNameAndSurnameThrowsSurnameExc() {
        String inCorrectSurname = "Barauskas-Juodenas-Baltija";

        assertThrows(MoreThenOneAllowedSymbolInSurnameException.class,
                () -> nameService.isCorrectNameAndSurname(surnameMale, inCorrectSurname));
    }
}
