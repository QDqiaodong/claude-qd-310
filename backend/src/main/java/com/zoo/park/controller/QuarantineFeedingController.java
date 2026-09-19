package com.zoo.park.controller;

import com.zoo.park.entity.QuarantineFeeding;
import com.zoo.park.entity.VetCheck;
import com.zoo.park.service.QuarantineFeedingService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 隔离加餐台：普通投喂页挡下的隔离动物，走这条专门通道。 */
@RestController
@RequestMapping("/api/quarantine-feedings")
public class QuarantineFeedingController {

    private final QuarantineFeedingService service;

    public QuarantineFeedingController(QuarantineFeedingService service) {
        this.service = service;
    }

    @GetMapping
    public List<QuarantineFeeding> list(@RequestParam(required = false) Long animalId) {
        return service.list(animalId);
    }

    /** 某只动物最近一张巡查，前端选完动物现拉，提交时后端还会再认一次最新的。 */
    @GetMapping("/latest-check")
    public VetCheck latestCheck(@RequestParam Long animalId) {
        return service.latestCheck(animalId);
    }

    @PostMapping
    public QuarantineFeeding create(@RequestBody QuarantineFeeding form) {
        return service.create(form);
    }
}
