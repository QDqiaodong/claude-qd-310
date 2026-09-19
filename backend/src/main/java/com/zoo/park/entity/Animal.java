package com.zoo.park.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** 动物档案。 */
@Entity
@Table(name = "animal")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "animal_code", nullable = false, length = 20, unique = true)
    public String animalCode;

    @Column(name = "animal_name", nullable = false, length = 40)
    public String animalName;

    @Column(name = "species", nullable = false, length = 40)
    public String species;

    @Column(name = "enclosure_id")
    public Long enclosureId;

    @Column(name = "birth_date")
    public LocalDate birthDate;

    /** 健康 / 观察 / 隔离 */
    @Column(name = "animal_state", nullable = false, length = 12)
    public String animalState;
}
