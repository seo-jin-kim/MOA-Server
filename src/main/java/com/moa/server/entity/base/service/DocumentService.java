package com.moa.server.entity.base.service;
import com.moa.server.entity.approval.DocumentEntity;
import com.moa.server.entity.approval.DocumentRepository;
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
public class DocumentService {
    private final DocumentRepository repository;

    public Page<DocumentEntity> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("documentId").descending());
        return repository.findAll(pageable);
    }
    public DocumentEntity getDetail(Integer documentId) {
        return repository.findById(documentId).orElse(null);
    }
    public void register(DocumentEntity data) { repository.save(data); }
    public void modify(DocumentEntity data) { repository.save(data); } // JPA는 save가 수정도 함
    public void remove(Integer documentId) { repository.deleteById(documentId); }
}
