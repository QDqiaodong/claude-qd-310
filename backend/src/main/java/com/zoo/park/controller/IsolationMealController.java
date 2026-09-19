package com.zoo.park.controller;

import com.zoo.park.entity.IsolationMeal;
import com.zoo.park.service.IsolationMealService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/isolation-meals")
public class IsolationMealController {

    private final IsolationMealService service;

    public IsolationMealController(IsolationMealService service) {
        this.service = service;
    }

    @GetMapping
    public List<IsolationMeal> list() {
        return service.list();
    }

    @PostMapping
    public IsolationMeal create(@RequestBody IsolationMeal form) {
        return service.save(form);
    }
}
