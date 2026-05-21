package com.moa.server.entity.user;

import com.moa.server.common.BaseEntity;
import com.moa.server.entity.approval.ApprovaEntity;
import com.moa.server.entity.user.dto.AdminUserDTO;
import com.moa.server.entity.user.dto.TeamUserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter // DTO 역할을 위해 세터를 열어줍니다
@NoArgsConstructor // JPA와 DTO 처리를 위한 기본 생성자
@AllArgsConstructor
@Builder
//세선에 객체를 저장할 때 변환해서 사용해야함  -> Serializable
public class UserEntity extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "password")
    private String password;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "quit_date")
    private LocalDate quitDate;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "grade_id")
    private Integer gradeId;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "performance")
    private String performance;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "bank")
    private String bank;

    @Column(name = "account_num")
    private String accountNum;
 
    //AdminRoleEntity 와 join
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private AdminRoleEntity role;

    //GradeEntity 와 join
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", insertable = false, updatable = false)
    private GradeEntity grade;

    //DepartmentEntity 와 join
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    private DepartmentEntity department;

    @OneToMany(mappedBy = "userWriter",fetch = FetchType.LAZY)
    private List<ApprovaEntity> approva = new ArrayList<>();

    // InventoryEntity.java 내부 혹은 별도 Mapper
    public AdminUserDTO toDTO() {
        return AdminUserDTO.builder()
                .roleId(this.roleId)
                .userId(this.userId)
                .userName(this.userName)
                .employeeId(this.employeeId)
                .phone(this.phone)
                .email(this.email)
                .roleName(this.role != null ? this.role.getName() : null)
                .roleCode(this.role != null ? this.role.getCord() : null)
                .gradeId(this.gradeId)
                .gradeCord(this.grade != null ? this.grade.getGradeCord(): null)
                .gradeName(this.grade != null ? this.grade.getGradeName() : null)
                .departmentId(this.departmentId)
                .departmentCord(this.department != null ? this.department.getDepartmentCord() : null)
                .departmentName(this.department != null ? this.department.getDepartmentName() : null)
                .build();

    }

    public TeamUserDTO teamDTO() {
        return TeamUserDTO.builder()
                .roleId(this.roleId)
                .userId(this.userId)
                .userName(this.userName)
                .employeeId(this.employeeId)
                .phone(this.phone)
                .email(this.email)
                .startDate(this.startDate)
                .performance(this.performance)
                .roleName(this.role != null ? this.role.getName() : null)
                .roleCode(this.role != null ? this.role.getCord() : null)
                .gradeId(this.gradeId)
                .gradeCord(this.grade != null ? this.grade.getGradeCord(): null)
                .gradeName(this.grade != null ? this.grade.getGradeName() : null)
                .departmentId(this.departmentId)
                .departmentCord(this.department != null ? this.department.getDepartmentCord() : null)
                .departmentName(this.department != null ? this.department.getDepartmentName() : null)
                .build();

    }

}
