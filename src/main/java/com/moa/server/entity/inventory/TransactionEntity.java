package com.moa.server.entity.inventory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"transaction\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "vendor_id")
    private Integer vendorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", insertable = false, updatable = false)
    private VendorEntity vendor;

    @Column(name = "orderform_id")
    private Integer orderformId;

    @Column(name = "salary_ledger_id")
    private Integer salaryLedgerId;

    @Column(name = "transaction_num")
    private Integer transactionNum;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_price")
    private Integer transactionPrice;

    @Column(name = "transaction_memo")
    private String transactionMemo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}