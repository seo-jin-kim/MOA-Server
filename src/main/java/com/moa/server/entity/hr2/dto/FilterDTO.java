package com.moa.server.entity.hr2.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterDTO {

    private String startDate;
    private String finishDate;
    private String category;
    private String keyword;
    private Integer departmentId;
    private String departmentName;

}
