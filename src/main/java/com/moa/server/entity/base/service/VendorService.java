package com.moa.server.entity.base.service;

import com.moa.server.entity.inventory.VendorEntity;
import com.moa.server.entity.inventory.VendorRepository;
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
public class VendorService {
    private final VendorRepository repository;

    public Page<VendorEntity> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("vendorId").descending());

        return repository.findAll(pageable);
    }
    public VendorEntity getDetail(Integer vendorId) {
        return repository.findById(vendorId).orElse(null);
    }
    public void register(VendorEntity data) { repository.save(data); }
    public void modify(VendorEntity data) { repository.save(data); } // JPA는 save가 수정도 함
    public void remove(Integer vendorId) { repository.deleteById(vendorId); }
}
