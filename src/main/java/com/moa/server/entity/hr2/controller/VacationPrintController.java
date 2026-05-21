package com.moa.server.entity.hr2.controller;

import com.moa.server.entity.hr2.dto.FilterDTO;
import com.moa.server.entity.hr2.dto.VacationPrintDTO;
import com.moa.server.entity.hr2.service.VacationPrintService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/leavesBalance")
@RequiredArgsConstructor
public class VacationPrintController {

    private final VacationPrintService service;

    @GetMapping
    public Page<VacationPrintDTO> list(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "15") int size,
                                       FilterDTO filterDTO) {
        return service.getList(page, size, filterDTO);
    }

    @GetMapping("/{vacationId}")
    public VacationPrintDTO detail(@PathVariable Integer vacationId) {
        return service.getDetail(vacationId);
    }

}
