package com.moa.server.entity.base.service;
import com.moa.server.entity.salary.CompanyAccountEntity;
import com.moa.server.entity.salary.CompanyAccountRepository;
import com.moa.server.entity.user.AdminRoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyAccountService {
    private final CompanyAccountRepository repository;

    public Page<CompanyAccountEntity> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("companyAccountId").descending());
        return repository.findAll(pageable);
    }
    public CompanyAccountEntity getDetail(Integer companyAccountId) {
        return repository.findById(companyAccountId).orElse(null);
    }
    public void register(CompanyAccountEntity data) { repository.save(data); }
    public void modify(CompanyAccountEntity data) { repository.save(data); } // JPA는 save가 수정도 함
    public void remove(Integer companyAccountId) { repository.deleteById(companyAccountId); }
}
