package com.moa.server.entity.hr2.controller;

import com.moa.server.entity.hr2.dto.BasicVacationDTO;
import com.moa.server.entity.hr2.service.BasicVacationService;
import com.moa.server.entity.vacation.BasicVacationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr/annualLeaves")
@RequiredArgsConstructor
public class BasicVacationController {

    private final BasicVacationService service;

    @GetMapping
    public Page<BasicVacationDTO> list(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "15") int size)
    {
        return service.getList(page, size);
    }
    @GetMapping("/{basicVacationId}")
    public BasicVacationDTO detail(@PathVariable Integer basicVacationId) {
        return service.getDetail(basicVacationId);
    }
    @PostMapping
    public void add(@RequestBody BasicVacationDTO data) {
        service.register(data);
    }
    @PutMapping("/{basicVacationId}")
    public void update(@PathVariable Integer basicVacationId, @RequestBody BasicVacationDTO data) {
        data.setBasicVacationId(basicVacationId);
        service.modify(data);
    }
    @DeleteMapping("/{basicVacationId}")
    public void delete(@PathVariable Integer basicVacationId) {
        service.remove(basicVacationId);
    }
}
