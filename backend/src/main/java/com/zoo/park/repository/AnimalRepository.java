package com.zoo.park.repository;

import com.zoo.park.entity.Animal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    Optional<Animal> findByAnimalCode(String animalCode);

    List<Animal> findAllByOrderByIdAsc();

    long countByEnclosureId(Long enclosureId);

    long countByAnimalState(String animalState);

    /**
     * 每个笼舍住了几只、平均年龄多大 —— 这类汇总直接让数据库算完再拿回来。
     */
    @Query("select a.enclosureId as enclosureId, count(a) as animalCount,"
            + " avg(year(current_date) - year(a.birthDate)) as avgAge"
            + " from Animal a where a.enclosureId is not null group by a.enclosureId")
    List<Map<String, Object>> countGroupByEnclosure();
}
