package com.zoo.park.repository;

import com.zoo.park.entity.IsolationMeal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IsolationMealRepository extends JpaRepository<IsolationMeal, Long> {

    List<IsolationMeal> findAllByOrderByIdDesc();

    boolean existsByAnimalIdAndMealDate(Long animalId, LocalDate mealDate);
}
