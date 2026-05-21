package com.moa.server.entity.layout.dao;

import com.moa.server.entity.menu.MenuEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LayoutDAO {

    private final EntityManager em;

    // 사이드바 상단 로그인유저 정보
    public Object[] userLayoutInfo(String employeeId){
        String sql = "SELECT profile_url, user_name, department_name, grade_name, employee_id " +
                "FROM \"user\" u " +
                "LEFT JOIN department d ON u.department_id = d.department_id " +
                "LEFT JOIN grade g ON u.grade_id = g.grade_id " +
                "WHERE u.employee_id = :employeeId";

        List<Object[]> result = em.createNativeQuery(sql)
                .setParameter("employeeId", employeeId)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

}



