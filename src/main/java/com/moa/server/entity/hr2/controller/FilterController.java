package com.moa.server.entity.hr2.controller;

import com.moa.server.entity.hr2.dto.FilterKeywordDTO;
import com.moa.server.entity.hr2.service.HRFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/filter")
@RequiredArgsConstructor
public class FilterController {
    private final HRFilterService service;

    /**
     * 필터 검색 API
     * @param type: "user", "department", "document" 중 하나
     * @param keyword: 사용자가 입력한 검색어
     */
    @GetMapping("/search")
    public ResponseEntity<List<FilterKeywordDTO>> getSearchList(
            @RequestParam("type") String type,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword
    ) {
        List<FilterKeywordDTO> results = service.getFilterList(type, keyword);

        return ResponseEntity.ok(results);
    }
    @GetMapping("/departments")
    public ResponseEntity<List<FilterKeywordDTO>> getDeptOptions(@RequestParam(value = "departmentName", required = false) String departmentName) {
        // 키워드 없이 전체 부서를 긁어옵니다.
        List<FilterKeywordDTO> depts = service.searchDepartments("");

        return ResponseEntity.ok(depts.stream()
                .map(d -> FilterKeywordDTO.builder()
                        .departmentId(d.getDepartmentId())
                        .departmentName(d.getDepartmentName())
                        .build())
                .toList());
    }
}
