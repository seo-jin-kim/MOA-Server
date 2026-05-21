package com.moa.server.entity.vacation;

import com.moa.server.entity.hr2.dto.HRCalendarDTO;
import com.moa.server.entity.hr2.dto.HRCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<WorkEntity, Integer> {

//  중복쿼리 봐함
//   @Query("SELECT w FROM WorkEntity w JOIN FETCH w.user LEFT JOIN FETCH w.allowance")
//   List<WorkEntity> findAllWithDetails();

    WorkEntity findByUserIdAndWorkDate(Integer userId, LocalDate workDate);

    @Query("SELECT w FROM WorkEntity w JOIN FETCH w.user LEFT JOIN FETCH w.allowance WHERE w.userId IN :userIds")
    List<WorkEntity> findByUserIdIn(@Param("userIds") Collection<Integer> userIds);

    //근무기록
    @Query(value = "SELECT w FROM WorkEntity w " +
            "JOIN FETCH w.user " +
            "LEFT JOIN FETCH w.allowance " +
            "WHERE (CAST(:keyword AS string) IS NULL OR w.user.userName LIKE CONCAT('%', CAST(:keyword AS string), '%')) " +
            "AND (CAST(:category AS string) IS NULL OR w.allowance.allowanceName = CAST(:category AS string)) " +
            "AND (:startDate IS NULL OR w.workDate >= :startDate) " +
            "AND (:finishDate IS NULL OR w.workDate <= :finishDate)",
            countQuery = "SELECT COUNT(w) FROM WorkEntity w " +
                    "WHERE (CAST(:keyword AS string) IS NULL OR w.user.userName LIKE CONCAT('%', CAST(:keyword AS string), '%')) " +
                    "AND (CAST(:category AS string) IS NULL OR w.allowance.allowanceName = :category)")
            Page<WorkEntity> findAllWithDetails(
                    @Param("startDate") LocalDateTime startDate,
                    @Param("finishDate") LocalDateTime finishDate,
                    @Param("category") String category,
                    @Param("keyword") String keyword,
                    Pageable pageable);


//    @Query("SELECT w FROM WorkEntity w " +
//            "JOIN FETCH w.user " +           // 유저 정보 한방에
//            "LEFT JOIN FETCH w.allowance" +  // 수당 정보 한방에 (없을 수 있으니 LEFT)
//            " WHERE (:keyword IS NULL OR w.user.userName LIKE CONCAT('%', :keyword, '%')) " +
//            "AND (:category IS NULL OR w.allowance.allowanceName = :category) " +
//            "AND (:startDate IS NULL OR w.workDate >= :startDate) " +
//            "AND (:finishDate IS NULL OR w.workDate <= :finishDate)")
//    Page<WorkEntity> findAllWithDetails(
//            @Param("startDate") LocalDateTime startDate,
//            @Param("finishDate") LocalDateTime finishDate,
//            @Param("category") String category,
//            @Param("keyword") String keyword,
//            Pageable pageable);

    //지각현황
    @Query("SELECT w FROM WorkEntity w " +
            "JOIN FETCH w.user u " +
            "LEFT JOIN FETCH u.department d" +
            " WHERE w.workStatus = '정상' " +      // 조건 1: 정상 상태
            "AND (HOUR(w.startWork) > 9 OR (HOUR(w.startWork) = 9 AND MINUTE(w.startWork) > 0))" +    // 조건 2: 9시 이후 출근
            "AND (:startDate IS NULL OR w.workDate >= :startDate) " +
            "AND (:finishDate IS NULL OR w.workDate <= :finishDate) " +
            "AND (:category IS NULL OR d.departmentName = :category)" +
            "AND (:keyword IS NULL OR u.userName LIKE CONCAT('%', :keyword, '%'))")
    // 사원명 검색
    Page<WorkEntity> findLateness(
            @Param("startDate") LocalDateTime startDate,
            @Param("finishDate") LocalDateTime finishDate,
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    //출퇴근인원수
    @Query("SELECT new com.moa.server.entity.hr2.dto.HRCountDTO(w.workDate, COUNT(w)) " +
            "FROM WorkEntity w " +
            "WHERE CAST(w.workDate AS localdatetime) >= :startDate " + // 필드를 LocalDateTime으로 형변환
            "AND CAST(w.workDate AS localdatetime) < :endDate " +
            "GROUP BY w.workDate " +
            "ORDER BY w.workDate ASC")
    List<HRCountDTO> findWorkCount(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


//    @Query("SELECT new com.moa.server.entity.hr2.dto.HRCountDTO(" +
//            "FUNCTION('DATE', w.workDate), " +
//            "COUNT(w)) " +
//            "FROM WorkEntity w " +
//            "WHERE w.workDate >= :startDate AND w.workDate < :endDate " +
//            "GROUP BY DATE(w.workDate) " +
//            "ORDER BY DATE(w.workDate)")
//    List<HRCountDTO> findWorkCount(
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate
//    );

    @Query("SELECT new com.moa.server.entity.hr2.dto.HRCalendarDTO(" +
            "w.workDate, " +
            "u.userName, " +
            "u.department.departmentName, " +
            "w.startWork, " +
            "w.finishWork) " +
            "FROM WorkEntity w " +
            "JOIN w.user u " +
            "WHERE w.workDate >= :startDate AND w.workDate < :endDate " +
            "ORDER BY w.workDate DESC, u.userName ASC")
    List<HRCalendarDTO> findWorkDetailList(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
