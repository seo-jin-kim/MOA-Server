package com.moa.server.entity.base.controller;

import com.moa.server.entity.approval.ApprovalLineEntity;
import com.moa.server.entity.base.service.AllowanceService;
import com.moa.server.entity.base.service.ApprovalLineService;
import com.moa.server.entity.salary.AllowanceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/base/approval")
@RequiredArgsConstructor
public class ApprovalLineController {
    private final ApprovalLineService service;

    @GetMapping
    public Page<ApprovalLineEntity> list(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size)
    {
        return service.getList(page, size);
    }
    @GetMapping("/{approvalLineId}")
    public ApprovalLineEntity detail(@PathVariable Integer approvalLineId) {
        return service.getDetail(approvalLineId);
    }
    @PostMapping
    public void add(@RequestBody ApprovalLineEntity data) {
        service.register(data);
    }
    @PutMapping("/{approvalLineId}")
    public void update(@RequestBody ApprovalLineEntity data) {
        service.modify(data);
    }
    @DeleteMapping("/{approvalLineId}")
    public void delete(@PathVariable Integer approvalLineId) {
        service.remove(approvalLineId);
    }
}
