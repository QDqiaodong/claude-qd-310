package com.zoo.park.service;

import com.zoo.park.dto.BizException;
import com.zoo.park.entity.Animal;
import com.zoo.park.entity.Enclosure;
import com.zoo.park.repository.AnimalRepository;
import com.zoo.park.repository.EnclosureRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnimalService {

    private final AnimalRepository animals;
    private final EnclosureRepository enclosures;

    public AnimalService(AnimalRepository animals, EnclosureRepository enclosures) {
        this.animals = animals;
        this.enclosures = enclosures;
    }

    public List<Animal> list(Long enclosureId, String state, String keyword) {
        return animals.findAllByOrderByIdAsc().stream()
                .filter(a -> enclosureId == null || enclosureId.equals(a.enclosureId))
                .filter(a -> state == null || state.isBlank() || state.equals(a.animalState))
                .filter(a -> keyword == null || keyword.isBlank()
                        || a.animalCode.contains(keyword) || a.animalName.contains(keyword)
                        || a.species.contains(keyword))
                .toList();
    }

    @Transactional
    public Animal save(Animal form) {
        Animal existed = null;
        if (form.id != null) {
            existed = animals.findById(form.id).orElseThrow(() -> new BizException("动物档案不存在"));
            if (form.animalCode == null || form.animalCode.isBlank()) {
                form.animalCode = existed.animalCode;
            }
            if (form.animalName == null || form.animalName.isBlank()) {
                form.animalName = existed.animalName;
            }
            if (form.species == null || form.species.isBlank()) {
                form.species = existed.species;
            }
        }
        if (form.animalCode == null || form.animalName == null || form.species == null) {
            throw new BizException("编号、名字和物种都得填");
        }
        animals.findByAnimalCode(form.animalCode).ifPresent(o -> {
            if (!o.id.equals(form.id)) {
                throw new BizException("动物编号 " + form.animalCode + " 重复了");
            }
        });
        if (form.birthDate != null && form.birthDate.isAfter(LocalDate.now())) {
            throw new BizException("出生日期不能是未来的");
        }
        if (form.enclosureId != null) {
            Enclosure enc = enclosures.findById(form.enclosureId)
                    .orElseThrow(() -> new BizException("要住的笼舍不存在"));
            if ("维修".equals(enc.enclosureState)) {
                throw new BizException("笼舍「" + enc.enclosureName + "」在维修，住不了");
            }
            long now = animals.countByEnclosureId(form.enclosureId);
            if (enc.capacity != null && existed == null && now >= enc.capacity) {
                throw new BizException("笼舍「" + enc.enclosureName + "」已经住满（" + enc.capacity + " 位）");
            }
            if (existed == null) {
                form.enclosureId = enc.id;
            } else {
                existed.enclosureId = form.enclosureId;
            }
        }
        if (existed == null) {
            form.animalState = form.animalState == null || form.animalState.isBlank() ? "健康" : form.animalState;
            return animals.save(form);
        }
        if (form.birthDate != null) {
            existed.birthDate = form.birthDate;
        }
        if (form.animalState != null && !form.animalState.isBlank()) {
            existed.animalState = form.animalState;
        }
        existed.animalCode = form.animalCode;
        existed.animalName = form.animalName;
        existed.species = form.species;
        return animals.save(existed);
    }
}
