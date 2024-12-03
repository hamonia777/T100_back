package org.example.t100.domain.openApi.repository;

import org.example.t100.domain.openApi.entity.OpenApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenApiRepository extends JpaRepository<OpenApi, Long> {
}
