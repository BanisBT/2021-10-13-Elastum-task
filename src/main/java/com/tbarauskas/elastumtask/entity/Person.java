package com.tbarauskas.elastumtask.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birthDate")
    private LocalDate birthDate;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated")
    private LocalDateTime updated;

    @Transient
    private String kinship;

    public boolean isMale(){
        return surname.endsWith("s");
    }

    public boolean isFemaleWithDoubleSurname() {
        return surname.contains("-");
    }

    public boolean isFemaleWithHusbandSurname() {
        return surname.endsWith("ienė") && !surname.contains("-");
    }

    public boolean isFemaleWithFamilySurname() {
        return surname.endsWith("utė") | surname.endsWith("ytė") |
                surname.endsWith("aitė");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return getId().equals(person.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
