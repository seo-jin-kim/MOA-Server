package com.moa.server.entity.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

    List<TransactionEntity> findAllByOrderByTransactionIdDesc();
    @Override
    List<TransactionEntity> findAll();

    @Override
    Page<TransactionEntity> findAll(Pageable pageable);

    @Override
    Optional<TransactionEntity> findById(Integer transactionId);

    List<TransactionEntity> findBySalaryLedgerId(Integer salaryLedgerId);

    Optional<TransactionEntity> findFirstBySalaryLedgerId(Integer salaryLedgerId);

    List<TransactionEntity> findByVendorId(Integer vendorId);

    Optional<TransactionEntity> findFirstByVendorId(Integer vendorId);

    List<TransactionEntity> findByCreatedAt(LocalDateTime createdAt);

    List<TransactionEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<TransactionEntity> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<TransactionEntity> findBySalaryLedgerIdIsNull();

    List<TransactionEntity> findByVendorIdIsNull();

    List<TransactionEntity> findByCreatedAtIsNull();

    Page<TransactionEntity> findByCreatedAtIsNull(Pageable pageable);

    List<TransactionEntity> findByUpdatedAtIsNull();

    Page<TransactionEntity> findByUpdatedAtIsNull(Pageable pageable);

    List<TransactionEntity> findBySalaryLedgerIdIsNotNull();

    List<TransactionEntity> findByVendorIdIsNotNull();

    List<TransactionEntity> findByCreatedAtIsNotNull();

    Page<TransactionEntity> findByCreatedAtIsNotNull(Pageable pageable);

    List<TransactionEntity> findByUpdatedAtIsNotNull();

    Page<TransactionEntity> findByUpdatedAtIsNotNull(Pageable pageable);

    Page<TransactionEntity> findAllByTransactionTypeOrderByTransactionIdDesc(String transactionType, Pageable pageable);

}