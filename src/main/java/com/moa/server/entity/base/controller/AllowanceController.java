package com.moa.server.entity.base.controller;

import com.moa.server.entity.base.service.AdminRoleService;
import com.moa.server.entity.base.service.AllowanceService;
import com.moa.server.entity.salary.AllowanceEntity;
import com.moa.server.entity.user.AdminRoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/base/allow")
@RequiredArgsConstructor
public class AllowanceController {
    private final AllowanceService service;

    @GetMapping
    public Page<AllowanceEntity> list(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "15") int size)
    {
        return service.getList(page, size);
    }
    @GetMapping("/{allowanceId}")
    public AllowanceEntity detail(@PathVariable Integer allowanceId) {
        return service.getDetail(allowanceId);
    }
    @PostMapping
    public void add(@RequestBody AllowanceEntity data) {
        service.register(data);
    }
    @PutMapping("/{allowanceId}")
    public void update(@RequestBody AllowanceEntity data) {
        service.modify(data);
    }
    @DeleteMapping("/{allowanceId}")
    public void delete(@PathVariable Integer allowanceId) {
        service.remove(allowanceId);
    }
}
