package com.tbarauskas.elastumtask.service;

import com.tbarauskas.elastumtask.entity.Person;
import com.tbarauskas.elastumtask.exception.NoRelativeFindForCurrentPersonException;
import com.tbarauskas.elastumtask.exception.PersonNotFoundException;
import com.tbarauskas.elastumtask.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.tbarauskas.elastumtask.model.Kinship.*;

@Service
public class PersonService {

    private final NameSymbolService nameSymbolService;

    private final PersonRepository personRepository;

    public PersonService(NameSymbolService nameSymbolService, PersonRepository personRepository) {
        this.nameSymbolService = nameSymbolService;
        this.personRepository = personRepository;
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
        Collection<Person> relativeList = new HashSet<>();
        Person person = getPersonById(id);

        if (person.isMale()) {
            relativeList.add(getPersonsWife(nameSymbolService.getSurnameForWifeFromHusband(person.getSurname()),
                    person.getBirthDate()));
            relativeList.addAll(getPersonsBrothers(person.getSurname(), person.getBirthDate()));
            relativeList.addAll(getPersonsSisters(person.getSurname(), person.getBirthDate()));
            relativeList.addAll(getPersonsSons(person.getSurname(), person.getBirthDate()));
            relativeList.addAll(getPersonsDaughters(person.getSurname(), person.getBirthDate()));
            relativeList.add(getPersonsFather(person.getSurname(), person.getBirthDate()));
        } else {

        }

        relativeList.remove(person);
        if (relativeList.size() <= 1) {
            throw new NoRelativeFindForCurrentPersonException();
        }
        return List.copyOf(relativeList);
    }

    private Person getPersonsWife(String surname, LocalDate birthDate) {
        Person wife = personRepository.getPersonByBirthDateBetweenAndSurnameContainingIgnoreCase(birthDate.minusYears(15),
                birthDate.plusYears(15), surname).orElse(null);

        if (wife != null) {
            wife.setKinship(WIFE.name());
            return wife;
        }
        return null;
    }

    private List<Person> getPersonsBrothers(String surname, LocalDate birthDate) {
        List<Person> brotherList = getPersonsByBirthDateBetweenAndSurname(surname, birthDate.minusYears(15),
                birthDate.plusYears(15));
        brotherList.forEach(person -> person.setKinship(BROTHER.name()));
        return brotherList;
    }

    private List<Person> getPersonsSisters(String surname, LocalDate birthDate) {
        String surnameForSister = nameSymbolService.getSurnameForDaughterOrSisterFromFather(surname);
        List<Person> sisterList = getPersonsByBirthDateBetweenAndSurname(surnameForSister, birthDate.minusYears(15),
                birthDate.plusYears(15));
        sisterList.forEach(person -> person.setKinship(SISTER.name()));
        return sisterList;
    }

    private List<Person> getPersonsSons(String surname, LocalDate birthDate) {
        List<Person> sonsList = getPersonsByBirthDateBetweenAndSurname(surname, birthDate.plusYears(15),
                birthDate.plusYears(40));
        sonsList.forEach(person -> person.setKinship(SON.name()));
        return sonsList;
    }

    private List<Person> getPersonsDaughters(String surname, LocalDate birthdate) {
        String surnameForDaughter = nameSymbolService.getSurnameForDaughterOrSisterFromFather(surname);
        List<Person> daughterList = getPersonsByBirthDateBetweenAndSurname(surnameForDaughter,
                birthdate.plusYears(15), birthdate.plusYears(40));
        daughterList.forEach(person -> person.setKinship(DAUGHTER.name()));
        return daughterList;
    }

    private Person getPersonsFather(String surname, LocalDate birthdate) {
        Person father = getPersonByBirthDateBetweenAndSurname(surname, birthdate.plusYears(15), birthdate.plusYears(40));

        if (father != null) {
            father.setKinship(FATHER.name());
            return father;
        }
        return null;
    }

    private Person getPersonsMother(String surname, LocalDate birthDate) {
        return null;
    }

    private List<Person> getPersonsByBirthDateBetweenAndSurname(String surname, LocalDate from, LocalDate to) {
        return personRepository.getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(from, to, surname);
    }

    private List<Person> getPersonsByBirthDateAfterAndSurname(String surname, LocalDate birthDate) {
        return personRepository.getPersonsByBirthDateAfterAndSurnameContainingIgnoreCase(birthDate, surname);
    }

    private Person getPersonByBirthDateAfterAndSurname(String surname, LocalDate birthDate) {
        return personRepository.getPersonByBirthDateAfterAndSurnameContainingIgnoreCase(birthDate, surname)
                .orElse(null);
    }

    private Person getPersonByBirthDateBetweenAndSurname(String surname, LocalDate from, LocalDate to) {
        return personRepository.getPersonByBirthDateBetweenAndSurnameContainingIgnoreCase(from, to, surname)
                .orElse(null);
    }
}
