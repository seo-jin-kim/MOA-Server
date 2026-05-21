package com.moa.server.entity.hr2.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class HRCountDTO {

    private LocalDate date;
    private Long totalCount;
    private List<HRCalendarDTO> details;


    public HRCountDTO(LocalDate date, Long totalCount) {
        this.date = date;
        this.totalCount = totalCount;
    }

    public HRCountDTO(LocalDate date, Long totalCount, List<HRCalendarDTO> details){
        this.date = date;
        this.totalCount = totalCount;
        this.details=details;
    }

}
