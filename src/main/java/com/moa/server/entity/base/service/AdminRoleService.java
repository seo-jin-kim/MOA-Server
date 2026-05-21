package com.moa.server.entity.base.service;
import com.moa.server.entity.user.AdminRoleEntity;
import com.moa.server.entity.user.AdminRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoleService {
    private final AdminRoleRepository repository;

    public Page<AdminRoleEntity> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("cord").descending());
        return repository.findAll(pageable);
    }
    public AdminRoleEntity getDetail(Integer id) {
        return repository.findById(id).orElse(null);
    }
    public void register(AdminRoleEntity data) { repository.save(data); }
    public void modify(AdminRoleEntity data) { repository.save(data); } // JPA는 save가 수정도 함
    public void remove(Integer id) { repository.deleteById(id); }
}
