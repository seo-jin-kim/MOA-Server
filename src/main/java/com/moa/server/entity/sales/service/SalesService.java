package com.moa.server.entity.sales.service;

import com.moa.server.common.exception.CustomException;
import com.moa.server.common.exception.ErrorCode;
import com.moa.server.entity.inventory.*;
import com.moa.server.entity.inventory.dto.OrderDTO;
import com.moa.server.entity.sales.dto.VendorMonthlyDTO;
import com.moa.server.entity.sales.dto.TaxInvoiceResponseDTO;
import com.moa.server.entity.sales.dto.TransactionRequestDTO;
import com.moa.server.entity.sales.dto.TransactionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SalesService {

    private final TransactionRepository transactionRepository;
    private final OrdererRepository ordererRepository;
    private final VendorRepository vendorRepository;
    private List<VendorMonthlyDTO> getMonthlyStats(List<TransactionEntity> transactions){

        int currentYear = LocalDateTime.now().getYear();
        Map<Integer, List<TransactionEntity>> groupedByVendor = transactions.stream()
                .filter(t -> t.getCreatedAt() != null)  //더미데이터로 데이터를 저장했기 때문에 createdAt이 혹시 null일 경우를 방지
                .filter(t -> t.getCreatedAt().getYear() == currentYear)
                .collect(Collectors.groupingBy(TransactionEntity::getVendorId));

        //entrySet -> Map을 순회할 때 나오는 키. 값
        return groupedByVendor.values().stream()
                .map(transactionEntities -> {
                    VendorEntity vendor = transactionEntities.getFirst().getVendor();
                    List<Long> monthly = new ArrayList<>(Collections.nCopies(12, 0L));

                    for (TransactionEntity t : transactionEntities) {
                        int monthIdx = t.getCreatedAt().getMonthValue() - 1;
                        monthly.set(monthIdx, monthly.get(monthIdx) + t.getTransactionPrice());
                    }

                    long total = monthly.stream().mapToLong(Long::longValue).sum();

                    return VendorMonthlyDTO.builder()
                            .vendorCode(vendor != null ? vendor.getVendorCord() : null)
                            .vendorName(vendor != null ? vendor.getVendorName() : null)
                            .monthly(monthly)
                            .total(total)
                            .build();
                })
                .toList();
    }

    private static final Integer SUPPLIER_VENDOR_ID = 11;

    private TransactionResponseDTO toDTO(TransactionEntity t){
        return TransactionResponseDTO.builder()
                .transactionId(t.getTransactionId())
                .vendorId(t.getVendorId())
                .vendorCode(t.getVendor().getVendorCord())
                .vendorName(t.getVendor().getVendorName())
                .orderformId(t.getOrderformId())
                .salaryLedgerId(t.getSalaryLedgerId())
                .transactionNum(t.getTransactionNum())
                .transactionType(t.getTransactionType())
                .transactionPrice(t.getTransactionPrice())
                .transactionMemo(t.getTransactionMemo())
                .createdAt(t.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    //전체조회
    public List<TransactionResponseDTO> getTransactions() {
        return transactionRepository.findAllByOrderByTransactionIdDesc()
                .stream()
                .filter(t -> t.getTransactionType().equals("일반전표"))
                .map(this::toDTO).toList();
    }

    //상세조회
    public TransactionResponseDTO getTransaction(Integer transactionId){
        TransactionEntity t = transactionRepository.findById(transactionId)
                .orElseThrow(()-> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));
        return toDTO(t);
    }

    //수정
    public void updateTransaction(Integer transactionId, TransactionRequestDTO request){
        TransactionEntity t = transactionRepository.findById(transactionId)
                .orElseThrow(()-> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));
        t.setTransactionPrice(request.getTransactionPrice());
        t.setTransactionMemo(request.getTransactionMemo());
    }

    //삭제
    public void deleteTransaction(Integer transactionId){
        TransactionEntity t = transactionRepository.findById(transactionId)
                .orElseThrow(()-> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        transactionRepository.delete(t);
    }

    //전자세금계산서 조회
    public TaxInvoiceResponseDTO getTaxInvoice(Integer transactionId){

        TransactionEntity t = transactionRepository.findById(transactionId)
                .orElseThrow(()-> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        VendorEntity supplier = vendorRepository.findById(SUPPLIER_VENDOR_ID)
                .orElseThrow(()-> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        VendorEntity receiver = vendorRepository.findById(t.getVendorId())
                .orElseThrow(()-> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        List<OrderDTO> items = ordererRepository.findByOrderformId(t.getOrderformId())
                .stream()
                .map(OrdererEntity::toinfoDTO)
                .toList();

        int supplyPrice = t.getTransactionPrice();
        int tax = (int) (supplyPrice * 0.1);
        int totalPrice = supplyPrice + tax;

        return TaxInvoiceResponseDTO.builder()

                .supplierName(supplier.getVendorName())
                .supplierCode(supplier.getVendorCord())
                .receiverName(receiver.getVendorName())
                .receiverCode(receiver.getVendorCord())
                .transactionId(t.getTransactionId())
                .transactionDate(t.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .supplyPrice(supplyPrice)
                .tax(tax)
                .totalPrice(totalPrice)
                .items(items)
                .build();

    }

    // 전자세금계산서 목록 조회
    public List<TransactionResponseDTO> getTaxInvoiceList() {
        return transactionRepository.findAllByOrderByTransactionIdDesc()
                .stream()
                .filter(t -> t.getOrderformId() != null)
                .map(this::toDTO)
                .toList();
    }

    //월별매입집계
    public List<VendorMonthlyDTO> getMonthlyExpense(){
        List<TransactionEntity> transactions = transactionRepository.findAllByOrderByTransactionIdDesc()
                .stream()
                .filter(t -> t.getOrderformId() != null)
                .toList();

        return getMonthlyStats(transactions);
    }

    //월별매출집계
    public List<VendorMonthlyDTO> getMonthlyRevenue(){
        List<TransactionEntity> transactions = transactionRepository.findAllByOrderByTransactionIdDesc()
                .stream()
                .filter(t -> t.getTransactionType().equals("매출전표"))
                .toList();
        return getMonthlyStats(transactions);
    }
}
