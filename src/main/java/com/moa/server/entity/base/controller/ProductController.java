package com.moa.server.entity.base.controller;

import com.moa.server.entity.base.service.AllowanceService;
import com.moa.server.entity.base.service.ProductService;
import com.moa.server.entity.inventory.ProductEntity;
import com.moa.server.entity.salary.AllowanceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/base/item")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping
    public Page<ProductEntity> list(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "15") int size)
    {
        return service.getList(page, size);
    }
    @GetMapping("/{productId}")
    public ProductEntity detail(@PathVariable Integer productId) {
        return service.getDetail(productId);
    }
    @PostMapping
    public void add(@RequestBody ProductEntity data) {
        service.register(data);
    }
    @PutMapping("/{productId}")
    public void update(@RequestBody ProductEntity data) {
        service.modify(data);
    }
    @DeleteMapping("/{productId}")
    public void delete(@PathVariable Integer productId) {
        service.remove(productId);
    }
}
