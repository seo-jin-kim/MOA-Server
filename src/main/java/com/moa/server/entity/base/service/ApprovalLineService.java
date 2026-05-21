package com.moa.server.entity.base.service;
import com.moa.server.entity.approval.ApprovalLineEntity;
import com.moa.server.entity.approval.ApprovalLineRepository;
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
public class ApprovalLineService {
    private final ApprovalLineRepository repository;

    public Page<ApprovalLineEntity> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("approvalLineId").descending());
        return repository.findAll(pageable);
    }
    public ApprovalLineEntity getDetail(Integer approvalLineId) {
        return repository.findById(approvalLineId).orElse(null);
    }
    public void register(ApprovalLineEntity data) { repository.save(data); }
    public void modify(ApprovalLineEntity data) { repository.save(data); } // JPA는 save가 수정도 함
    public void remove(Integer approvalLineId) { repository.deleteById(approvalLineId); }
}
