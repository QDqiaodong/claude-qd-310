package com.zoo.park.repository;

import com.zoo.park.entity.QuarantineFeeding;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuarantineFeedingRepository extends JpaRepository<QuarantineFeeding, Long> {

    Optional<QuarantineFeeding> findByFeedingCode(String feedingCode);

    List<QuarantineFeeding> findAllByOrderByFeedDateDescIdDesc();

    /** 同一只隔离动物某个日历日已经落下的加餐单——一天只许一张。 */
    Optional<QuarantineFeeding> findByAnimalIdAndFeedDate(Long animalId, java.time.LocalDate feedDate);
}
