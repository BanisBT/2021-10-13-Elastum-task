package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.exception.MoreThenOneAllowedSymbolInNameException;
import com.tbarauskas.elastumtask.exception.MoreThenOneAllowedSymbolInSurnameException;
import com.tbarauskas.elastumtask.model.Kinship;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NameSymbolService {

    public String getNeededSurname(String surname, int firstOrSecond) {
        String[] surnameArray = surname.split("-", 2);

        if (StringUtils.countOccurrencesOf(surname, "-") == 0) {
            return surname;
        } else if (firstOrSecond == 1) {
            return surnameArray[0];
        } else if (firstOrSecond == 2) {
            return surnameArray[1];
        }
        throw new RuntimeException();
    }

    public void isCorrectNameAndSurname(String name, String surname) {
        isCorrectName(name);
        isCorrectSurname(surname);
    }

    public String getWordWithNewInflexion(String word, String oldInflexion, String newInflexion) {
        return word.substring(0, word.length() - oldInflexion.length()) + newInflexion;
    }

    public String getSurnameForRelativeFromGirl(String surname, Kinship kinship) {
        String kinshipInflexion;

        if (kinship.equals(Kinship.HUSBAND)) {
            kinshipInflexion = "as";
        } else {
            kinshipInflexion = "ienė";
        }

        if (surname.endsWith("aitė")) {
            return getWordWithNewInflexion(surname, "aitė", kinshipInflexion);
        } else if (surname.endsWith("ytė")) {
            return getWordWithNewInflexion(surname, "ytė", kinshipInflexion);
        } else if (surname.endsWith("utė")) {
            return getWordWithNewInflexion(surname, "utė", kinshipInflexion);
        }
        return "Special person";
    }

    public String getSurnameForWifeFromHusband(String surname) {
        char thirdFromBehindLetterOfSurname = surname.charAt(surname.length() - 3);

        if (StringUtils.countOccurrencesOf("aeėuūio", String.valueOf(thirdFromBehindLetterOfSurname)) == 0) {
            return surname.substring(0, surname.length() - 2) + "ienė";
        } else {
            return surname.substring(0, surname.length() - 3) + "ienė";
        }
    }

    public String getSurnameForDaughterOrSisterFromFather(String surname) {
        int length = surname.length();

        if (surname.substring(length - 2).equals("as")) {
            return surname.substring(0, length - 2) + "aitė";
        } else if (surname.substring(length - 2).equals("is") | surname.substring(length - 2).equals("ys")) {
            return surname.substring(0, length - 2) + "ytė";
        } else if (surname.substring(length - 2).equals("us")) {
            return surname.substring(0, length - 2) + "utė";
        } else if (surname.substring(length - 4).equals("ienė")) {
            return (surname.substring(0, length - 4) + "aitė");
        }

        return "Special person";
    }

    public String getSurnameForMotherOrSisterFromFemale(String surname) {
        return null;
    }

    private void isCorrectName(String name) {
        if (StringUtils.countOccurrencesOf(name, " ") > 1) {
            throw new MoreThenOneAllowedSymbolInNameException();
        }
    }

    private void isCorrectSurname(String surname) {
        if (StringUtils.countOccurrencesOf(surname, "-") > 1) {
            throw new MoreThenOneAllowedSymbolInSurnameException();
        }
    }
}
