package com.moa.server.entity.inventory;

import com.moa.server.entity.user.AdminRoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrdererRepository extends JpaRepository<OrdererEntity, Integer> {

    @EntityGraph(attributePaths = {"product", "orderform" , "orderform.vendor"})
    List<OrdererEntity> findByProduct_ProductNameContaining(String productName);

    @EntityGraph(attributePaths = {"product", "orderform" , "orderform.vendor" })
    Page<OrdererEntity> findByOrderformId(Integer orderformId, Pageable pageable);

    void deleteByOrderformId(Integer orderformId);

    @EntityGraph(attributePaths = {"product", "orderform", "orderform.vendor"})
    List<OrdererEntity> findByOrderformId(Integer orderFormId);
}

