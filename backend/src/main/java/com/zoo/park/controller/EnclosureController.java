package com.zoo.park.controller;

import com.zoo.park.entity.Enclosure;
import com.zoo.park.service.EnclosureService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enclosures")
public class EnclosureController {

    private final EnclosureService service;

    public EnclosureController(EnclosureService service) {
        this.service = service;
    }

    @GetMapping
    public List<Enclosure> list(@RequestParam(required = false) String state,
                                @RequestParam(required = false) String keyword) {
        return service.list(state, keyword);
    }

    /** 每个笼舍的居住汇总（住在几只、平均年龄）。 */
    @GetMapping("/occupancy")
    public List<Map<String, Object>> occupancy() {
        return service.occupancy();
    }

    @PostMapping
    public Enclosure create(@RequestBody Enclosure form) {
        return service.save(form);
    }

    @PutMapping("/{id}")
    public Enclosure update(@PathVariable Long id, @RequestBody Enclosure form) {
        form.id = id;
        return service.save(form);
    }
}
