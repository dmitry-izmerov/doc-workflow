package ru.demi.docworkflow.model;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;

@Entity
public class Company extends AbstractPersistable<Long> {
    private String name;
}
