package com.moa.server.entity.vacation;

import com.moa.server.common.BaseEntity;
import com.moa.server.entity.salary.AllowanceEntity;
import com.moa.server.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work")
@Getter
@Setter // DTO 역할을 위해 세터를 열어줍니다
@NoArgsConstructor // JPA와 DTO 처리를 위한 기본 생성자
@AllArgsConstructor
@Builder
public class WorkEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    private Integer workId;

    @Column(name = "user_id" , insertable = false, updatable = false )
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "work_date")
    private LocalDate workDate;

    @Column(name = "start_work")
    private LocalDateTime startWork;

    @Column(name = "finish_work")
    private LocalDateTime finishWork;

    @Column(name = "work_status")
    private String workStatus;

    @Column(name = "work_memo")
    private String workMemo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "allowance_cord",
            referencedColumnName = "allowance_cord",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT) // 물리적 FK 생성 안 함
    )
    private AllowanceEntity allowance;

    public String getDisplayStatus() {
        if (this.allowance != null) {
            return this.allowance.getAllowanceName();
        }
        return (this.workStatus != null) ? this.workStatus : "정상근무";
    }

}
