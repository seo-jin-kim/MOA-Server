package com.moa.server.entity.hr2.dto;

import com.moa.server.entity.vacation.WorkEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LatenessDTO {
    private String workDate;
    private String userName;
    private final String startTime = "09:00";
    private String startWork;
    private String departmentName; // 부서명 (필터용)

    public static LatenessDTO fromEntity(WorkEntity entity) {
        return LatenessDTO.builder()
                .workDate(entity.getWorkDate()!= null ? entity.getWorkDate().toString() : null)
                .userName(entity.getUser().getUserName())
                .startWork(entity.getStartWork()!= null ? entity.getWorkDate().toString() : null)
                .departmentName(entity.getUser().getDepartment() != null ?
                        entity.getUser().getDepartment().getDepartmentName() : "소속없음")
                .build();
    }
}
