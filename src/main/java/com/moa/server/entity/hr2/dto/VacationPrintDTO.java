package com.moa.server.entity.hr2.dto;

import com.moa.server.entity.approval.ApprovaEntity;
import com.moa.server.entity.vacation.VacationEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationPrintDTO {
    private String departmentName;
    private String employeeId;
    private String userName;
    private Double basicVacation;
    private Double useVacation;
    private Double remainingVacation;
    private Integer vacationId;
    private String approvaDate;

    public static VacationPrintDTO fromEntity(VacationEntity entity) {

        String stringDate = entity.getUser().getApprova().stream()
                .filter(a -> "결재".equals(a.getApprovaStatus())) // 상태가 '결재'인 것만 필터링
                .map(a -> a.getApprovaDate().toString())  // 날짜를 문자로 변환
                .findFirst()                             // 그중 첫 번째 것
                .orElse("-");

        return VacationPrintDTO.builder()
                .departmentName(entity.getUser().getDepartment() != null ?
                        entity.getUser().getDepartment().getDepartmentName() : "소속없음")
                .employeeId(entity.getUser().getEmployeeId())
                .userName(entity.getUser().getUserName())
                .basicVacation(entity.getBasicVacation() != null ?
                        entity.getBasicVacation().getBasicVacationDay().doubleValue() : 0.00)
                .useVacation(entity.getUseVacation()!= null ?
                        entity.getUseVacation().doubleValue() : 0.00)
                .remainingVacation(entity.getRemainingVacation()!= null ?
                        entity.getRemainingVacation().doubleValue() : 0.00)
                .vacationId(entity.getVacationId())
                .approvaDate(stringDate)
                .build();
    }

}
