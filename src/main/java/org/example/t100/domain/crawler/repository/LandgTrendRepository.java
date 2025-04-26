package org.example.t100.domain.crawler.repository;

import org.example.t100.domain.crawler.entity.BandfTrend;
import org.example.t100.domain.crawler.entity.LandgTrend;
import org.example.t100.domain.crawler.entity.Trend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LandgTrendRepository extends JpaRepository<LandgTrend, Long> {
    List<LandgTrend> findByIdBetween(long l, long l1);

    List<LandgTrend> findAllByOrderByIdDesc(Pageable pageable);
}
