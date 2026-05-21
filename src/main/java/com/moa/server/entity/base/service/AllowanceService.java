package com.moa.server.entity.base.service;
import com.moa.server.entity.salary.AllowanceEntity;
import com.moa.server.entity.salary.AllowanceRepository;
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
public class AllowanceService {
    private final AllowanceRepository repository;

    public Page<AllowanceEntity> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("allowanceId").descending());
        return repository.findAll(pageable);
    }
    public AllowanceEntity getDetail(Integer allowanceId) {
        return repository.findById(allowanceId).orElse(null);
    }
    public void register(AllowanceEntity data) { repository.save(data); }
    public void modify(AllowanceEntity data) { repository.save(data); } // JPA는 save가 수정도 함
    public void remove(Integer allowanceId) { repository.deleteById(allowanceId); }

}
