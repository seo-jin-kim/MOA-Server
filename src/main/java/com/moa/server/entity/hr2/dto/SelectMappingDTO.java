package com.moa.server.entity.hr2.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectMappingDTO {

    private String employeeId;
    private String userName;
    private String allowanceCord;
    private String allowanceName;
}
