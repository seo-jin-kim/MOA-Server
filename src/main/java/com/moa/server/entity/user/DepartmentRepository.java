package com.moa.server.entity.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Integer> {

    //findById는 기본제공

    Optional<DepartmentEntity> findByDepartmentId(Integer departmentId);

    List<DepartmentEntity> findByDepartmentNameContaining(String departmentName);
    
}

