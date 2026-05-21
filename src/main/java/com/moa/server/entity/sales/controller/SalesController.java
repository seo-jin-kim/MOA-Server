package com.moa.server.entity.sales.controller;
import com.moa.server.entity.sales.dto.TaxInvoiceResponseDTO;
import com.moa.server.entity.sales.dto.TransactionRequestDTO;
import com.moa.server.entity.sales.dto.TransactionResponseDTO;
import com.moa.server.entity.sales.dto.VendorMonthlyDTO;
import com.moa.server.entity.sales.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {
    private final SalesService salesService;

    //전체조회
    @GetMapping("/journals")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(Pageable pageable) {
        return ResponseEntity.ok(salesService.getTransactions(pageable));
    }

    //상세조회
    @GetMapping("/journals/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(
            @PathVariable Integer transactionId) {
        return ResponseEntity.ok(salesService.getTransaction(transactionId));
    }

    //수정
    @PutMapping("/journals/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Integer transactionId, @RequestBody TransactionRequestDTO request){
        salesService.updateTransaction(transactionId, request);
        return ResponseEntity.ok().build();
    }

    //삭제
    @DeleteMapping("/journals/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Integer transactionId) {
        salesService.deleteTransaction(transactionId);
        return ResponseEntity.ok().build();
    }

    //전자세금계산서 조회
    @GetMapping("/journals/{transactionId}/tax-invoice")
    public ResponseEntity<TaxInvoiceResponseDTO> getTaxInvoice(@PathVariable Integer transactionId){
        return ResponseEntity.ok(salesService.getTaxInvoice(transactionId));
    }

    //전자세금계산서 리스트
    @GetMapping("/taxInv")
    public ResponseEntity<List<TransactionResponseDTO>> getTaxInvoiceList() {
        return ResponseEntity.ok(salesService.getTaxInvoiceList());
    }

    //월별매입집계
    @GetMapping("/expense")
    public ResponseEntity<List<VendorMonthlyDTO>> getMonthlyExpense() {
        return ResponseEntity.ok(salesService.getMonthlyExpense());
    }

    //월별매출집계
    @GetMapping("/revenue")
    public ResponseEntity<List<VendorMonthlyDTO>> getMonthlyRevenue() {
        return ResponseEntity.ok(salesService.getMonthlyRevenue());
    }
}
