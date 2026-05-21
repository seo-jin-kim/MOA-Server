package com.moa.server.entity.hr2.dto;

import com.moa.server.entity.approval.ApprovaEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalWaitDTO {

    private Integer approvaId;
    private String approvaDate;
    private String approvaTitle;
    private String writer;
    private String documentName;
    private String approvaState;
    private String memo;
    private String departmentName; // 탭 구분
    private Integer departmentId;

    public static ApprovalWaitDTO fromEntity(ApprovaEntity entity) {
        if (entity == null) return null;

        // 1. 부서 ID 안전하게 가져오기
        Integer deptIdStr = 0;
        if (entity.getUserWriter() != null && entity.getUserWriter().getDepartment() != null) {
            deptIdStr = entity.getUserWriter().getDepartment().getDepartmentId();
        }

        return ApprovalWaitDTO.builder()
                .approvaId(entity.getApprovaId())
                // 날짜 null 체크 추가 (데이터 꼬임 대비)
                .approvaDate(entity.getApprovaDate() != null ? entity.getApprovaDate().toString() : "")
                .approvaTitle(entity.getApprovaTitle())
                .writer(entity.getUserWriter() != null ? entity.getUserWriter().getUserName() : "알 수 없음")
                .approvaState(entity.getApprovaStatus())
                .documentName(entity.getLine() != null ? entity.getLine().getDocumentName() : "삭제된 양식")
                .departmentId(deptIdStr)
                .memo("")
                .build();
    }
}
