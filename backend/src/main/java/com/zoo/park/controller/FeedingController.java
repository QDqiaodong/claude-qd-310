package com.zoo.park.controller;

import com.zoo.park.entity.Feeding;
import com.zoo.park.service.FeedingService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedings")
public class FeedingController {

    private final FeedingService service;

    public FeedingController(FeedingService service) {
        this.service = service;
    }

    @GetMapping
    public List<Feeding> list(@RequestParam(required = false) Long animalId,
                              @RequestParam(required = false) String keeper,
                              @RequestParam(required = false) String keyword) {
        return service.list(animalId, keeper, keyword);
    }

    /** 饲养员投喂汇总。 */
    @GetMapping("/by-keeper")
    public List<Map<String, Object>> byKeeper() {
        return service.keeperSummary();
    }

    @PostMapping
    public Feeding create(@RequestBody Feeding form) {
        return service.save(form);
    }
}
