package com.moa.server.entity.sales.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionResponseDTO {

    private Integer transactionId;
    private Integer vendorId;
    private String vendorCode;
    private String vendorName;
    private Integer orderformId;
    private Integer salaryLedgerId;
    private Integer transactionNum;
    private String transactionType;
    private Integer transactionPrice;
    private String transactionMemo;
    private String createdAt;



}
