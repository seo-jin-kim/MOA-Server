package com.moa.server.entity.base.service;
import com.moa.server.entity.inventory.StorageEntity;
import com.moa.server.entity.inventory.StorageRepository;
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
public class StorageService {
    private final StorageRepository repository;

    public Page<StorageEntity> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("storageId").descending());
        return repository.findAll(pageable); }
    public StorageEntity getDetail(Integer storageId) {
        return repository.findById(storageId).orElse(null);
    }
    public void register(StorageEntity data) { repository.save(data); }
    public void modify(StorageEntity data) { repository.save(data); } // JPA는 save가 수정도 함
    public void remove(Integer storageId) { repository.deleteById(storageId); }
}
