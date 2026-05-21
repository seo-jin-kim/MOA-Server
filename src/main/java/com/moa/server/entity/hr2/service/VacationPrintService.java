package com.moa.server.entity.hr2.service;

import com.moa.server.entity.hr2.dto.FilterDTO;
import com.moa.server.entity.hr2.dto.VacationPrintDTO;
import com.moa.server.entity.vacation.VacationEntity;
import com.moa.server.entity.vacation.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VacationPrintService {
    private final VacationRepository repository;

    @Transactional
    public Page<VacationPrintDTO> getList(int page, int size, FilterDTO filterDTO) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("vacationId").descending());

        LocalDate start = (filterDTO.getStartDate() != null && !filterDTO.getStartDate().isEmpty())
                ? LocalDate.parse(filterDTO.getStartDate())
                : null;

        LocalDate finish = (filterDTO.getFinishDate() != null && !filterDTO.getFinishDate().isEmpty())
                ? LocalDate.parse(filterDTO.getFinishDate())
                : null;

        Page<VacationEntity> result = repository.findVacationPrint(
                start != null ? start.atStartOfDay() : null,
                finish != null ? finish.atTime(23, 59, 59) : null,
                filterDTO.getCategory(),
                filterDTO.getKeyword(),
                pageable
        );

        return result.map(VacationPrintDTO::fromEntity);
    }

    @Transactional
    public VacationPrintDTO getDetail(Integer vacationId) {
        return repository.findById(vacationId)
                .map(VacationPrintDTO::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록이 없습니다. ID: " + vacationId));
    }


}
