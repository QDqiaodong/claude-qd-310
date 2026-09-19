package com.zoo.park.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

/** 隔离加餐单：只收隔离中的动物，同一动物同一个日历日只能落下一张。 */
@Entity
@Table(name = "isolation_meal",
        uniqueConstraints = @UniqueConstraint(name = "uk_meal_animal_day",
                columnNames = {"animal_id", "meal_date"}))
public class IsolationMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "meal_code", nullable = false, length = 20, unique = true)
    public String mealCode;

    @Column(name = "animal_id", nullable = false)
    public Long animalId;

    /** 加餐落在哪个日历日，由服务端在提交当下定。 */
    @Column(name = "meal_date", nullable = false)
    public LocalDate mealDate;

    @Column(name = "food_name", nullable = false, length = 40)
    public String foodName;

    @Column(name = "grams", nullable = false)
    public Integer grams;

    @Column(name = "keeper_name", nullable = false, length = 32)
    public String keeperName;
}
