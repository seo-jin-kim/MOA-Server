package com.moa.server.entity.hr2.controller;

import com.moa.server.entity.hr2.dto.ApprovalWaitDTO;
import com.moa.server.entity.hr2.dto.FilterDTO;
import com.moa.server.entity.hr2.service.ApprovalWaitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/approvalWait")
@RequiredArgsConstructor
public class ApprovalWaitController {

    private final ApprovalWaitService service;

    @GetMapping
    public Page<ApprovalWaitDTO> list(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "15") int size,
                                      FilterDTO filterDTO) {
        return service.getList(page,size, filterDTO);
    }
    @PatchMapping("/{approvaId}")
    public String updateStatus(@PathVariable Integer approvaId,
                               @RequestParam String approvaState) {
        service.changeStatus(approvaId, approvaState);
        return "변경 성공";
    }
}
