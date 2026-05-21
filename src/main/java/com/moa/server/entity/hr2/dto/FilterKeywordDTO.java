package com.moa.server.entity.hr2.dto;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterKeywordDTO {
    private String userName;
    private String employeeId;
    private String departmentName;
    private String departmentId;
    private String documentName;
    private String documentId;
}
