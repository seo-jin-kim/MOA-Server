package com.moa.server.entity.base.controller;

import com.moa.server.entity.base.service.AdminRoleService;
import com.moa.server.entity.user.AdminRoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping("/api/base/role")
@RequiredArgsConstructor
public class AdminRoleController {
    private final AdminRoleService service;

    @GetMapping
    public Page<AdminRoleEntity> list(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "15") int size)
    {
        return service.getList(page, size);
    }
    @GetMapping("/{id}")
    public AdminRoleEntity detail(@PathVariable Integer id) {
        return service.getDetail(id);
    }
    @PostMapping
    public void add(@RequestBody AdminRoleEntity data) {
        service.register(data);
    }
    @PutMapping("/{id}")
    public void update(@RequestBody AdminRoleEntity data) {
        service.modify(data);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.remove(id);
    }
}
