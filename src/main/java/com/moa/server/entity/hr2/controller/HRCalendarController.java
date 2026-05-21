package com.moa.server.entity.hr2.controller;

import com.moa.server.entity.hr2.dto.HRCountDTO;
import com.moa.server.entity.hr2.service.HRCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hr/calendar")
@RequiredArgsConstructor
public class HRCalendarController {
    private final HRCalendarService service;

    @GetMapping
    public List<HRCountDTO> getCalendar(@RequestParam String selectMonth) {

        //yyyy-DD 형식일 때만 받기 (프론트에서 해당 양식으로 포장할 예정)
        if (!selectMonth.matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("형식 오류 (yyyy-MM)");
        }

        LocalDate targetMonth = LocalDate.parse(selectMonth + "-01");

        return service.getCalendarData(targetMonth);
    }
}
