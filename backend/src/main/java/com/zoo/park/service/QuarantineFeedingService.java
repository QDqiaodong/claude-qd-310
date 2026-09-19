package com.zoo.park.service;

import com.zoo.park.dto.BizException;
import com.zoo.park.entity.Animal;
import com.zoo.park.entity.QuarantineFeeding;
import com.zoo.park.entity.VetCheck;
import com.zoo.park.repository.AnimalRepository;
import com.zoo.park.repository.QuarantineFeedingRepository;
import com.zoo.park.repository.VetCheckRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * 隔离加餐台。
 *
 * <p>兽医的规矩在这里收口：
 * <ul>
 *   <li>只收档案状态此刻仍是「隔离」的动物，状态不对整张单作废，绝不偷记进普通投喂流水；</li>
 *   <li>饲料名必须能在该动物<b>最近一张</b>巡查的备注里找到原文，找不到就提交失败，
 *       打开页之后补的新巡查在提交当口现查，照样认最新那张；</li>
 *   <li>同一只隔离动物一个日历日只能落一张加餐单，重复的那张（哪怕和人同时记）必须失败，
 *       失败原因带给值班员，失败的单子不进库，刷新也不会冒出来。</li>
 * </ul>
 */
@Service
public class QuarantineFeedingService {

    private final QuarantineFeedingRepository quarantineFeedings;
    private final AnimalRepository animals;
    private final VetCheckRepository checks;
    private final QuarantineFeedingInserter inserter;

    public QuarantineFeedingService(QuarantineFeedingRepository quarantineFeedings,
                                    AnimalRepository animals,
                                    VetCheckRepository checks,
                                    QuarantineFeedingInserter inserter) {
        this.quarantineFeedings = quarantineFeedings;
        this.animals = animals;
        this.checks = checks;
        this.inserter = inserter;
    }

    public List<QuarantineFeeding> list(Long animalId) {
        return quarantineFeedings.findAllByOrderByFeedDateDescIdDesc().stream()
                .filter(o -> animalId == null || animalId.equals(o.animalId))
                .toList();
    }

    /** 该动物最近一张巡查——提交和页面提示都现查，不缓存。 */
    public VetCheck latestCheck(Long animalId) {
        List<VetCheck> latest =
                checks.findByAnimalIdOrderByCheckDateDescIdDesc(animalId, PageRequest.of(0, 1));
        return latest.isEmpty() ? null : latest.get(0);
    }

    public QuarantineFeeding create(QuarantineFeeding form) {
        if (form.animalId == null) {
            throw new BizException("得指明加餐的是哪只动物");
        }
        if (form.foodName == null || form.foodName.isBlank()) {
            throw new BizException("饲料名不能空着");
        }
        if (form.amount == null || form.amount <= 0) {
            throw new BizException("加餐克数得大于 0");
        }
        String foodName = form.foodName.trim();

        // 提交当下再读一遍档案：状态不是隔离，整张单作废，不许落到任何普通投喂流水里。
        Animal animal = animals.findById(form.animalId)
                .orElseThrow(() -> new BizException("加餐的动物不存在"));
        if (!"隔离".equals(animal.animalState)) {
            throw new BizException("「" + animal.animalName + "」当前是「" + animal.animalState
                    + "」，不在隔离，加餐单作废，也不能记到普通投喂里");
        }

        // 提交当下现查最近一张巡查备注，页面打开后补的新巡查也认。
        VetCheck latest = latestCheck(animal.id);
        if (latest == null || latest.remark == null || latest.remark.isBlank()) {
            throw new BizException("「" + animal.animalName
                    + "」还没有带备注的巡查单，兽医没点头，加餐记不了");
        }
        String remark = latest.remark.trim();
        if (!remark.contains(foodName)) {
            throw new BizException("饲料「" + foodName + "」对不上「" + animal.animalName
                    + "」最近一张巡查的备注原文（" + remark + "），按兽医规矩这张单不能落");
        }

        LocalDate feedDate = form.feedDate != null ? form.feedDate : LocalDate.now();
        String keeperName = form.keeperName == null || form.keeperName.isBlank()
                ? "未署名" : form.keeperName.trim();

        // 顺手先查一次，给正常情况下的重复记一笔一句明白话；真正的并发抢注靠库唯一约束兜底。
        quarantineFeedings.findByAnimalIdAndFeedDate(animal.id, feedDate).ifPresent(first -> {
            throw new BizException(dayDuplicatedMessage(animal.animalName, feedDate, first));
        });

        // 单号按“动物 + 日历日”定，天然一天一张，和唯一约束对上。
        String code = "QF-" + animal.animalCode + "-" + feedDate;

        QuarantineFeeding order = new QuarantineFeeding();
        order.feedingCode = code;
        order.animalId = animal.id;
        order.feedDate = feedDate;
        order.foodName = foodName;
        order.amount = form.amount;
        order.keeperName = keeperName;
        order.createdAt = LocalDateTime.now();

        try {
            return inserter.insert(order);
        } catch (DataIntegrityViolationException ex) {
            // 几乎同时提交时后到的那笔：前一笔已经落下，唯一约束把这笔记成失败。
            QuarantineFeeding winner = quarantineFeedings
                    .findByAnimalIdAndFeedDate(animal.id, feedDate)
                    .orElse(null);
            if (winner != null) {
                throw new BizException(dayDuplicatedMessage(animal.animalName, feedDate, winner));
            }
            throw new BizException("这张加餐单没落下（数据冲突），请刷新后重试");
        }
    }

    private String dayDuplicatedMessage(String animalName, LocalDate feedDate, QuarantineFeeding first) {
        return "「" + animalName + "」在 " + feedDate + " 已有先成功的加餐单 "
                + first.feedingCode + "（" + first.keeperName + " 记的 "
                + first.foodName + " " + first.amount + " 克），同日不能再落第二张";
    }
}
