package com.zoo.park.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** 投喂记录。 */
@Entity
@Table(name = "feeding")
public class Feeding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "feeding_code", nullable = false, length = 20, unique = true)
    public String feedingCode;

    @Column(name = "animal_id", nullable = false)
    public Long animalId;

    @Column(name = "feed_time", length = 8)
    public String feedTime;

    @Column(name = "food_name", nullable = false, length = 40)
    public String foodName;

    @Column(name = "amount", nullable = false)
    public Integer amount;

    @Column(name = "keeper_name", nullable = false, length = 32)
    public String keeperName;
}
