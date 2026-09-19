package com.zoo.park.controller;

import com.zoo.park.entity.VetCheck;
import com.zoo.park.service.VetCheckService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checks")
public class VetCheckController {

    private final VetCheckService service;

    public VetCheckController(VetCheckService service) {
        this.service = service;
    }

    @GetMapping
    public List<VetCheck> list(@RequestParam(required = false) Long animalId,
                               @RequestParam(required = false) String result,
                               @RequestParam(required = false) String keyword) {
        return service.list(animalId, result, keyword);
    }

    /** 体检概况：正常 / 异常 / 今天查了几只。 */
    @GetMapping("/overview")
    public Map<String, Object> overview() {
        return service.overview();
    }

    @PostMapping
    public VetCheck create(@RequestBody VetCheck form) {
        return service.save(form);
    }
}
