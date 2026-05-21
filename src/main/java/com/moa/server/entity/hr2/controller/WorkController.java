package com.moa.server.entity.hr2.controller;

import com.moa.server.entity.hr2.dto.FilterDTO;
import com.moa.server.entity.hr2.dto.SelectMappingDTO;
import com.moa.server.entity.hr2.dto.WorkDTO;
import com.moa.server.entity.hr2.service.WorkService;
import com.moa.server.entity.user.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/hr/attendances")
@RequiredArgsConstructor
public class WorkController {
    private final WorkService service;

    //출근
   @PostMapping("/checkin")
    public ResponseEntity<Void> checkIn(HttpSession session) {
        SessionUser loginUser = (SessionUser) session.getAttribute(SessionUser.USER);
        service.checkIn(loginUser.getUserId());
        return ResponseEntity.ok().build();
    }   
   
   // 선택한 사번/이름 데이터 반영
    @GetMapping("/put/user")
    public ResponseEntity<List<SelectMappingDTO>> fetchEmployee(@RequestParam String keyword) {
        List<SelectMappingDTO> list = service.getUser(keyword);
        return ResponseEntity.ok(list);
    }

    // 선택한 수당코드/수당명 데이터 반영
    @GetMapping("/put/allowance")
    public ResponseEntity<List<SelectMappingDTO>> fetchAllowance(@RequestParam String keyword) {
        List<SelectMappingDTO> list = service.getAllowance(keyword);
        return ResponseEntity.ok(list);
    }


    @GetMapping
    public Page<WorkDTO> list(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "15") int size,
                              FilterDTO filterDTO) {
        return service.getList(page,size, filterDTO);
    }

    @GetMapping("/{workId}")
    public WorkDTO detail(@PathVariable Integer workId) {
        return service.getDetail(workId);
    }

    @PostMapping
    public void add(@RequestBody WorkDTO data) {
        service.register(data);
    }

    @PutMapping("/{workId}")
    public void update(@PathVariable Integer workId, @RequestBody WorkDTO data) {
        data.setWorkId(workId);
        service.modify(data);
    }

    // 퇴근
    @PostMapping("/checkout")
    public ResponseEntity<Void> checkOut(HttpSession session) {
        SessionUser loginUser = (SessionUser) session.getAttribute(SessionUser.USER);
        service.checkOut(loginUser.getUserId());
        return ResponseEntity.ok().build();
    }

    // 오늘 출퇴근 조회
    @GetMapping("/today")
    public ResponseEntity<?> getToday(HttpSession session) {
        SessionUser loginUser = (SessionUser) session.getAttribute(SessionUser.USER);
        if (loginUser == null) return ResponseEntity.status(401).build();

        WorkDTO dto = service.getTodayWork(loginUser.getUserId());
        return ResponseEntity.ok(dto);
    }
}

