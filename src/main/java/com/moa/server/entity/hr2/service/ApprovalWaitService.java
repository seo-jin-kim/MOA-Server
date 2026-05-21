package com.moa.server.entity.hr2.service;

import com.moa.server.entity.approval.ApprovaEntity;
import com.moa.server.entity.approval.ApprovaRepository;
import com.moa.server.entity.hr2.dto.ApprovalWaitDTO;
import com.moa.server.entity.hr2.dto.FilterDTO;
import com.moa.server.entity.hr2.dto.WorkDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApprovalWaitService {

    private final ApprovaRepository repository;


    @Transactional
    public Page<ApprovalWaitDTO> getList(int page, int size, FilterDTO filterDTO) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("approvaId").descending());
        LocalDate start = (filterDTO.getStartDate() != null && !filterDTO.getStartDate().isEmpty())
                ? LocalDate.parse(filterDTO.getStartDate())
                : null;

        LocalDate finish = (filterDTO.getFinishDate() != null && !filterDTO.getFinishDate().isEmpty())
                ? LocalDate.parse(filterDTO.getFinishDate())
                : null;
        String category = (filterDTO.getCategory() != null && !filterDTO.getCategory().isEmpty())
                ? filterDTO.getCategory() : null;

        String keyword = (filterDTO.getKeyword() != null && !filterDTO.getKeyword().isEmpty())
                ? filterDTO.getKeyword() : null;

        Integer departmentId = filterDTO.getDepartmentId();

        System.out.println("백엔드 전달 부서명: " + departmentId);
        return repository.findApprovalList(
                        start != null ? start.atStartOfDay() : null,
                        finish != null ? finish.atTime(23, 59, 59) : null,
                        category,
                        keyword,
                        departmentId,
                        pageable)
                .map(ApprovalWaitDTO::fromEntity);
    }

    @Transactional // 리턴 안 해도 값이 넘어감
    public void changeStatus(Integer approvaId, String approvaState) {
        // 1. 레포지토리에서 해당 결재 건 조회
        ApprovaEntity entity = repository.findById(approvaId)
                .orElseThrow(() -> new RuntimeException("해당 결재 정보를 찾을 수 없습니다."));

        // 2. 상태 변경 (select 박스에서 넘어온 값으로 세팅)
        entity.setApprovaStatus(approvaState);

    }

}
