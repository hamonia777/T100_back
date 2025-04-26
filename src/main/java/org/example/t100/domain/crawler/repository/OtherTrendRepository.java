package org.example.t100.domain.crawler.repository;

import org.example.t100.domain.crawler.entity.OtherTrend;
import org.example.t100.domain.crawler.entity.Trend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtherTrendRepository extends JpaRepository<OtherTrend, Long> {

    List<OtherTrend> findByIdBetween(long l, long l1);

    List<OtherTrend> findAllByOrderByIdDesc(Pageable pageable);
}
