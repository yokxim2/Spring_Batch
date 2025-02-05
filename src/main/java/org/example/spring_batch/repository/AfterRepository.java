package org.example.spring_batch.repository;

import org.example.spring_batch.entity.AfterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AfterRepository extends JpaRepository<AfterEntity, Long> {

}
