package com.moa.server.entity.base.controller;

import com.moa.server.entity.base.service.AllowanceService;
import com.moa.server.entity.base.service.CompanyAccountService;
import com.moa.server.entity.salary.AllowanceEntity;
import com.moa.server.entity.salary.CompanyAccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/base/account")
@RequiredArgsConstructor
public class CompanyAccountController {
    private final CompanyAccountService service;

    @GetMapping
    public Page<CompanyAccountEntity> list(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "15") int size)
    {
        return service.getList(page, size);
    }
    @GetMapping("/{companyAccountId}")
    public CompanyAccountEntity detail(@PathVariable Integer companyAccountId) {
        return service.getDetail(companyAccountId);
    }
    @PostMapping
    public void add(@RequestBody CompanyAccountEntity data) {
        service.register(data);
    }
    @PutMapping("/{companyAccountId}")
    public void update(@RequestBody CompanyAccountEntity data) {
        service.modify(data);
    }
    @DeleteMapping("/{companyAccountId}")
    public void delete(@PathVariable Integer companyAccountId) {
        service.remove(companyAccountId);
    }
}
