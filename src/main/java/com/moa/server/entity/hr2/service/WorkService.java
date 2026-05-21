package com.moa.server.entity.hr2.service;

import com.moa.server.entity.hr2.dto.FilterDTO;
import com.moa.server.entity.hr2.dto.SelectMappingDTO;
import com.moa.server.entity.hr2.dto.WorkDTO;
import com.moa.server.entity.salary.AllowanceEntity;
import com.moa.server.entity.salary.AllowanceRepository;
import com.moa.server.entity.user.UserEntity;
import com.moa.server.entity.user.UserRepository;
import com.moa.server.entity.vacation.WorkEntity;
import com.moa.server.entity.vacation.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkService {
    private final WorkRepository workRepository;
    private AllowanceEntity allowance;
    private final UserRepository userRepository;
    private final AllowanceRepository allowanceRepository;

    // 1. 전체 조회
    @Transactional
    public Page<WorkDTO> getList(int page, int size, FilterDTO filterDTO) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("workDate").descending());


        LocalDate start = (filterDTO.getStartDate() != null && !filterDTO.getStartDate().isEmpty())
                ? LocalDate.parse(filterDTO.getStartDate()) : null;

        LocalDate finish = (filterDTO.getFinishDate() != null && !filterDTO.getFinishDate().isEmpty())
                ? LocalDate.parse(filterDTO.getFinishDate()) : null;

        String keyword = (filterDTO.getKeyword() == null) ? "" : filterDTO.getKeyword();
        String category = filterDTO.getCategory();

        return workRepository.findAllWithDetails(
                        start != null ? start.atStartOfDay() : null,
                        finish != null ? finish.atTime(23, 59, 59) : null,
                        category,
                        keyword,
                        pageable)
                .map(WorkDTO::new);


//        return workRepository.findAllWithDetails(
//                        start != null ? start.atStartOfDay() : null,
//                        finish != null ? finish.atTime(23, 59, 59) : null,
//                        filterDTO.getCategory(),
//                        filterDTO.getKeyword(), pageable)
//                .map(WorkDTO::new);
    }

    // 2. 상세 조회
    @Transactional
    public WorkDTO getDetail(Integer workId) {
        WorkEntity work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록이 없습니다. ID: " + workId));
        return new WorkDTO(work);
    }

    // 3. 등록 (Create)
    @Transactional
    public void register(WorkDTO dto) {
        // DTO의 사번(EmployeeId)으로 유저 검색
        UserEntity user = userRepository.findByEmployeeId(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("미확인 유저입니다."));

        // DTO의 수당 이름(allowanceName)으로 수당 엔티티 검색
        if (dto.getAllowanceName() != null && !dto.getAllowanceName().isEmpty()) {
            // findByAllowanceName이 Optional을 반환한다고 가정 시 .orElse(null) 처리
            allowance = (AllowanceEntity) allowanceRepository.findByAllowanceName(dto.getAllowanceName())
                    .orElse((AllowanceEntity) allowanceRepository.findAll());
        }

        WorkEntity work = WorkEntity.builder()
                .user(user)
                .allowance(allowance)
                .workDate(dto.getWorkDate())
                .workStatus(dto.getWorkStatus())
                .workMemo(dto.getWorkMemo())
                .build();

        workRepository.save(work);
    }

    // 4. 수정 (Update)
    @Transactional
    public void modify(WorkDTO dto) {
        WorkEntity work = workRepository.findById(dto.getWorkId())
                .orElseThrow(() -> new IllegalArgumentException("해당 기록이 없습니다."));

        // 수당 변경 처리
        if (dto.getAllowanceName() != null && !dto.getAllowanceName().isEmpty()) {
            // 수정 시에도 이름을 기준으로 찾거나, 코드가 있다면 코드로 찾기
            AllowanceEntity allowance = allowanceRepository.findByAllowanceName(dto.getAllowanceName())
                    .orElse(null);
            work.setAllowance(allowance);
        } else {
            work.setAllowance(null); // 수당이 없으면 NULL로 변경
        }

        // 데이터 업데이트 (Dirty Checking으로 자동 반영)
        work.setWorkDate(dto.getWorkDate());
        work.setWorkStatus(dto.getWorkStatus());
        work.setWorkMemo(dto.getWorkMemo());
    }

    // 5. 삭제 (Delete)
    @Transactional
    public void remove(Integer workId) {
        workRepository.deleteById(workId);
    }

    // 출근
    @Transactional
    public void checkIn(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        WorkEntity work = WorkEntity.builder()
                .user(user)
                .workDate(LocalDate.now())
                .startWork(LocalDateTime.now())
                .workStatus("정상")
                .build();
        workRepository.save(work);
    }

    // 퇴근
    @Transactional
    public void checkOut(Integer userId) {
        WorkEntity work = workRepository.findByUserIdAndWorkDate(userId, LocalDate.now());
        if (work == null) throw new IllegalArgumentException("출근 기록 없음");
        work.setFinishWork(LocalDateTime.now());
    }

    // 오늘 출퇴근 조회
    @Transactional(readOnly = true)
    public WorkDTO getTodayWork(Integer userId) {
        WorkEntity work = workRepository.findByUserIdAndWorkDate(userId, LocalDate.now());
        if (work == null) return null;
        return new WorkDTO(work);
    }

    //  직원 정보 자동 채우기용 서비스
    @Transactional(readOnly = true)
    public List<SelectMappingDTO> getUser(String keyword) {
        List<Object[]> results = null;
        if(keyword==null || keyword.trim().isEmpty()){
            results = userRepository.searchUser();
        }else{
            results = userRepository.searchUserByKeyword(keyword);
        }

        return results.stream() // 1. 데이터를 흐르게 한다 ✊😠
                .map(result -> SelectMappingDTO.builder()
                        .employeeId((String) result[0])
                        .userName((String) result[1])
                        .build())
                .collect(Collectors.toList());
    }

    // 수당 정보 자동 채우기용 서비스
    @Transactional(readOnly = true)
    public List<SelectMappingDTO> getAllowance(String keyword) {
        List<Object[]> results = null;
        if(keyword==null || keyword.trim().isEmpty()){
            results = allowanceRepository.searchAllowance();
        }else{
            results = allowanceRepository.searchAllowanceByKeyword(keyword);
        }

        return results.stream() // 1. 데이터를 흐르게 한다
                .map(result -> SelectMappingDTO.builder()
                        .allowanceCord((String) result[0])
                        .allowanceName((String) result[1])
                        .build())
                .collect(Collectors.toList());
    }
}

