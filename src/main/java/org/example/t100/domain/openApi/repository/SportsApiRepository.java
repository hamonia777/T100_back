package org.example.t100.domain.openApi.repository;

import org.example.t100.domain.openApi.entity.OpenApi;
import org.example.t100.domain.openApi.entity.SportsOpenApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportsApiRepository extends JpaRepository<SportsOpenApi, Long> {
    SportsOpenApi findFirstByOrderByIdDesc();
}
