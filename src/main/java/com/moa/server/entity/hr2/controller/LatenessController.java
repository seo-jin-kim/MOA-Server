package com.moa.server.entity.hr2.controller;

import com.moa.server.entity.hr2.dto.FilterDTO;
import com.moa.server.entity.hr2.dto.LatenessDTO;
import com.moa.server.entity.hr2.dto.WorkDTO;
import com.moa.server.entity.hr2.service.LatenessService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hr/latenesses")
@RequiredArgsConstructor
public class LatenessController {

    private final LatenessService service;

    @GetMapping
    public Page<LatenessDTO> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "15") int size,
                                  FilterDTO filterDTO) {
        return service.getList(page,size, filterDTO);
    }

}
