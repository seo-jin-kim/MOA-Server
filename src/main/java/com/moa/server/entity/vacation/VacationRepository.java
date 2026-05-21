package com.moa.server.entity.vacation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<VacationEntity, Integer> {

    List<VacationEntity> findByUserIdIn(Collection<Integer> userIds);

    @Query(value = "SELECT v FROM VacationEntity v " +
            "JOIN FETCH v.user u " +
            "LEFT JOIN FETCH u.department d " +
            "LEFT JOIN FETCH v.basicVacation bv " +
            "JOIN u.approva a " +
            "WHERE a.approvaStatus = '결재' " +
            "AND (CAST(:startDate AS string) IS NULL OR a.approvaDate >= :startDate) " + // 날짜 캐스팅 추가
            "AND (CAST(:finishDate AS string) IS NULL OR a.approvaDate <= :finishDate) " + // 날짜 캐스팅 추가
            "AND (CAST(:category AS string) IS NULL OR d.departmentName = CAST(:category AS string)) " +
            "AND (CAST(:keyword AS string) IS NULL OR u.userName LIKE CONCAT('%', CAST(:keyword AS string), '%'))",
            countQuery = "SELECT COUNT(v) FROM VacationEntity v " +
                    "JOIN v.user u " +
                    "JOIN u.approva a " +
                    "LEFT JOIN u.department d " +
                    "WHERE a.approvaStatus = '결재' " +
                    "AND (CAST(:startDate AS string) IS NULL OR a.approvaDate >= :startDate) " + // 날짜 캐스팅 추가
                    "AND (CAST(:finishDate AS string) IS NULL OR a.approvaDate <= :finishDate) " + // 날짜 캐스팅 추가
                    "AND (CAST(:category AS string) IS NULL OR d.departmentName = CAST(:category AS string)) " +
                    "AND (CAST(:keyword AS string) IS NULL OR u.userName LIKE CONCAT('%', CAST(:keyword AS string), '%'))")
    Page<VacationEntity> findVacationPrint(
            @Param("startDate") LocalDateTime startDate,
            @Param("finishDate") LocalDateTime finishDate,
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable);



//    @Query("SELECT v FROM VacationEntity v " +
//            "JOIN FETCH v.user u " +
//            "LEFT JOIN FETCH u.department d " +
//            "LEFT JOIN FETCH v.basicVacation bv " + // 휴가 일수 정보 조인
//            "JOIN u.approva a " +
//            " WHERE a.approvaStatus = '결재' "+
//            "AND (:startDate IS NULL OR a.approvaDate >= :startDate) " +
//            "AND (:finishDate IS NULL OR a.approvaDate <= :finishDate) " +
//            "AND (:category IS NULL OR d.departmentName = :category) " +
//            "AND (:keyword IS NULL OR u.userName LIKE CONCAT('%', :keyword, '%'))")
//    Page<VacationEntity> findVacationPrint(
//            @Param("startDate") LocalDateTime startDate,
//            @Param("finishDate") LocalDateTime finishDate,
//            @Param("category") String category,
//            @Param("keyword") String keyword,
//            Pageable pageable);

   
}

