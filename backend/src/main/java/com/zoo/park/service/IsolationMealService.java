package com.zoo.park.service;

import com.zoo.park.dto.BizException;
import com.zoo.park.entity.Animal;
import com.zoo.park.entity.IsolationMeal;
import com.zoo.park.entity.VetCheck;
import com.zoo.park.repository.AnimalRepository;
import com.zoo.park.repository.IsolationMealRepository;
import com.zoo.park.repository.VetCheckRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IsolationMealService {

    private final IsolationMealRepository meals;
    private final AnimalRepository animals;
    private final VetCheckRepository checks;

    public IsolationMealService(IsolationMealRepository meals, AnimalRepository animals,
                                VetCheckRepository checks) {
        this.meals = meals;
        this.animals = animals;
        this.checks = checks;
    }

    public List<IsolationMeal> list() {
        return meals.findAllByOrderByIdDesc();
    }

    /**
     * 落一张隔离加餐单。所有校验都在提交当下重新查库：
     * 只收隔离中的动物；饲料名得能在该动物最新一张巡查备注里找到原文；
     * 同一只动物同一个日历日只留一张（数据库唯一索引兜底，并发时后到的那笔整体回滚）。
     */
    @Transactional
    public IsolationMeal save(IsolationMeal form) {
        if (form.animalId == null) {
            throw new BizException("得指明给哪只隔离动物加餐");
        }
        Animal animal = animals.findById(form.animalId)
                .orElseThrow(() -> new BizException("要加餐的动物不存在"));
        if (!"隔离".equals(animal.animalState)) {
            throw new BizException("「" + animal.animalName + "」这会儿不是隔离状态，整张加餐单作废");
        }
        if (form.foodName == null || form.foodName.isBlank()) {
            throw new BizException("饲料名不能空着");
        }
        form.foodName = form.foodName.trim();
        if (form.grams == null || form.grams <= 0) {
            throw new BizException("克数得大于 0");
        }
        if (form.keeperName == null || form.keeperName.isBlank()) {
            throw new BizException("当班饲养员得署名，夜里漏喂要能找到人");
        }
        form.keeperName = form.keeperName.trim();

        // 兽医的收口：认提交当下最新录入的那张巡查备注，页面打开后补的也算数
        VetCheck latest = checks.findFirstByAnimalIdOrderByIdDesc(form.animalId)
                .orElseThrow(() -> new BizException(
                        "「" + animal.animalName + "」还没有巡查备注，兽医没留话，不能加餐"));
        String remark = latest.remark == null ? "" : latest.remark;
        if (!remark.contains(form.foodName)) {
            throw new BizException("饲料「" + form.foodName + "」没出现在「" + animal.animalName
                    + "」最近一张巡查备注里，兽医不认");
        }

        form.id = null;
        form.mealDate = LocalDate.now();
        if (meals.existsByAnimalIdAndMealDate(form.animalId, form.mealDate)) {
            throw new BizException("「" + animal.animalName + "」今天已经落下一张加餐单了，同一天不能并存第二张");
        }
        // 单号等主键出来再回填，先放个一次性的临时值占位
        form.mealCode = "T" + UUID.randomUUID().toString().replace("-", "").substring(0, 18);
        IsolationMeal saved;
        try {
            saved = meals.saveAndFlush(form);
        } catch (DataIntegrityViolationException e) {
            // 两人同时落单：唯一索引只放行先到的，这笔整体回滚，不会留半截
            throw new BizException("「" + animal.animalName + "」今天已经落下一张加餐单了，同一天不能并存第二张");
        }
        saved.mealCode = "JM-" + saved.id;
        return meals.save(saved);
    }
}
