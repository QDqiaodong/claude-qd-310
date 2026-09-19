package com.zoo.park.service;

import com.zoo.park.dto.BizException;
import com.zoo.park.entity.Animal;
import com.zoo.park.entity.VetCheck;
import com.zoo.park.repository.AnimalRepository;
import com.zoo.park.repository.VetCheckRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VetCheckService {

    private final VetCheckRepository checks;
    private final AnimalRepository animals;

    public VetCheckService(VetCheckRepository checks, AnimalRepository animals) {
        this.checks = checks;
        this.animals = animals;
    }

    public List<VetCheck> list(Long animalId, String result, String keyword) {
        return checks.findAllByOrderByIdDesc().stream()
                .filter(c -> animalId == null || animalId.equals(c.animalId))
                .filter(c -> result == null || result.isBlank() || result.equals(c.checkResult))
                .filter(c -> keyword == null || keyword.isBlank()
                        || c.checkCode.contains(keyword) || c.vetName.contains(keyword))
                .toList();
    }

    /** 园区体检概况：正常多少、异常多少、今天查了几只。 */
    public Map<String, Object> overview() {
        Map<String, Object> out = new HashMap<>();
        out.put("normalCount", checks.countByCheckResult("正常"));
        out.put("abnormalCount", checks.countByCheckResult("异常"));
        out.put("totalAnimals", animals.count());
        out.put("watchingCount", animals.countByAnimalState("观察"));
        long today = checks.findAllByOrderByIdDesc().stream()
                .filter(c -> LocalDate.now().equals(c.checkDate))
                .count();
        out.put("todayChecks", today);
        return out;
    }

    @Transactional
    public VetCheck save(VetCheck form) {
        if (form.checkCode == null || form.checkCode.isBlank()) {
            throw new BizException("巡查单号不能空着");
        }
        form.checkCode = form.checkCode.trim();
        checks.findByCheckCode(form.checkCode).ifPresent(o -> {
            if (!o.id.equals(form.id)) {
                throw new BizException("巡查单号 " + form.checkCode + " 重复了");
            }
        });
        if (form.animalId == null) {
            throw new BizException("得指明巡查的是哪只动物");
        }
        animals.findById(form.animalId).orElseThrow(() -> new BizException("被巡查的动物不存在"));
        if (form.checkDate != null && form.checkDate.isAfter(LocalDate.now())) {
            throw new BizException("巡查日期不能填到未来");
        }
        if ("异常".equals(form.checkResult) && (form.remark == null || form.remark.isBlank())) {
            throw new BizException("判成异常的话，备注得写点东西");
        }
        if (form.checkResult == null || form.checkResult.isBlank()) {
            form.checkResult = "正常";
        }
        form.vetName = form.vetName == null || form.vetName.isBlank() ? "未署名" : form.vetName.trim();
        return checks.save(form);
    }
}
