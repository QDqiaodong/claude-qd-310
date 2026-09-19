package com.zoo.park.service;

import com.zoo.park.dto.BizException;
import com.zoo.park.entity.Animal;
import com.zoo.park.entity.Feeding;
import com.zoo.park.repository.AnimalRepository;
import com.zoo.park.repository.FeedingRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedingService {

    private final FeedingRepository feedings;
    private final AnimalRepository animals;

    public FeedingService(FeedingRepository feedings, AnimalRepository animals) {
        this.feedings = feedings;
        this.animals = animals;
    }

    public List<Feeding> list(Long animalId, String keeper, String keyword) {
        return feedings.findAllByOrderByIdDesc().stream()
                .filter(f -> animalId == null || animalId.equals(f.animalId))
                .filter(f -> keeper == null || keeper.isBlank() || keeper.equals(f.keeperName))
                .filter(f -> keyword == null || keyword.isBlank()
                        || f.feedingCode.contains(keyword) || f.foodName.contains(keyword))
                .toList();
    }

    /** 每位饲养员的投喂汇总，直接由数据库聚合出来。 */
    public List<Map<String, Object>> keeperSummary() {
        return feedings.summaryByKeeper();
    }

    @Transactional
    public Feeding save(Feeding form) {
        if (form.feedingCode == null || form.feedingCode.isBlank()) {
            throw new BizException("投喂单号不能空着");
        }
        form.feedingCode = form.feedingCode.trim();
        feedings.findByFeedingCode(form.feedingCode).ifPresent(o -> {
            if (!o.id.equals(form.id)) {
                throw new BizException("投喂单号 " + form.feedingCode + " 重复了");
            }
        });
        if (form.animalId == null) {
            throw new BizException("得指明喂的是哪只动物");
        }
        Animal animal = animals.findById(form.animalId)
                .orElseThrow(() -> new BizException("要喂的动物不存在"));
        if ("隔离".equals(animal.animalState)) {
            throw new BizException("「" + animal.animalName + "」正在隔离，投喂要走专门通道");
        }
        if (form.amount == null || form.amount <= 0) {
            throw new BizException("投喂量得大于 0");
        }
        if (form.foodName == null || form.foodName.isBlank()) {
            throw new BizException("饲料名不能空着");
        }
        form.keeperName = form.keeperName == null || form.keeperName.isBlank() ? "未署名" : form.keeperName.trim();
        form.foodName = form.foodName.trim();
        return feedings.save(form);
    }
}
