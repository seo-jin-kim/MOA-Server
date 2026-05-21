package com.moa.server.entity.hr2.service;

import com.moa.server.entity.approval.DocumentRepository;
import com.moa.server.entity.hr2.dto.FilterKeywordDTO;
import com.moa.server.entity.user.DepartmentRepository;
import com.moa.server.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class HRFilterService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DocumentRepository documentRepository;

    public HRFilterService(UserRepository userRepository, DepartmentRepository departmentRepository, DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.documentRepository = documentRepository;
    }

    public List<FilterKeywordDTO> getFilterList(String type, String keyword) {
         return switch (type) {
            case "user" -> searchEmployees(keyword);
            case "departmentName" -> searchDepartments(keyword);
            case "document" -> searchDocuments(keyword);
            default -> Collections.emptyList();
        };
    }

    // [사원 테이블 긁어오기]
    public List<FilterKeywordDTO> searchEmployees(String keyword) {
        // 엔티티를 긁어와서 DTO로 변환(map)하는 과정입니다.
        return userRepository.findByUserNameContaining(keyword).stream()
                .map(e -> FilterKeywordDTO.builder()
                        .employeeId(String.valueOf(e.getUserId())) // 엔티티 필드명 확인 필요!
                        .userName(e.getUserName())
                        .build())
                .toList();
    }

    // [부서 테이블 긁어오기]
    public List<FilterKeywordDTO> searchDepartments(String keyword) {
        return departmentRepository.findByDepartmentNameContaining(keyword).stream()
                .map(d -> FilterKeywordDTO.builder()
                        .departmentId(String.valueOf(d.getDepartmentId())) // 엔티티 필드명 확인!
                        .departmentName(d.getDepartmentName())
                        .build())
                .toList();
    }

    // [문서/구분 테이블 긁어오기]
    public List<FilterKeywordDTO> searchDocuments(String keyword) {
        return documentRepository.findByDocumentNameContaining(keyword).stream()
                .map(doc -> FilterKeywordDTO.builder()
                        .documentId(String.valueOf(doc.getDocumentId())) // 엔티티 필드명 확인!
                        .documentName(doc.getDocumentName())
                        .build())
                .toList();
    }
}
