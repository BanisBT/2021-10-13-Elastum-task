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

    private final PersonRepository personRepository;

    private final Person tempPerson;

    public PersonService(NameSymbolService nameSymbolService, PersonRepository personRepository) {
        this.nameSymbolService = nameSymbolService;
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
            relativeSet = getMalePersonsSetRelative(surname, birthDate);
        } else if (person.isFemaleWithHusbandSurname()) {
            relativeSet = getFemalesWithHusbandSurnameSetRelative(surname, birthDate);

//            Jei moteris su mergautine pavarde
        } else if (person.isFemaleWithFamilySurname()) {

//            Jei moteris su dviem pavardemis
        } else if (person.isFemaleWithDoubleSurname()) {

//            Padaryti Vyro, Sunu, Anuku pavarde -as (is antros pavardes)
//            Padaryti Dukru, anukiu pavarde -aite, -yte, ute (is antros pavardes)
//            Padaryti mamos ir mociutes pavarde -iene (is pirmos pavardes)
//            Padaryti seseru pavarde (pirmoji pavarde)
//            Padaryti tevo, senelio ir broliu pavarde -as(is pirmos pavarde)

//            Rasti Vyra, sunus ir anukus
//            Dukras, anukes
//            Mama ir mociute
//            Teva, seneli ir brolius
//            Seses
        }

        relativeSet.remove(person);
        relativeSet.remove(tempPerson);

        if (relativeSet.size() == 0) {
            throw new NoRelativeFindForCurrentPersonException();
        }
        return List.copyOf(relativeSet);
    }

    private Collection<Person> getMalePersonsSetRelative(String surname, LocalDate birthDate) {
        Collection<Person> relativeSet = new HashSet<>();
        String surnameFromMaleToWoman = nameSymbolService.getSurnameForWifeFromHusband(surname);
        String surnameFromMaleToGirl = nameSymbolService.getSurnameForDaughterOrSisterFromFather(surname);

        relativeSet.add(getPersonsFemaleRelative(surnameFromMaleToWoman, birthDate.minusYears(15),
                birthDate.plusYears(15), WIFE));
        relativeSet.add(getPersonsFemaleRelative(surnameFromMaleToWoman, birthDate.minusYears(40),
                birthDate.minusYears(15).minusDays(1), MOTHER));
        relativeSet.addAll(getPersonsListMaleRelative(surname, birthDate, BROTHER));
        relativeSet.addAll(getPersonsListFemaleRelative(surnameFromMaleToGirl,
                birthDate.minusYears(15), birthDate.plusYears(15), SISTER));
        relativeSet.add(getPersonsMaleRelative(surname, birthDate, FATHER));

        relativeSet.addAll(getPersonsListRelative(surname, surnameFromMaleToGirl, birthDate));

        return relativeSet;
    }

    private Collection<Person> getFemalesWithHusbandSurnameSetRelative(String surname, LocalDate birthDate) {
        Collection<Person> relativeSet = new HashSet<>();

        String surnameFromWomanToMan = nameSymbolService.getWordWithNewInflexion(surname, "ienė", "as");
        String surnameFromWomanToGirl = nameSymbolService.getWordWithNewInflexion
                (surname, "ienė", "aitė");

        relativeSet.add(getPersonsMaleRelative(surnameFromWomanToMan, birthDate, HUSBAND));
        relativeSet.addAll(getPersonsListRelative(surnameFromWomanToMan, surnameFromWomanToGirl, birthDate));

        return relativeSet;
    }

    private Collection<Person> getFemalesWithFamilySurnameRelatives(String surname, LocalDate birthDate) {
        Collection<Person> relativeSet = new HashSet<>();

        //            Seseru pavarde ta pati
//            Padaryti tevo, broliu ir senelio pavarde -as
        String surnameForMale = nameSymbolService.getSurnameForRelativeFromGirl(surname, HUSBAND);
//            Padaryti mamos ir mociutes pavardes -iene
        String surnameForWoman = nameSymbolService.getSurnameForRelativeFromGirl(surname, MOTHER);

//            Rasti Teva, seneli ir brolius
        relativeSet.addAll(getPersonsListMaleRelative(surnameForMale, birthDate, BROTHER));
        relativeSet.add(getPersonsMaleRelative(surnameForMale, birthDate, FATHER));
        relativeSet.add(getPersonsMaleRelative(surnameForMale, birthDate, GRANDFATHER));
//            Mama ir mociute
//            Seses
        relativeSet.addAll(getPersonsListFemaleRelative(surname, birthDate.minusYears(15), birthDate.plusYears(15), SISTER));
        return null;
    }

    private List<Person> getPersonsListRelative(String surnameMale, String surnameFemale, LocalDate birthDate) {
        List<Person> relativeList = new ArrayList<>();

        relativeList.addAll(getPersonsListMaleRelative(surnameMale, birthDate, SON));
        relativeList.addAll(getPersonsListMaleRelative(surnameMale, birthDate, GRANDSON));
        relativeList.addAll(getPersonsListFemaleRelative(surnameFemale, birthDate.plusYears(15), birthDate.plusYears(40),
                DAUGHTER));
        relativeList.addAll(getPersonsListFemaleRelative(surnameFemale, birthDate.plusYears(40).plusDays(1),
                birthDate.plusYears(222), GRANDDAUGHTER));

        return relativeList;
    }

    private Person getPersonsMaleRelative(String surname, LocalDate birthDate, Kinship kinship) {
        LocalDate from = null;
        LocalDate to = null;

        if (kinship.equals(HUSBAND)) {
            from = birthDate.minusYears(15);
            to = birthDate.plusYears(15);
        } else if (kinship.equals(FATHER)) {
            from = birthDate.minusYears(40);
            to = birthDate.minusYears(15).minusDays(1);
        } else if (kinship.equals(GRANDFATHER)) {
            from = birthDate.minusYears(999);
            to = birthDate.minusYears(40).minusDays(1);
        }

        Person maleRelative = getPersonByBirthDateBetweenAndSurname(surname, from, to);

        if (maleRelative != null) {
            maleRelative.setKinship(kinship.name());
            return maleRelative;
        }
        return tempPerson;
    }

    private Person getPersonsFemaleRelative(String surname, LocalDate from, LocalDate to, Kinship kinship) {
        List<Person> femaleRelativeList = getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(surname, from, to);
        for (Person person : femaleRelativeList) {
            if (person.isFemaleWithDoubleSurname()
                    && nameSymbolService.getNeededSurname(surname, 2).equals(surname)) {
                person.setKinship(kinship.name());
                return person;
            } else if (person.getSurname().equals(surname)) {
                person.setKinship(kinship.name());
                return person;
            }
        }
        return tempPerson;
    }

    private List<Person> getPersonsListMaleRelative(String surname, LocalDate birthDate, Kinship kinship) {
        LocalDate from = null;
        LocalDate to = null;

        if (kinship.equals(HUSBAND) | kinship.equals(BROTHER)) {
            from = birthDate.minusYears(15);
            to = birthDate.plusYears(15);
        } else if (kinship.equals(GRANDFATHER)) {
            from = birthDate.minusYears(999);
            to = birthDate.minusYears(40).minusDays(1);
        } else if (kinship.equals(SON)) {
            from = birthDate.plusYears(15).plusDays(1);
            to = birthDate.plusYears(40);
        } else if (kinship.equals(GRANDSON)) {
            from = birthDate.plusYears(40).plusDays(1);
            to = birthDate.plusYears(999);
        }

        List<Person> maleRelativeList = getPersonsByBirthDateBetweenAndSurname(surname, from, to);
        maleRelativeList.forEach(person -> person.setKinship(kinship.name()));
        return maleRelativeList;
    }

    private List<Person> getPersonsListFemaleRelative(String surname, LocalDate from, LocalDate to, Kinship kinship) {
        List<Person> femaleRelativeList = getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(surname, from, to);
        femaleRelativeList.forEach(person -> person.setKinship(kinship.name()));
        return femaleRelativeList;
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
