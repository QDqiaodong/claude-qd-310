package com.zoo.park.repository;

import com.zoo.park.entity.Feeding;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedingRepository extends JpaRepository<Feeding, Long> {

    Optional<Feeding> findByFeedingCode(String feedingCode);

    List<Feeding> findAllByOrderByIdDesc();

    /** 每位饲养员喂了多少次、一共喂了多少量。 */
    @Query("select f.keeperName as keeperName, count(f) as times, sum(f.amount) as totalAmount"
            + " from Feeding f group by f.keeperName order by count(f) desc")
    List<Map<String, Object>> summaryByKeeper();
}
