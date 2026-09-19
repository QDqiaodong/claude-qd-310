package com.zoo.park.repository;

import com.zoo.park.entity.VetCheck;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VetCheckRepository extends JpaRepository<VetCheck, Long> {

    Optional<VetCheck> findByCheckCode(String checkCode);

    /** 该动物最新录入的一张巡查（提交加餐单时认这张的备注）。 */
    Optional<VetCheck> findFirstByAnimalIdOrderByIdDesc(Long animalId);

    List<VetCheck> findAllByOrderByIdDesc();

    long countByCheckResult(String checkResult);

    long countByVetName(String vetName);
}
