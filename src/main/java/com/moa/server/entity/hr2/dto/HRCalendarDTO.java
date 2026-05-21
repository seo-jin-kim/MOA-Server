package com.moa.server.entity.hr2.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class HRCalendarDTO {

    private LocalDate workDate;
    private String userName;
    private String departmentName;
    private LocalDateTime startWork;
    private LocalDateTime finishWork;

    public HRCalendarDTO(
            LocalDate workDate,
            String userName,
            String departmentName,
            LocalDateTime startWork,
            LocalDateTime finishWork
    ) {
        this.workDate = workDate;
        this.userName = userName;
        this.departmentName = departmentName;
        this.startWork = startWork;
        this.finishWork = finishWork;
    }
}
