package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.entity.Person;
import com.tbarauskas.elastumtask.exception.NoRelativeFindForCurrentPersonException;
import com.tbarauskas.elastumtask.exception.PersonNotFoundException;
import com.tbarauskas.elastumtask.model.Kinship;
import com.tbarauskas.elastumtask.repository.PersonRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.tbarauskas.elastumtask.model.Kinship.*;

@Service
public class PersonService {

    private final NameSymbolService nameSymbolService;

    private final BirthDateService dateService;

    private final PersonRepository personRepository;

    public PersonService(NameSymbolService nameSymbolService, BirthDateService dateService, PersonRepository personRepository) {
        this.nameSymbolService = nameSymbolService;
        this.dateService = dateService;
        this.personRepository = personRepository;
    }

    public Person getPersonById(Long id) {
        return personRepository.getPersonById(id).orElseThrow(() -> new PersonNotFoundException(id));
    }

    public List<Person> getAllPersons(String sortBy) {
        if (sortBy != null) {
            switch (sortBy) {
                case "name":
                    return personRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
                case "surname":
                    return personRepository.findAll(Sort.by(Sort.Direction.ASC, "surname"));
                case "birthDate":
                    return personRepository.findAll(Sort.by(Sort.Direction.ASC, "birthDate"));
            }
        }
        return personRepository.findAll();
    }

    public Person createPerson(Person person) {
        nameSymbolService.isCorrectNameAndSurname(person.getName(), person.getSurname());
        return personRepository.save(person);
    }

    public Person updatePerson(Long id, Person person) {
        nameSymbolService.isCorrectNameAndSurname(person.getName(), person.getSurname());
        Person personFromDb = getPersonById(id);

        personFromDb.setName(person.getName());
        personFromDb.setSurname(person.getSurname());
        personFromDb.setBirthDate(person.getBirthDate());

        return personRepository.save(personFromDb);
    }

    public List<Person> getPersonsRelatives(Long id) {
        List<Person> relatives = new ArrayList<>();
        Person person = getPersonById(id);

        String surname = person.getSurname();
        LocalDate birthDate = person.getBirthDate();

        if (person.isMale()) {
            relatives = getMalePersonsSetRelatives(surname, birthDate, person);
        } else if (person.isFemaleWithHusbandSurname()) {
            relatives = getFemalesWithHusbandSurnameSetRelative(surname, birthDate, person);
        } else if (person.isFemaleWithFamilySurname()) {
            relatives = getFemalesWithFamilySurnameRelatives(surname, birthDate, person);
        } else if (person.isFemaleWithDoubleSurname()) {
            relatives = getFemalesWithDoubleSurnameRelative(surname, birthDate, person);
        }

        List<Person> uniqueRelatives = relatives.stream()
                .distinct()
                .collect(Collectors.toList());

        uniqueRelatives.remove(person);

        if (uniqueRelatives.size() == 0) {
            throw new NoRelativeFindForCurrentPersonException();
        }
        return uniqueRelatives;
    }

    private List<Person> getMalePersonsSetRelatives(String surname, LocalDate birthDate, Person person) {
        List<Person> relatives = new ArrayList<>();
        String surnameFromMaleToWoman = nameSymbolService.getSurnameForRelativeSearch(surname, HUSBAND, WIFE);
        String surnameFromMaleToGirl = nameSymbolService.getSurnameForRelativeSearch(surname, FATHER, DAUGHTER);

        relatives.add(getPersonsFemaleRelative(surnameFromMaleToWoman, birthDate, WIFE, person));
        relatives.addAll(getPersonsListRelatives(surname, surnameFromMaleToWoman, surnameFromMaleToGirl, birthDate, person));
        relatives.addAll(getPersonsNextGenerationListRelatives(surname, surnameFromMaleToGirl, birthDate, person));

        return relatives;
    }

    private List<Person> getFemalesWithHusbandSurnameSetRelative(String surname, LocalDate birthDate, Person person) {
        List<Person> relatives = new ArrayList<>();

        String surnameFromWomanToMan = nameSymbolService.getSurnameForRelativeSearch(surname, WIFE, HUSBAND);
        String surnameFromWomanToGirl = nameSymbolService.getSurnameForRelativeSearch(surname, MOTHER, DAUGHTER);

        relatives.add(getPersonsMaleRelative(surnameFromWomanToMan, birthDate, HUSBAND, person));
        relatives.addAll(getPersonsNextGenerationListRelatives(surnameFromWomanToMan, surnameFromWomanToGirl, birthDate, person));

        return relatives;
    }

    private List<Person> getFemalesWithFamilySurnameRelatives(String surname, LocalDate birthDate, Person person) {
        String surnameForMale = nameSymbolService.getSurnameForRelativeSearch(surname, DAUGHTER, FATHER);
        String surnameForWoman = nameSymbolService.getSurnameForRelativeSearch(surname, DAUGHTER, MOTHER);

        return getPersonsListRelatives(surnameForMale, surnameForWoman, surname, birthDate, person);
    }

    private List<Person> getFemalesWithDoubleSurnameRelative(String surname, LocalDate birthDate, Person person) {
        List<Person> relatives = new ArrayList<>();

        String surnameFirstWord = nameSymbolService.getNeededSurnameFromDouble(surname, 1);
        String surnameSecondWord = nameSymbolService.getNeededSurnameFromDouble(surname, 2);
        String surnameForMale = nameSymbolService.getSurnameForRelativeSearch(surnameSecondWord, WIFE, HUSBAND);
        String surnameForGirl = nameSymbolService.getSurnameForRelativeSearch(surnameSecondWord, MOTHER, DAUGHTER);
        String surnameForWoman = nameSymbolService.getSurnameForRelativeSearch(surnameFirstWord, DAUGHTER, MOTHER);
        String surnameFamilyForMale = nameSymbolService.getSurnameForRelativeSearch(surnameFirstWord, DAUGHTER, FATHER);

        relatives.add(getPersonsMaleRelative(surnameForMale, birthDate, HUSBAND, person));
        relatives.addAll(getPersonsNextGenerationListRelatives(surnameForMale, surnameForGirl, birthDate, person));
        relatives.addAll(getPersonsListRelatives(surnameFamilyForMale, surnameForWoman, surnameFirstWord, birthDate,
                person));

        return relatives;
    }

    private List<Person> getPersonsNextGenerationListRelatives(String surnameMale, String surnameFemale,
                                                               LocalDate birthDate, Person person) {
        List<Person> relativeList = new ArrayList<>();

        relativeList.addAll(getPersonsListMaleRelative(surnameMale, birthDate, SON));
        relativeList.addAll(getPersonsListMaleRelative(surnameMale, birthDate, GRANDSON));
        relativeList.addAll(getPersonsListFemaleRelative(surnameFemale, birthDate, DAUGHTER, person));
        relativeList.addAll(getPersonsListFemaleRelative(surnameFemale, birthDate, GRANDDAUGHTER, person));

        return relativeList;
    }

    private List<Person> getPersonsListRelatives(String surnameMale, String surnameWoman, String surnameGirl,
                                                 LocalDate birthDate, Person person) {
        List<Person> relativeList = new ArrayList<>();

        relativeList.add(getPersonsMaleRelative(surnameMale, birthDate, FATHER, person));
        relativeList.add(getPersonsMaleRelative(surnameMale, birthDate, GRANDFATHER, person));
        relativeList.add(getPersonsFemaleRelative(surnameWoman, birthDate, MOTHER, person));
        relativeList.add(getPersonsFemaleRelative(surnameWoman, birthDate, GRANDMOTHER, person));
        relativeList.addAll(getPersonsRelatives(surnameMale, birthDate, BROTHER, person));
        relativeList.addAll(getPersonsRelatives(surnameGirl, birthDate, SISTER, person));

        return relativeList;
    }

    private Person getPersonsMaleRelative(String surname, LocalDate birthDate, Kinship kinship, Person person) {
        Person maleRelative = getPersonByBirthDateBetweenAndSurname(surname,
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(0),
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(1));

        if (maleRelative != null) {
            maleRelative.setKinship(kinship.name());
            return maleRelative;
        }
        return person;
    }

    private Person getPersonsFemaleRelative(String surname, LocalDate birthDate, Kinship kinship, Person person) {
        List<Person> femaleRelativeList = getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(surname,
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(0),
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(1));

        return getPersonsFemaleRelativeFromList(femaleRelativeList, surname, kinship, person).get(0);
    }

    private List<Person> getPersonsFemaleRelativeFromList(List<Person> personList, String surname, Kinship kinship,
                                                          Person personSearch) {
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
            relativeList.add(personSearch);
        }
        return relativeList;
    }

    private List<Person> getPersonsRelatives(String surname, LocalDate birthDate, Kinship kinship, Person person) {
        if (kinship.isMale()) {
            return getPersonsListMaleRelative(surname, birthDate, kinship);
        }
        return getPersonsListFemaleRelative(surname, birthDate, kinship, person);
    }

    private List<Person> getPersonsListMaleRelative(String surname, LocalDate birthDate, Kinship kinship) {
        List<Person> maleRelativeList = getPersonsByBirthDateBetweenAndSurname(surname,
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(0),
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(1));
        maleRelativeList.forEach(person -> person.setKinship(kinship.name()));

        return maleRelativeList;
    }

    private List<Person> getPersonsListFemaleRelative(String surname, LocalDate birthDate, Kinship kinship, Person person) {
        List<Person> femaleRelativeList = getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(surname,
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(0),
                dateService.getDatesForRelativeSearching(birthDate, kinship).get(1));

        return getPersonsFemaleRelativeFromList(femaleRelativeList, surname, kinship, person);
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
