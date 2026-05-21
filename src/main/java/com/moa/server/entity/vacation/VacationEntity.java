package com.moa.server.entity.vacation;

import com.moa.server.common.BaseEntity;
import com.moa.server.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vacation")
@Getter
@Setter // DTO 역할을 위해 세터를 열어줍니다
@NoArgsConstructor // JPA와 DTO 처리를 위한 기본 생성자
@AllArgsConstructor
@Builder
public class VacationEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vacation_id")
    private Integer vacationId;

    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_vacation_id")
    private BasicVacationEntity basicVacation;

    @Column(name = "use_vacation")
    private Integer useVacation;

    @Column(name = "remaining_vacation")
    private Integer remainingVacation;
}
