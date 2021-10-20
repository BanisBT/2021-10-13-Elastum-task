package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.entity.Person;
import com.tbarauskas.elastumtask.exception.NoRelativeFindForCurrentPersonException;
import com.tbarauskas.elastumtask.exception.PersonNotFoundException;
import com.tbarauskas.elastumtask.model.Kinship;
import com.tbarauskas.elastumtask.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.tbarauskas.elastumtask.model.Kinship.*;

@Service
public class PersonService {

    private final NameSymbolService nameSymbolService;

    private final BirthDateService dateService;

    private final PersonRepository personRepository;

    private final Person tempPerson;

    public PersonService(NameSymbolService nameSymbolService, BirthDateService dateService, PersonRepository personRepository) {
        this.nameSymbolService = nameSymbolService;
        this.dateService = dateService;
        this.personRepository = personRepository;
        this.tempPerson = personRepository.getPersonById(1L).orElseThrow(RuntimeException::new);
    }

    public Person getPersonById(Long id) {
        return personRepository.getPersonById(id).orElseThrow(() -> new PersonNotFoundException(id));
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person createPerson(Person person) {
        nameSymbolService.isCorrectNameAndSurname(person.getName(), person.getSurname());
        return personRepository.save(person);
    }

    public List<Person> getPersonsRelatives(Long id) {
        Collection<Person> relativeSet = new HashSet<>();
        Person person = getPersonById(id);

        String surname = person.getSurname();
        LocalDate birthDate = person.getBirthDate();

        if (person.isMale()) {
            relativeSet = getMalePersonsSetRelatives(surname, birthDate);
        } else if (person.isFemaleWithHusbandSurname()) {
            relativeSet = getFemalesWithHusbandSurnameSetRelative(surname, birthDate);
        } else if (person.isFemaleWithFamilySurname()) {
            relativeSet = getFemalesWithFamilySurnameRelatives(surname, birthDate);
        } else if (person.isFemaleWithDoubleSurname()) {
            relativeSet = getFemalesWithDoubleSurnameRelative(surname, birthDate);
        }

        relativeSet.remove(person);
        relativeSet.remove(tempPerson);

        if (relativeSet.size() == 0) {
            throw new NoRelativeFindForCurrentPersonException();
        }
        return List.copyOf(relativeSet);
    }

    private Collection<Person> getMalePersonsSetRelatives(String surname, LocalDate birthDate) {
        Collection<Person> relativeSet = new HashSet<>();
        String surnameFromMaleToWoman = nameSymbolService.getSurnameForRelativeSearch(surname, HUSBAND, WIFE);
        String surnameFromMaleToGirl = nameSymbolService.getSurnameForRelativeSearch(surname, FATHER, DAUGHTER);

        relativeSet.add(getPersonsFemaleRelative(surnameFromMaleToWoman, birthDate, WIFE));
        relativeSet.addAll(getPersonsListRelatives(surname, surnameFromMaleToWoman, surnameFromMaleToGirl, birthDate));
        relativeSet.addAll(getPersonsNextGenerationListRelatives(surname, surnameFromMaleToGirl, birthDate));

        return relativeSet;
    }

    private Collection<Person> getFemalesWithHusbandSurnameSetRelative(String surname, LocalDate birthDate) {
        Collection<Person> relativeSet = new HashSet<>();

        String surnameFromWomanToMan = nameSymbolService.getSurnameForRelativeSearch(surname, WIFE, HUSBAND);
        String surnameFromWomanToGirl = nameSymbolService.getSurnameForRelativeSearch(surname, MOTHER, DAUGHTER);

        relativeSet.add(getPersonsMaleRelative(surnameFromWomanToMan, birthDate, HUSBAND));
        relativeSet.addAll(getPersonsNextGenerationListRelatives(surnameFromWomanToMan, surnameFromWomanToGirl, birthDate));

        return relativeSet;
    }

    private Collection<Person> getFemalesWithFamilySurnameRelatives(String surname, LocalDate birthDate) {
        String surnameForMale = nameSymbolService.getSurnameForRelativeSearch(surname, DAUGHTER, FATHER);
        String surnameForWoman = nameSymbolService.getSurnameForRelativeSearch(surname, DAUGHTER, MOTHER);

        return getPersonsListRelatives(surnameForMale, surnameForWoman, surname, birthDate);
    }

    private Collection<Person> getFemalesWithDoubleSurnameRelative(String surname, LocalDate birthDate) {
        Collection<Person> relativeSet = new HashSet<>();

        String surnameFirstWord = nameSymbolService.getNeededSurnameFromDouble(surname, 1);
        String surnameSecondWord = nameSymbolService.getNeededSurnameFromDouble(surname, 2);
        String surnameForMale = nameSymbolService.getSurnameForRelativeSearch(surnameSecondWord, WIFE, HUSBAND);
        String surnameForGirl = nameSymbolService.getSurnameForRelativeSearch(surnameSecondWord, MOTHER, DAUGHTER);
        String surnameForWoman = nameSymbolService.getSurnameForRelativeSearch(surnameFirstWord, DAUGHTER, MOTHER);
        String surnameFamilyForMale = nameSymbolService.getSurnameForRelativeSearch(surnameFirstWord, DAUGHTER, FATHER);

        relativeSet.add(getPersonsMaleRelative(surnameForMale, birthDate, HUSBAND));
        relativeSet.addAll(getPersonsNextGenerationListRelatives(surnameForMale, surnameForGirl, birthDate));
        relativeSet.addAll(getPersonsListRelatives(surnameFamilyForMale, surnameForWoman, surnameFirstWord, birthDate));

        return relativeSet;
    }

    private List<Person> getPersonsNextGenerationListRelatives(String surnameMale, String surnameFemale, LocalDate birthDate) {
        List<Person> relativeList = new ArrayList<>();

        relativeList.addAll(getPersonsListMaleRelative(surnameMale, birthDate, SON));
        relativeList.addAll(getPersonsListMaleRelative(surnameMale, birthDate, GRANDSON));
        relativeList.addAll(getPersonsListFemaleRelative(surnameFemale, birthDate, DAUGHTER));
        relativeList.addAll(getPersonsListFemaleRelative(surnameFemale, birthDate, GRANDDAUGHTER));

        return relativeList;
    }

    private List<Person> getPersonsListRelatives(String surnameMale, String surnameWoman, String surnameGirl,
                                                 LocalDate birthDate) {
        List<Person> relativeList = new ArrayList<>();

        relativeList.add(getPersonsMaleRelative(surnameMale, birthDate, FATHER));
        relativeList.add(getPersonsMaleRelative(surnameMale, birthDate, GRANDFATHER));
        relativeList.add(getPersonsFemaleRelative(surnameWoman, birthDate, MOTHER));
        relativeList.add(getPersonsFemaleRelative(surnameWoman, birthDate, GRANDMOTHER));
        relativeList.addAll(getPersonsRelatives(surnameMale, birthDate, BROTHER));
        relativeList.addAll(getPersonsRelatives(surnameMale, birthDate, SISTER));

        return relativeList;
    }

    private Person getPersonsMaleRelative(String surname, LocalDate birthDate, Kinship kinship) {
        Person maleRelative = getPersonByBirthDateBetweenAndSurname(surname,
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(0),
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(1));

        if (maleRelative != null) {
            maleRelative.setKinship(kinship.name());
            return maleRelative;
        }
        return tempPerson;
    }

    private Person getPersonsFemaleRelative(String surname, LocalDate birthDate, Kinship kinship) {
        List<Person> femaleRelativeList = getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(surname,
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(0),
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(1));

        return getPersonsFemaleRelativeFromList(femaleRelativeList, surname, kinship).get(0);
    }

    private List<Person> getPersonsFemaleRelativeFromList(List<Person> personList, String surname, Kinship kinship) {
        List<Person> relativeList = new ArrayList<>();

        for (Person person : personList) {
            if (person.getSurname().equals(surname)) {
                person.setKinship(kinship.name());
                relativeList.add(person);
            } else if (person.isFemaleWithDoubleSurname()) {
                if (kinship.isFemaleWIthFamilySurname()
                        && nameSymbolService.getNeededSurnameFromDouble(person.getSurname(), 1)
                        .equals(surname)) {
                    person.setKinship(kinship.name());
                    relativeList.add(person);
                } else if (kinship.isFemaleWithHusbandSurname()
                        && nameSymbolService.getNeededSurnameFromDouble(person.getSurname(), 2)
                        .equals(surname)) {
                    relativeList.add(person);
                }
            }
        }
        if (relativeList.isEmpty()) {
            relativeList.add(tempPerson);
        }
        return relativeList;
    }

    private List<Person> getPersonsRelatives(String surname, LocalDate birthDate, Kinship kinship) {
        if (kinship.isMale()) {
            return getPersonsListMaleRelative(surname, birthDate, kinship);
        }
        return getPersonsListFemaleRelative(surname, birthDate, kinship);
    }

    private List<Person> getPersonsListMaleRelative(String surname, LocalDate birthDate, Kinship kinship) {
        List<Person> maleRelativeList = getPersonsByBirthDateBetweenAndSurname(surname,
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(0),
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(1));
        maleRelativeList.forEach(person -> person.setKinship(kinship.name()));

        return maleRelativeList;
    }

    private List<Person> getPersonsListFemaleRelative(String surname, LocalDate birthDate, Kinship kinship) {
        List<Person> femaleRelativeList = getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(surname,
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(0),
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(1));

        return getPersonsFemaleRelativeFromList(femaleRelativeList, surname, kinship);
    }

    private Person getPersonByBirthDateBetweenAndSurname(String surname, LocalDate from, LocalDate to) {
        return personRepository.getPersonByBirthDateBetweenAndSurname(from, to, surname).orElse(null);
    }

    private List<Person> getPersonsByBirthDateBetweenAndSurname(String surname, LocalDate from, LocalDate to) {
        return personRepository.getPersonsByBirthDateBetweenAndSurname(from, to, surname);
    }

    private List<Person> getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(String surname, LocalDate from,
                                                                                    LocalDate to) {
        return personRepository.getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(from, to, surname);
    }
}
