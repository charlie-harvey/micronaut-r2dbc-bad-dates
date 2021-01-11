package io.micronaut.data.r2dbc;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.LocalDateTime;

@MappedEntity
public class OwnerWithLocalDateTime {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int age;
    private LocalDateTime dateCreated;

    @Creator
    public OwnerWithLocalDateTime(String name, LocalDateTime dateCreated) {
        this.name = name;
        this.dateCreated = dateCreated;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateCreated() {
        return this.dateCreated;
    }
}
