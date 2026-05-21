package com.moa.server.entity.hr2.service;

import com.moa.server.entity.hr2.dto.HRCalendarDTO;
import com.moa.server.entity.hr2.dto.HRCountDTO;
import com.moa.server.entity.vacation.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HRCalendarService {

    private final WorkRepository repository;

    public List<HRCountDTO> getCalendarData(LocalDate targetMonth) {

        LocalDate month = LocalDate.parse(targetMonth + "-01");

        LocalDateTime start = month.atStartOfDay();
        LocalDateTime end = month.plusMonths(1).atStartOfDay();

        List<HRCountDTO> countList = repository.findWorkCount(start, end);
        List<HRCalendarDTO> detailList = repository.findWorkDetailList(start, end);


        Map<LocalDate, List<HRCalendarDTO>> detailMap = detailList.stream()
                .collect(Collectors.groupingBy(HRCalendarDTO::getWorkDate));

        // 날짜 기준으로 그룹핑
//        Map<LocalDate, List<HRCalendarDTO>> detailMap =
//                detailList.stream().collect(Collectors.groupingBy(
//                                dto -> dto.getWorkDate().toLocalDate()
//                        ));


        Map<LocalDate, Long> countMap = countList.stream()
                .collect(Collectors.toMap(
                        HRCountDTO::getDate, // LocalDate 반환
                        dto -> dto.getTotalCount() == null ? 0L : dto.getTotalCount(),
                        (existing, replacement) -> existing // 중복 날짜 발생 시 처리
                ));


//        Map<LocalDateTime, Long> countMap =
//                countList.stream()
//                        .collect(Collectors.toMap(
//                                HRCountDTO::getDate,
//                                dto -> dto.getTotalCount() == null ? 0L : dto.getTotalCount().longValue()
//                        ));


        List<HRCountDTO> result = new ArrayList<>();

        for (LocalDate date = month; date.isBefore(month.plusMonths(1)); date = date.plusDays(1)) {

            result.add(
                    HRCountDTO.builder()
                            .date(date)
                            // 3. getOrDefault를 사용하고 바로 Long으로 할당
                            .totalCount(countMap.getOrDefault(date, 0L))
                            // 상세 리스트 매핑 (detailMap에서 해당 날짜 데이터 가져오기)
                            .details(detailMap.getOrDefault(date, Collections.emptyList()))
                            .build()
            );
        }


//        for (LocalDateTime date = month.atStartOfDay();
//             !date.isAfter(month.plusMonths(1).minusDays(1).atStartOfDay());
//             date = date.plusDays(1)) {
//
//            result.add(
//                    HRCountDTO.builder()
//                            .date(date)
//                            // 데이터 없으면 0
//                            .totalCount((long) countMap.getOrDefault(date, 0L).intValue())
//                            // 상세 없으면 빈 리스트
//                            .details(Collections.emptyList())
//                            .build()
//            );
//        }

        return result;
    }
}