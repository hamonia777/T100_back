package org.example.t100.domain.crawler.repository;

import org.example.t100.domain.crawler.entity.BandfTrend;
import org.example.t100.domain.crawler.entity.EnterTrend;
import org.example.t100.domain.crawler.entity.Trend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnterTrendRepository extends JpaRepository<EnterTrend, Long> {
    List<EnterTrend> findByIdBetween(long l, long l1);

    List<EnterTrend> findAllByOrderByIdDesc(Pageable pageable);
}

