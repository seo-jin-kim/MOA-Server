package com.moa.server.entity.approval;

import com.moa.server.common.BaseEntity;
import com.moa.server.entity.approval.dto.ApprovaUserDTO;
import com.moa.server.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "approva")
@Getter
@Setter // DTO 역할을 위해 세터를 열어줍니다
@NoArgsConstructor // JPA와 DTO 처리를 위한 기본 생성자
@AllArgsConstructor
@Builder
public class ApprovaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approva_id")
    private Integer approvaId;

    @Column(name = "approva_title")
    private String approvaTitle;

    @Column(name = "approva_content")
    private String approvaContent;

    @Column(name = "writer")
    private Integer writer;

    @Column(name = "approva_kind")
    private Integer approvaKind;

    @Column(name = "approva_status")
    private String approvaStatus;

    @Column(name = "approva_memu")
    private String approvaMemu;

    @Column(name = "file")
    private String file;

    @Column(name = "approver")
    private Integer approver;

    @Column(name = "approva_date")
    private LocalDateTime approvaDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //ApprovaEntity 와 join
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approva_kind", insertable = false, updatable = false, referencedColumnName = "document_id")
    private DocumentEntity line;

    //UserEntity 와 join
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer", insertable = false, updatable = false, referencedColumnName = "user_id")
    private UserEntity userWriter;

    //UserEntity 와 join
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver", insertable = false, updatable = false, referencedColumnName = "approval_line_id")
    private ApprovalLineEntity lineApprover;

    public ApprovaUserDTO MapDTO() {
        return ApprovaUserDTO.builder()
                .approvaId(this.approvaId)
                .approvaTitle(this.approvaTitle)
                .approvaContent(this.approvaContent)
                .approvaKind(this.approvaKind)
                .approvaStatus(this.approvaStatus)
                .approvaMemu(this.approvaMemu)
                .file(this.file)
                .approvaDate(this.approvaDate)
                .writerInfo(this.userWriter != null ? createUserDetail(this.userWriter) : null)
                .approverInfo(this.lineApprover != null ? createApproverDetail(this.lineApprover) : null)
                .build();
    }

    private ApprovaUserDTO.UserInfo createUserDetail(UserEntity user) {
        return ApprovaUserDTO.UserInfo.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .employeeId(user.getEmployeeId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .roleCode(user.getRole() != null ? user.getRole().getCord() : null)
                .gradeName(user.getGrade() != null ? user.getGrade().getGradeName() : null)
                .gradeCord(user.getGrade() != null ? user.getGrade().getGradeCord() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getDepartmentName() : null)
                .departmentCord(user.getDepartment() != null ? user.getDepartment().getDepartmentCord() : null)
                .build();
    }

    private ApprovaUserDTO.ApproverInfo createApproverDetail(ApprovalLineEntity user) {
        return ApprovaUserDTO.ApproverInfo.builder()
                .userId(user.getUserApprover().getUserId())
                .userName(user.getUserApprover().getUserName())
                .employeeId(user.getUserApprover().getEmployeeId())
                .phone(user.getUserApprover().getPhone())
                .email(user.getUserApprover().getEmail())
                .roleName(user.getUserApprover().getRole() != null ? user.getUserApprover().getRole().getName() : null)
                .roleCode(user.getUserApprover().getRole() != null ? user.getUserApprover().getRole().getCord() : null)
                .gradeName(user.getUserApprover().getGrade() != null ? user.getUserApprover().getGrade().getGradeName() : null)
                .gradeCord(user.getUserApprover().getGrade() != null ? user.getUserApprover().getGrade().getGradeCord() : null)
                .departmentName(user.getUserApprover().getDepartment() != null ? user.getUserApprover().getDepartment().getDepartmentName() : null)
                .departmentCord(user.getUserApprover().getDepartment() != null ? user.getUserApprover().getDepartment().getDepartmentCord() : null)
                .build();
    }



    private String gradeName;
    private String gradeCord;

    private String departmentName;
    private String departmentCord;


}
