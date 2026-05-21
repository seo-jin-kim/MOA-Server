package com.moa.server.entity.base.controller;

import com.moa.server.entity.approval.DocumentEntity;
import com.moa.server.entity.base.service.AllowanceService;
import com.moa.server.entity.base.service.DocumentService;
import com.moa.server.entity.salary.AllowanceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/base/form")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @GetMapping
    public Page<DocumentEntity> list(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "15") int size)
    {
        return service.getList(page, size);
    }
    @GetMapping("/{documentId}")
    public DocumentEntity detail(@PathVariable Integer documentId) {
        return service.getDetail(documentId);
    }
    @PostMapping
    public void add(@RequestBody DocumentEntity data) {
        service.register(data);
    }
    @PutMapping("/{documentId}")
    public void update(@RequestBody DocumentEntity data) {
        service.modify(data);
    }
    @DeleteMapping("/{documentId}")
    public void delete(@PathVariable Integer documentId) {
        service.remove(documentId);
    }

}
