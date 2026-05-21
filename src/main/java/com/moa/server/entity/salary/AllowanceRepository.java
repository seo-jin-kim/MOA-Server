package com.moa.server.entity.salary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllowanceRepository extends JpaRepository<AllowanceEntity, Integer> {
    Optional<AllowanceEntity> findByAllowanceName(String allowanceName);

    Optional<AllowanceEntity> findByAllowanceCord(String allowanceCode);

    // 수정/등록 창 수당 선택용
    @Query("SELECT a.allowanceCord, a.allowanceName " +
            "FROM AllowanceEntity a " +
            "WHERE a.allowanceCord LIKE CONCAT('%', :keyword, '%') OR a.allowanceName LIKE CONCAT('%', :keyword, '%')")
    List<Object[]> searchAllowanceByKeyword(@Param("keyword") String keyword);

    @Query("SELECT a.allowanceCord, a.allowanceName " +
            "FROM AllowanceEntity a ")
    List<Object[]> searchAllowance();
    //예시
    //List<BoardVOEntity> findByTitleContaining  (String title);

}

