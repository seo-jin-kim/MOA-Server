package com.moa.server.entity.base.controller;

import com.moa.server.entity.base.service.AllowanceService;
import com.moa.server.entity.base.service.VendorService;
import com.moa.server.entity.inventory.VendorEntity;
import com.moa.server.entity.salary.AllowanceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/base/partner")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService service;

    @GetMapping
    public Page<VendorEntity> list(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "15") int size)
    {
        return service.getList(page, size);
    }
    @GetMapping("/{vendorId}")
    public VendorEntity detail(@PathVariable Integer vendorId) {
        return service.getDetail(vendorId);
    }
    @PostMapping
    public void add(@RequestBody VendorEntity data) {
        service.register(data);
    }
    @PutMapping("/{vendorId}")
    public void update(@RequestBody VendorEntity data) {
        service.modify(data);
    }
    @DeleteMapping("/{vendorId}")
    public void delete(@PathVariable Integer vendorId) {
        service.remove(vendorId);
    }
}
