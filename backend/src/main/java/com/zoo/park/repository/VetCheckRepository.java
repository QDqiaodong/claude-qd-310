package com.zoo.park.repository;

import com.zoo.park.entity.VetCheck;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VetCheckRepository extends JpaRepository<VetCheck, Long> {

    Optional<VetCheck> findByCheckCode(String checkCode);

    List<VetCheck> findAllByOrderByIdDesc();

    /** 某只动物最近一张巡查：先比日期，日期相同（或都空着）再按录入顺序，取最新的那张。 */
    List<VetCheck> findByAnimalIdOrderByCheckDateDescIdDesc(Long animalId, Pageable pageable);

    long countByCheckResult(String checkResult);

    long countByVetName(String vetName);
}
