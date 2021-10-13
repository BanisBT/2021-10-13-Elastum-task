package com.tbarauskas.elastumtask.repository;

import com.tbarauskas.elastumtask.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
