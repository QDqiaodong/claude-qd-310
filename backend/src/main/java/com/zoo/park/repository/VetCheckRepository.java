package com.zoo.park.repository;

import com.zoo.park.entity.VetCheck;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VetCheckRepository extends JpaRepository<VetCheck, Long> {

    Optional<VetCheck> findByCheckCode(String checkCode);

    List<VetCheck> findAllByOrderByIdDesc();

    long countByCheckResult(String checkResult);

    long countByVetName(String vetName);
}
