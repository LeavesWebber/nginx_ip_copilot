package org.example.backend.IPmanagement.repository;

import org.example.backend.IPmanagement.model.IpRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpRuleRepository extends JpaRepository<IpRule, String> {
    // 基础的CRUD操作由JpaRepository提供
} 