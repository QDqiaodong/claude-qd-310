package com.zoo.park.controller;

import com.zoo.park.entity.Animal;
import com.zoo.park.service.AnimalService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private final AnimalService service;

    public AnimalController(AnimalService service) {
        this.service = service;
    }

    @GetMapping
    public List<Animal> list(@RequestParam(required = false) Long enclosureId,
                             @RequestParam(required = false) String state,
                             @RequestParam(required = false) String keyword) {
        return service.list(enclosureId, state, keyword);
    }

    @PostMapping
    public Animal create(@RequestBody Animal form) {
        return service.save(form);
    }

    @PutMapping("/{id}")
    public Animal update(@PathVariable Long id, @RequestBody Animal form) {
        form.id = id;
        return service.save(form);
    }
}
