package com.moa.server.entity.hr2.service;

import com.moa.server.entity.hr2.dto.FilterDTO;
import com.moa.server.entity.hr2.dto.LatenessDTO;
import com.moa.server.entity.hr2.dto.WorkDTO;
import com.moa.server.entity.vacation.WorkRepository;
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
public class LatenessService {

    private final WorkRepository workRepository;

    @Transactional
    public Page<LatenessDTO> getList(int page, int size, FilterDTO filterDTO) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("workDate").descending());

        LocalDate start = (filterDTO.getStartDate() != null && !filterDTO.getStartDate().isEmpty())
                ? LocalDate.parse(filterDTO.getStartDate())
                : null;

        LocalDate finish = (filterDTO.getFinishDate() != null && !filterDTO.getFinishDate().isEmpty())
                ? LocalDate.parse(filterDTO.getFinishDate())
                : null;

        return workRepository.findLateness(
                start != null ? start.atStartOfDay() : null,
                finish != null ? finish.atTime(23, 59, 59) : null,
                filterDTO.getCategory(),
                filterDTO.getKeyword(),
                pageable
        ).map(LatenessDTO::fromEntity);
    }

}
