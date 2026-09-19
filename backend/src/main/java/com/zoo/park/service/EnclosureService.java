package com.zoo.park.service;

import com.zoo.park.dto.BizException;
import com.zoo.park.entity.Enclosure;
import com.zoo.park.repository.AnimalRepository;
import com.zoo.park.repository.EnclosureRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnclosureService {

    private final EnclosureRepository enclosures;
    private final AnimalRepository animals;

    public EnclosureService(EnclosureRepository enclosures, AnimalRepository animals) {
        this.enclosures = enclosures;
        this.animals = animals;
    }

    public List<Enclosure> list(String state, String keyword) {
        return enclosures.findAllByOrderByIdAsc().stream()
                .filter(e -> state == null || state.isBlank() || state.equals(e.enclosureState))
                .filter(e -> keyword == null || keyword.isBlank()
                        || e.enclosureCode.contains(keyword) || e.enclosureName.contains(keyword))
                .toList();
    }

    /** 每个笼舍的居住情况：住了几只、平均几岁。 */
    public List<Map<String, Object>> occupancy() {
        return animals.countGroupByEnclosure();
    }

    @Transactional
    public Enclosure save(Enclosure form) {
        Enclosure existed = null;
        if (form.id != null) {
            existed = enclosures.findById(form.id).orElseThrow(() -> new BizException("笼舍不存在"));
            if (form.enclosureCode == null || form.enclosureCode.isBlank()) {
                form.enclosureCode = existed.enclosureCode;
            }
            if (form.enclosureName == null || form.enclosureName.isBlank()) {
                form.enclosureName = existed.enclosureName;
            }
        }
        if (form.enclosureCode == null || form.enclosureName == null) {
            throw new BizException("笼舍编号和名称都得填");
        }
        enclosures.findByEnclosureCode(form.enclosureCode).ifPresent(o -> {
            if (!o.id.equals(form.id)) {
                throw new BizException("笼舍编号 " + form.enclosureCode + " 重复了");
            }
        });
        if (form.capacity != null && form.capacity <= 0) {
            throw new BizException("可容纳数得是正数");
        }
        if (existed == null) {
            form.enclosureState = form.enclosureState == null || form.enclosureState.isBlank() ? "开放" : form.enclosureState;
            return enclosures.save(form);
        }
        if ("维修".equals(form.enclosureState) && !"维修".equals(existed.enclosureState)
                && animals.countByEnclosureId(form.id) > 0) {
            throw new BizException("这个笼舍里还住着动物，先迁走再报修");
        }
        if (form.zoneArea != null) {
            existed.zoneArea = form.zoneArea;
        }
        if (form.capacity != null) {
            existed.capacity = form.capacity;
        }
        if (form.enclosureState != null && !form.enclosureState.isBlank()) {
            existed.enclosureState = form.enclosureState;
        }
        existed.enclosureCode = form.enclosureCode;
        existed.enclosureName = form.enclosureName;
        return enclosures.save(existed);
    }
}
