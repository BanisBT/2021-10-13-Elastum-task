package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.exception.MoreThenOneAllowedSymbolInNameException;
import com.tbarauskas.elastumtask.exception.MoreThenOneAllowedSymbolInSurnameException;
import com.tbarauskas.elastumtask.model.Kinship;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NameSymbolService {

    private final String specialPerson = "Special persons";

    private final String iene = "ienė";

    private final String as = "as";

    private final String yte = "ytė";

    private final String ute = "utė";

    private final String aite = "aitė";

    public String getSurnameForRelativeSearch(String surname, Kinship fromKinship, Kinship toKinship) {

        if (fromKinship.isMale()) {
            if (toKinship.isFemaleWithHusbandSurname()) {
                return getSurnameForWomanFromHusband(surname);
            } else if (toKinship.isFemaleWIthFamilySurname()) {
                return getSurnameForGirlFromFatherOrWoman(surname);
            }

        } else if (fromKinship.isFemaleWithHusbandSurname()) {
            if (toKinship.isMale()) {
                return getWordWithNewInflexion(surname, iene, as);
            } else if (toKinship.isFemaleWIthFamilySurname()) {
                return getSurnameForGirlFromFatherOrWoman(surname);
            }

        } else if (fromKinship.isFemaleWIthFamilySurname()) {
            if (toKinship.isMale() | toKinship.isFemaleWithHusbandSurname()) {
                return getSurnameForRelativeFromGirl(surname, toKinship);
            }
        }
        return specialPerson;
    }

    public String getNeededSurnameFromDouble(String surname, int firstOrSecond) {
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

    private String getWordWithNewInflexion(String word, String oldInflexion, String newInflexion) {
        return word.substring(0, word.length() - oldInflexion.length()) + newInflexion;
    }

    private String getSurnameForRelativeFromGirl(String surname, Kinship kinship) {

        if (kinship.equals(Kinship.HUSBAND) | kinship.equals(Kinship.FATHER) | kinship.equals(Kinship.BROTHER)) {
            if (surname.endsWith(aite)) {
                return getWordWithNewInflexion(surname, aite, as);
            } else if (surname.endsWith(yte)) {
                return getWordWithNewInflexion(surname, yte, "is");
            } else if (surname.endsWith(ute)) {
                return getWordWithNewInflexion(surname, ute, "us");
            }
        } else {
            if (surname.endsWith(aite)) {
                return getWordWithNewInflexion(surname, aite, iene);
            } else if (surname.endsWith(yte)) {
                return getWordWithNewInflexion(surname, yte, iene);
            } else if (surname.endsWith(ute)) {
                return getWordWithNewInflexion(surname, ute, iene);
            }
        }
            return specialPerson;
    }


        private String getSurnameForWomanFromHusband (String surname){
            char thirdFromBehindLetterOfSurname = surname.charAt(surname.length() - 3);

            if (StringUtils.countOccurrencesOf("aeėuūio", String.valueOf(thirdFromBehindLetterOfSurname)) == 0) {
                return surname.substring(0, surname.length() - 2) + iene;
            } else {
                return surname.substring(0, surname.length() - 3) + iene;
            }
        }

        private String getSurnameForGirlFromFatherOrWoman (String surname){
            int length = surname.length();

            if (surname.substring(length - 2).equals(as)) {
                return surname.substring(0, length - 2) + aite;
            } else if (surname.substring(length - 2).equals("is") | surname.substring(length - 2).equals("ys")) {
                return surname.substring(0, length - 2) + yte;
            } else if (surname.substring(length - 2).equals("us")) {
                return surname.substring(0, length - 2) + ute;
            } else if (surname.substring(length - 4).equals(iene)) {
                return (surname.substring(0, length - 4) + aite);
            }

            return specialPerson;
        }

        private void isCorrectName (String name){
            if (StringUtils.countOccurrencesOf(name, " ") > 1) {
                throw new MoreThenOneAllowedSymbolInNameException();
            }
        }

        private void isCorrectSurname (String surname){
            if (StringUtils.countOccurrencesOf(surname, "-") > 1) {
                throw new MoreThenOneAllowedSymbolInSurnameException();
            }
        }
    }
