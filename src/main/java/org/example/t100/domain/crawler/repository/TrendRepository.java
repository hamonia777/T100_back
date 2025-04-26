package org.example.t100.domain.crawler.repository;

import org.example.t100.domain.crawler.entity.Trend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrendRepository extends JpaRepository<Trend, Long> {
    List<Trend> findByIdBetween(long l, long l1);

    List<Trend> findAllByOrderByIdDesc(Pageable pageable);
}
