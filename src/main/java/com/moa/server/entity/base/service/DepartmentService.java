package com.moa.server.entity.base.service;
import com.moa.server.entity.user.AdminRoleEntity;
import com.moa.server.entity.user.DepartmentEntity;
import com.moa.server.entity.user.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;

    public Page<DepartmentEntity> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("departmentId").descending());
        return repository.findAll(pageable);
    }
    public DepartmentEntity getDetail(Integer departmentId) {
        return repository.findById(departmentId).orElse(null);
    }
    public void register(DepartmentEntity data) { repository.save(data); }
    public void modify(DepartmentEntity data) { repository.save(data); } // JPA는 save가 수정도 함
    public void remove(Integer departmentId) { repository.deleteById(departmentId); }
}
