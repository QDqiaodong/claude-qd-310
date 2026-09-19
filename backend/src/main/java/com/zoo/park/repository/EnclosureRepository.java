package com.zoo.park.repository;

import com.zoo.park.entity.Enclosure;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnclosureRepository extends JpaRepository<Enclosure, Long> {
    Optional<Enclosure> findByEnclosureCode(String enclosureCode);
    List<Enclosure> findAllByOrderByIdAsc();
}
