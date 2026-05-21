package com.moa.server.entity.approval;

import com.moa.server.common.BaseEntity;
import com.moa.server.entity.approval.dto.ApprovaLineCordMapDTO;
import com.moa.server.entity.inventory.dto.ProductCordMapDTO;
import com.moa.server.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "approval_line")
@Getter
@Setter // DTO 역할을 위해 세터를 열어줍니다
@NoArgsConstructor // JPA와 DTO 처리를 위한 기본 생성자
@AllArgsConstructor
@Builder
public class ApprovalLineEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_line_id")
    private Integer approvalLineId;

    @Column(name = "approval_line_cord")
    private String approvalLineCord;

    @Column(name = "approval_line_user")
    private Integer approvalLineUser;

    @Column(name = "approval_line_name")
    private String approvalLineName;

    @Column(name = "approval_line_is_use")
    private Integer approvalLineIsUse;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_line_user", insertable = false, updatable = false, referencedColumnName = "user_id")
    private UserEntity userApprover;

    public ApprovaLineCordMapDTO MapDTO() {
        return ApprovaLineCordMapDTO.builder()
                .approvalLineId(this.approvalLineId)
                .approvalLineCord(this.approvalLineCord)
                .approvalLineUser(this.approvalLineUser)
                .approvalLineName(this.approvalLineName)
                .userName(this.userApprover.getUserName())
                .employeeId(this.userApprover.getEmployeeId())
                .build();
    }

}
