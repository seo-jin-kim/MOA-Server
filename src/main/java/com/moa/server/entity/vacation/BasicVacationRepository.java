package com.moa.server.entity.vacation;

import com.moa.server.entity.user.AdminRoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasicVacationRepository extends JpaRepository<BasicVacationEntity, Integer> {

    //예시
    //List<BoardVOEntity> findByTitleContaining  (String title);

    Page<BasicVacationEntity> findAll(Pageable pageable);
}

