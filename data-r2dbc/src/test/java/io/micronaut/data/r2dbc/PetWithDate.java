package io.micronaut.data.r2dbc;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.tck.entities.Pet;

import java.util.Date;
import java.util.UUID;

@MappedEntity
public class PetWithDate {
    @Id
    @AutoPopulated
    private UUID id;
    private String name;
    @Relation(Relation.Kind.MANY_TO_ONE)
    private OwnerWithLocalDateTime owner;
    private Pet.PetType type = Pet.PetType.DOG;
    private Date dateCreated;

    @Creator
    public PetWithDate(String name, Date dateCreated, OwnerWithLocalDateTime owner) {
        this.name = name;
        this.dateCreated = dateCreated;
        this.owner = owner;
    }

    public OwnerWithLocalDateTime getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public Pet.PetType getType() {
        return type;
    }

    public void setType(Pet.PetType type) {
        this.type = type;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public enum PetType {
        DOG,
        CAT
    }
}
