package com.moa.server.entity.approval;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface ApprovaRepository extends JpaRepository<ApprovaEntity, Integer> {

    //내가 신청한 결재목록
    @EntityGraph(attributePaths = {"line", "userWriter", "userWriter.department", "userWriter.grade", "lineApprover" , "lineApprover.userApprover" , "lineApprover.userApprover.department", "lineApprover.userApprover.grade"})
    Page<ApprovaEntity> findByWriter(Integer writer, Pageable pageable );

    // 결재 상세정보 / 팀장용 결재 상세 조회
    @EntityGraph(attributePaths = {"line", "userWriter", "userWriter.department", "userWriter.grade", "lineApprover" , "lineApprover.userApprover" , "lineApprover.userApprover.department", "lineApprover.userApprover.grade"})
    Page<ApprovaEntity> findByApprovaId( Integer approvaId ,Pageable pageable );

    //팀장용 결재목록 조회
    @EntityGraph(attributePaths = {"line", "userWriter", "userWriter.department", "userWriter.grade", "lineApprover" , "lineApprover.userApprover" , "lineApprover.userApprover.department", "lineApprover.userApprover.grade"})
    Page<ApprovaEntity> findByLineApprover_ApprovalLineUser(Integer approvalLineUser, Pageable pageable );

    //  미결재 결재 삭제
    void deleteByApprovaId(Integer approvaId);

    // 팀장 결제 내역 반려 / 결재 처리 approvalAct/{approva_id}
    @Modifying
    @Transactional
    @Query("UPDATE ApprovaEntity a SET a.approvaStatus = :approvaStatus WHERE a.approvaId = :approvaId")
    int updateApprovaIdApprovaStatus(Integer  approvaId , String approvaStatus);


    @Query(value = "SELECT a FROM ApprovaEntity a " +
            "LEFT JOIN FETCH a.userWriter u " +
            "LEFT JOIN FETCH u.department d " +
            "LEFT JOIN FETCH a.line dc " +
            "WHERE (CAST(:startDate AS string) IS NULL OR a.approvaDate >= :startDate) " +
            "AND (CAST(:finishDate AS string) IS NULL OR a.approvaDate <= :finishDate) " +
            "AND (CAST(:category AS string) IS NULL OR dc.documentName = CAST(:category AS string)) " +
            // 핵심 수정: LIKE 절 내부의 파라미터까지 CAST로 감싸야 합니다.
            "AND (CAST(:keyword AS string) IS NULL OR u.userName LIKE CONCAT('%', CAST(:keyword AS string), '%')) " +
            "AND (CAST(:departmentId AS string) IS NULL OR d.departmentId = :departmentId)",
            countQuery = "SELECT COUNT(a) FROM ApprovaEntity a " +
                    "LEFT JOIN a.userWriter u " +
                    "LEFT JOIN u.department d " +
                    "LEFT JOIN a.line dc " +
                    "WHERE (CAST(:startDate AS string) IS NULL OR a.approvaDate >= :startDate) " +
                    "AND (CAST(:finishDate AS string) IS NULL OR a.approvaDate <= :finishDate) " +
                    "AND (CAST(:category AS string) IS NULL OR dc.documentName = CAST(:category AS string)) " +
                    "AND (CAST(:keyword AS string) IS NULL OR u.userName LIKE CONCAT('%', CAST(:keyword AS string), '%')) " +
                    "AND (CAST(:departmentId AS string) IS NULL OR d.departmentId = :departmentId)")
    Page<ApprovaEntity> findApprovalList(
            @Param("startDate") LocalDateTime startDate,
            @Param("finishDate") LocalDateTime finishDate,
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("departmentId") Integer departmentId,
            Pageable pageable);


//    @Query("SELECT a FROM ApprovaEntity a " +
//            "JOIN FETCH a.userWriter u " +
//            "LEFT JOIN u.department d " +
//            "LEFT JOIN a.line dc " +
//            "WHERE (:startDate IS NULL OR a.approvaDate >= :startDate) " +
//            "AND (:finishDate IS NULL OR a.approvaDate <= :finishDate) " +
//            "AND (:category IS NULL OR dc.documentName = :category) " +
//            "AND (:keyword IS NULL OR u.userName LIKE CONCAT('%', :keyword, '%')) " +
//            "AND (:departmentId IS NULL OR d.departmentId = :departmentId)")
//    Page<ApprovaEntity> findApprovalList(
//            @Param("startDate") LocalDateTime startDate,
//            @Param("finishDate") LocalDateTime finishDate,
//            @Param("category") String category,
//            @Param("keyword") String keyword,
//            @Param("departmentId") Integer departmentId,
//            Pageable pageable);
}

