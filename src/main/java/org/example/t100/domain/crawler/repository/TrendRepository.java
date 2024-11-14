package org.example.t100.domain.crawler.repository;

import org.example.t100.domain.crawler.entity.Trend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrendRepository extends JpaRepository<Trend, Long> {
}
