package com.tbarauskas.elastumtask.repository;

import com.tbarauskas.elastumtask.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> getPersonById(Long id);

    Optional<Person> getPersonByBirthDateBetweenAndSurnameContainingIgnoreCase(LocalDate from, LocalDate to,
                                                                               String surname);

    Optional<Person> getPersonByBirthDateAfterAndSurnameContainingIgnoreCase(LocalDate birthDate, String surname);

    List<Person> getPersonsByBirthDateBetweenAndSurnameContainingIgnoreCase(LocalDate from, LocalDate to, String surname);

    List<Person> getPersonsByBirthDateAfterAndSurnameContainingIgnoreCase(LocalDate from, String surname);
}
