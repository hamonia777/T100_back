package org.example.t100.domain.openApi.repository;

import org.example.t100.domain.openApi.entity.EnterOpenApi;
import org.example.t100.domain.openApi.entity.OpenApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterApiRepository extends JpaRepository<EnterOpenApi, Long> {
    EnterOpenApi findFirstByOrderByIdDesc();
}
