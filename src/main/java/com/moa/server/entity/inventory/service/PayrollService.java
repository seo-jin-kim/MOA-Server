package com.moa.server.entity.inventory.service;

import com.moa.server.entity.inventory.TransactionEntity;
import com.moa.server.entity.inventory.TransactionRepository;
import com.moa.server.entity.inventory.dto.TransactionSalaryDTO;
import com.moa.server.entity.inventory.dto.TransactionSalaryRequestDTO;
import com.moa.server.entity.salary.SalaryEntity;
import com.moa.server.entity.salary.SalaryLedgerEntity;
import com.moa.server.entity.salary.SalaryLedgerRepository;
import com.moa.server.entity.salary.SalaryRepository;
import com.moa.server.entity.salary.TransfeRepository;
import com.moa.server.entity.user.UserEntity;
import com.moa.server.entity.user.UserRepository;
import com.moa.server.entity.vacation.VacationEntity;
import com.moa.server.entity.vacation.VacationRepository;
import com.moa.server.entity.vacation.WorkEntity;
import com.moa.server.entity.vacation.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PayrollService {
    private static final int PAYROLL_VENDOR_ID = 11;
    private static final BigDecimal STANDARD_MONTHLY_WORK_HOURS = BigDecimal.valueOf(209);
    private static final BigDecimal OVERTIME_HOURS_PER_DAY = BigDecimal.valueOf(6);
    private static final BigDecimal OVERTIME_MULTIPLIER = BigDecimal.valueOf(1.2);
    private static final BigDecimal DAILY_WORK_HOURS = BigDecimal.valueOf(8);
    private static final BigDecimal WEEKEND_MULTIPLIER = BigDecimal.valueOf(1.5);
    private static final LocalTime OVERTIME_START_TIME = LocalTime.of(18, 0);

    private final UserRepository userRepository;
    private final SalaryRepository salaryRepository;
    private final SalaryLedgerRepository salaryLedgerRepository;
    private final TransfeRepository transfeRepository;
    private final TransactionRepository transactionRepository;
    private final WorkRepository workRepository;
    private final VacationRepository vacationRepository;

    public List<TransactionEntity> transactionSalaryList() {
        return transactionRepository.findByVendorId(PAYROLL_VENDOR_ID);
    }

    public List<TransactionSalaryDTO> transactionSalaryResponseList() {
        return toSalaryResponses(transactionSalaryList());
    }

    public Page<TransactionSalaryDTO> transactionSalaryPageList(int page, int size) {
        return toPage(transactionSalaryResponseList(), createPageable(page, size));
    }

    public List<TransactionSalaryDTO> transactionSalaryResponseSearch(String searchCondition, String searchKeyword) {
        return toSalaryResponses(transactionSalarySearch(searchCondition, searchKeyword));
    }

    public Page<TransactionSalaryDTO> transactionSalaryPageSearch(
            String searchCondition,
            String searchKeyword,
            int page,
            int size
    ) {
        return toPage(
                transactionSalaryResponseSearch(searchCondition, searchKeyword),
                createPageable(page, size)
        );
    }

    public List<TransactionEntity> transactionSalarySearch(String searchCondition, String searchKeyword) {
        if (searchCondition == null || searchCondition.isBlank()) {
            return transactionSalaryList();
        }

        String condition = searchCondition.trim().toLowerCase();
        String keyword = searchKeyword == null ? "" : searchKeyword.trim();

        List<TransactionEntity> transactions = switch (condition) {
            case "salaryledgerid", "salary_ledger_id" -> {
                Integer salaryLedgerId = parseInteger(keyword);
                yield transactionRepository.findByVendorId(PAYROLL_VENDOR_ID).stream()
                        .filter(transaction -> Objects.equals(resolveSalaryLedgerId(transaction), salaryLedgerId))
                        .toList();
            }
            case "vendorid", "vendor_id" -> transactionRepository.findByVendorId(parseInteger(keyword));
            case "createdat", "created_at" -> {
                LocalDate createdDate = parseLocalDate(keyword);
                yield transactionRepository.findByCreatedAtBetween(
                        createdDate.atStartOfDay(),
                        createdDate.plusDays(1).atStartOfDay()
                );
            }
            case "updatedat", "updated_at", "updateat" -> {
                LocalDate updatedDate = parseLocalDate(keyword);
                yield transactionRepository.findByUpdatedAtBetween(
                        updatedDate.atStartOfDay(),
                        updatedDate.plusDays(1).atStartOfDay()
                );
            }
            default -> throw new IllegalArgumentException("Unsupported search condition: " + searchCondition);
        };

        return transactions.stream()
                .filter(transaction -> Objects.equals(transaction.getVendorId(), PAYROLL_VENDOR_ID))
                .toList();
    }

    public TransactionSalaryDTO transactionSalaryInfo(Integer transactionId) {
        return transactionRepository.findById(transactionId)
                .filter(transaction -> Objects.equals(transaction.getVendorId(), PAYROLL_VENDOR_ID))
                .map(transaction -> toSalaryResponses(List.of(transaction)))
                .filter(responses -> !responses.isEmpty())
                .map(responses -> responses.get(0))
                .orElse(null);
    }

    public TransactionEntity transactionSalaryAdd(TransactionSalaryRequestDTO transactionRequest) {
        SalaryLedgerEntity salaryLedger = resolveOrCreateSalaryLedger(transactionRequest);
        Long salaryAmount = resolveSalaryAmount(transactionRequest, salaryLedger);

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionId(transactionRequest.getTransactionId())
                .vendorId(transactionRequest.getVendorId() == null ? PAYROLL_VENDOR_ID : transactionRequest.getVendorId())
                .salaryLedgerId(salaryLedger == null ? transactionRequest.getSalaryLedgerId() : salaryLedger.getSalaryLedgerId())
                .transactionNum(resolveTransactionNum(transactionRequest, salaryLedger))
                .transactionType(transactionRequest.getTransactionType() == null ? "급여" : transactionRequest.getTransactionType())
                .transactionPrice(resolveTransactionPrice(transactionRequest, salaryAmount))
                .transactionMemo(transactionRequest.getTransactionMemo())
                .createdAt(transactionRequest.getCreatedAt())
                .updatedAt(transactionRequest.getUpdatedAt())
                .build();

        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(LocalDateTime.now());
        }

        return transactionRepository.save(transaction);
    }

    private SalaryLedgerEntity resolveOrCreateSalaryLedger(TransactionSalaryRequestDTO transactionRequest) {
        if (transactionRequest.getSalaryLedgerId() != null
                && transactionRequest.getUserId() == null
                && transactionRequest.getBankTransferId() == null
                && transactionRequest.getSalaryDate() == null
                && transactionRequest.getSalaryAmount() == null
                && transactionRequest.getBasePay() == null
                && transactionRequest.getOvertimeAllowance() == null
                && transactionRequest.getWeekendAllowance() == null
                && transactionRequest.getAnnualAllowance() == null) {
            return salaryLedgerRepository.findById(transactionRequest.getSalaryLedgerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary ledger not found."));
        }

        if (transactionRequest.getUserId() == null
                && transactionRequest.getBankTransferId() == null
                && transactionRequest.getSalaryDate() == null
                && transactionRequest.getSalaryAmount() == null
                && transactionRequest.getBasePay() == null
                && transactionRequest.getOvertimeAllowance() == null
                && transactionRequest.getWeekendAllowance() == null
                && transactionRequest.getAnnualAllowance() == null) {
            return null;
        }

        if (transactionRequest.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required.");
        }

        if (!userRepository.existsById(transactionRequest.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found.");
        }

        Long salaryAmount = salaryRepository.findByUserId(transactionRequest.getUserId())
                .map(SalaryEntity::getBasePay)
                .orElse(transactionRequest.getSalaryAmount());

        if (salaryAmount == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary base pay not found.");
        }

        SalaryLedgerEntity salaryLedger = SalaryLedgerEntity.builder()
                .userId(transactionRequest.getUserId())
                .bankTransferId(transactionRequest.getBankTransferId())
                .salaryDate(transactionRequest.getSalaryDate())
                .basePay(resolveLedgerBasePay(transactionRequest, salaryAmount))
                .salaryAmount(salaryAmount)
                .overtimeAllowance(defaultAllowanceAmount(transactionRequest.getOvertimeAllowance()))
                .weekendAllowance(defaultAllowanceAmount(transactionRequest.getWeekendAllowance()))
                .annualAllowance(transactionRequest.getAnnualAllowance())
                .build();

        return salaryLedgerRepository.save(salaryLedger);
    }

    private Long resolveLedgerBasePay(TransactionSalaryRequestDTO transactionRequest, Long defaultBasePay) {
        if (transactionRequest.getBasePay() != null) {
            return transactionRequest.getBasePay();
        }

        return defaultBasePay;
    }

    private Long defaultAllowanceAmount(Long allowance) {
        return allowance == null ? 0L : allowance;
    }

    private Long resolveSalaryAmount(TransactionSalaryRequestDTO transactionRequest, SalaryLedgerEntity salaryLedger) {
        if (salaryLedger != null && salaryLedger.getSalaryAmount() != null) {
            return salaryLedger.getSalaryAmount();
        }

        return transactionRequest.getSalaryAmount();
    }

    private Integer resolveTransactionNum(
            TransactionSalaryRequestDTO transactionRequest,
            SalaryLedgerEntity salaryLedger
    ) {
        if (transactionRequest.getTransactionNum() != null) {
            return transactionRequest.getTransactionNum();
        }

        return salaryLedger == null ? null : salaryLedger.getSalaryLedgerId();
    }

    private Integer resolveTransactionPrice(TransactionSalaryRequestDTO transactionRequest, Long salaryAmount) {
        if (transactionRequest.getTransactionPrice() != null) {
            return transactionRequest.getTransactionPrice();
        }

        if (salaryAmount == null) {
            return null;
        }

        if (salaryAmount > Integer.MAX_VALUE || salaryAmount < Integer.MIN_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary amount is out of transaction price range.");
        }

        return salaryAmount.intValue();
    }

    public TransactionEntity transactionSalaryUpdate(
            Integer transactionId,
            TransactionSalaryRequestDTO transactionRequest
    ) {
        TransactionEntity transaction = transactionRepository.findById(transactionId)
                .filter(item -> Objects.equals(item.getVendorId(), PAYROLL_VENDOR_ID))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payroll transaction not found."));
        SalaryLedgerEntity salaryLedger = resolveLinkedSalaryLedger(transaction, transactionRequest);

        if (transactionRequest.getVendorId() != null) {
            transaction.setVendorId(transactionRequest.getVendorId());
        }

        if (transactionRequest.getSalaryLedgerId() != null) {
            transaction.setSalaryLedgerId(transactionRequest.getSalaryLedgerId());
        }

        if (transactionRequest.getTransactionNum() != null) {
            transaction.setTransactionNum(transactionRequest.getTransactionNum());
        }

        if (transactionRequest.getTransactionType() != null) {
            transaction.setTransactionType(transactionRequest.getTransactionType());
        }

        if (transactionRequest.getTransactionPrice() != null) {
            transaction.setTransactionPrice(transactionRequest.getTransactionPrice());
        }

        if (transactionRequest.getTransactionMemo() != null) {
            transaction.setTransactionMemo(transactionRequest.getTransactionMemo());
        }

        if (transactionRequest.getCreatedAt() != null) {
            transaction.setCreatedAt(transactionRequest.getCreatedAt());
        }

        if (salaryLedger != null) {
            if (transactionRequest.getUserId() != null) {
                salaryLedger.setUserId(transactionRequest.getUserId());
            }

            if (transactionRequest.getBankTransferId() != null) {
                salaryLedger.setBankTransferId(transactionRequest.getBankTransferId());
            }

            if (transactionRequest.getSalaryDate() != null) {
                salaryLedger.setSalaryDate(transactionRequest.getSalaryDate());
            }

            if (transactionRequest.getBasePay() != null) {
                salaryLedger.setBasePay(transactionRequest.getBasePay());
            }

            if (transactionRequest.getSalaryAmount() != null) {
                salaryLedger.setSalaryAmount(transactionRequest.getSalaryAmount());
            }

            if (transactionRequest.getOvertimeAllowance() != null) {
                salaryLedger.setOvertimeAllowance(transactionRequest.getOvertimeAllowance());
            }

            if (transactionRequest.getWeekendAllowance() != null) {
                salaryLedger.setWeekendAllowance(transactionRequest.getWeekendAllowance());
            }

            if (transactionRequest.getAnnualAllowance() != null) {
                salaryLedger.setAnnualAllowance(transactionRequest.getAnnualAllowance());
            }

            salaryLedgerRepository.save(salaryLedger);
        }

        if (Boolean.TRUE.equals(transactionRequest.getClearUpdatedAt())) {
            transaction.setUpdatedAt(null);
        } else if (transactionRequest.isUpdatedAtProvided()) {
            transaction.setUpdatedAt(transactionRequest.getUpdatedAt());
        }

        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        transactionRepository.flush();

        return savedTransaction;
    }

    public void transactionSalaryDelete(Integer transactionId) {
        TransactionEntity transaction = transactionRepository.findById(transactionId)
                .filter(item -> Objects.equals(item.getVendorId(), PAYROLL_VENDOR_ID))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payroll transaction not found."));

        Integer salaryLedgerId = resolveSalaryLedgerId(transaction);
        transactionRepository.delete(transaction);
        transactionRepository.flush();

        if (salaryLedgerId == null) {
            return;
        }

        boolean isReferencedByOtherPayroll = transactionRepository.findBySalaryLedgerId(salaryLedgerId).stream()
                .filter(other -> Objects.equals(other.getVendorId(), PAYROLL_VENDOR_ID))
                .filter(other -> !Objects.equals(other.getTransactionId(), transactionId))
                .findAny()
                .isPresent();

        if (!isReferencedByOtherPayroll) {
            transfeRepository.deleteBySalaryLedger_SalaryLedgerId(salaryLedgerId);
            salaryLedgerRepository.findById(salaryLedgerId)
                    .ifPresent(salaryLedgerRepository::delete);
        }
    }

    private SalaryLedgerEntity resolveLinkedSalaryLedger(
            TransactionEntity transaction,
            TransactionSalaryRequestDTO transactionRequest
    ) {
        Integer salaryLedgerId = transactionRequest.getSalaryLedgerId();

        if (salaryLedgerId == null) {
            salaryLedgerId = resolveSalaryLedgerId(transaction);
        }

        if (salaryLedgerId == null) {
            return null;
        }

        return salaryLedgerRepository.findById(salaryLedgerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary ledger not found."));
    }

    private List<TransactionSalaryDTO> toSalaryResponses(List<TransactionEntity> transactions) {
        Map<Integer, SalaryLedgerEntity> salaryLedgerById = loadSalaryLedgers(transactions);
        Map<Integer, UserEntity> userById = loadUsers(salaryLedgerById.values());
        Map<Integer, SalaryEntity> salaryByUserId = loadSalaries(userById.keySet());
        Map<Integer, List<WorkEntity>> workByUserId = loadWorks(userById.keySet());
        Map<Integer, VacationEntity> vacationByUserId = loadVacations(userById.keySet());

        return transactions.stream()
                .map(transaction -> {
                    Integer salaryLedgerId = resolveSalaryLedgerId(transaction);
                    SalaryLedgerEntity salaryLedger =
                            salaryLedgerId == null ? null : salaryLedgerById.get(salaryLedgerId);
                    Integer userId = salaryLedger == null ? null : salaryLedger.getUserId();
                    UserEntity user = userId == null ? null : userById.get(userId);
                    SalaryEntity salary = userId == null ? null : salaryByUserId.get(userId);
                    List<WorkEntity> workRecords = userId == null ? List.of() : workByUserId.getOrDefault(userId, List.of());
                    VacationEntity vacation = userId == null ? null : vacationByUserId.get(userId);
                    LocalDate payrollDate = resolvePayrollDate(transaction, salaryLedger);
                    Long basePay = resolveBasePay(salaryLedger, salary);
                    PayrollAllowanceSummary allowanceSummary = calculateAllowanceSummary(
                            basePay,
                            workRecords,
                            vacation,
                            payrollDate
                    );

                    return toSalaryResponse(
                            transaction,
                            salaryLedgerId,
                            salaryLedger,
                            user,
                            salary,
                            basePay,
                            mergeAllowanceSummary(salaryLedger, allowanceSummary)
                    );
                })
                .toList();
    }

    private Long resolveBasePay(SalaryLedgerEntity salaryLedger, SalaryEntity salary) {
        if (salaryLedger != null && salaryLedger.getBasePay() != null) {
            return salaryLedger.getBasePay();
        }

        return salary == null ? null : salary.getBasePay();
    }

    private PayrollAllowanceSummary mergeAllowanceSummary(
            SalaryLedgerEntity salaryLedger,
            PayrollAllowanceSummary calculatedSummary
    ) {
        if (salaryLedger == null) {
            return calculatedSummary;
        }

        return new PayrollAllowanceSummary(
                salaryLedger.getOvertimeAllowance() != null
                        ? salaryLedger.getOvertimeAllowance()
                        : calculatedSummary.getOvertimeAllowance(),
                salaryLedger.getWeekendAllowance() != null
                        ? salaryLedger.getWeekendAllowance()
                        : calculatedSummary.getWeekendAllowance(),
                salaryLedger.getAnnualAllowance() != null
                        ? salaryLedger.getAnnualAllowance()
                        : calculatedSummary.getAnnualAllowance()
        );
    }

    private Integer resolveSalaryLedgerId(TransactionEntity transaction) {
        Integer transactionNum = transaction.getTransactionNum();

        if (transactionNum != null) {
            if (transactionNum >= 8000) {
                return transactionNum - 8000;
            }

            return transactionNum;
        }

        return transaction.getSalaryLedgerId();
    }

    private Map<Integer, SalaryLedgerEntity> loadSalaryLedgers(List<TransactionEntity> transactions) {
        List<Integer> salaryLedgerIds = transactions.stream()
                .map(this::resolveSalaryLedgerId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (salaryLedgerIds.isEmpty()) {
            return Map.of();
        }

        return salaryLedgerRepository.findBySalaryLedgerIdIn(salaryLedgerIds).stream()
                .collect(Collectors.toMap(
                        SalaryLedgerEntity::getSalaryLedgerId,
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private Map<Integer, UserEntity> loadUsers(Collection<SalaryLedgerEntity> salaryLedgers) {
        Map<Integer, UserEntity> userById = new LinkedHashMap<>();

        for (SalaryLedgerEntity salaryLedger : salaryLedgers) {
            Integer userId = salaryLedger.getUserId();

            if (userId == null || userById.containsKey(userId)) {
                continue;
            }

            userRepository.findById(userId).ifPresent(user -> userById.put(userId, user));
        }

        return userById;
    }

    private Map<Integer, SalaryEntity> loadSalaries(Collection<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        return salaryRepository.findByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(
                        SalaryEntity::getUserId,
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private Map<Integer, List<WorkEntity>> loadWorks(Collection<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        return workRepository.findByUserIdIn(userIds).stream()
                .filter(work -> work.getUserId() != null)
                .collect(Collectors.groupingBy(
                        WorkEntity::getUserId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private Map<Integer, VacationEntity> loadVacations(Collection<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        return vacationRepository.findByUserIdIn(userIds).stream()
                .filter(vacation -> vacation.getUserId() != null)
                .collect(Collectors.toMap(
                        VacationEntity::getUserId,
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private LocalDate resolvePayrollDate(TransactionEntity transaction, SalaryLedgerEntity salaryLedger) {
        if (salaryLedger != null && salaryLedger.getSalaryDate() != null) {
            return salaryLedger.getSalaryDate().toLocalDate();
        }

        if (transaction.getCreatedAt() != null) {
            return transaction.getCreatedAt().toLocalDate();
        }

        if (transaction.getUpdatedAt() != null) {
            return transaction.getUpdatedAt().toLocalDate();
        }

        return null;
    }

    private PayrollAllowanceSummary calculateAllowanceSummary(
            Long basePay,
            List<WorkEntity> workRecords,
            VacationEntity vacation,
            LocalDate payrollDate
    ) {
        if (basePay == null || basePay <= 0) {
            return PayrollAllowanceSummary.empty();
        }

        long overtimeAllowance = 0L;
        long weekendAllowance = 0L;

        if (payrollDate != null) {
            YearMonth payrollMonth = YearMonth.from(payrollDate);
            long overtimeDays = countOvertimeDays(workRecords, payrollMonth);
            long weekendDays = countWeekendDays(workRecords, payrollMonth);

            overtimeAllowance = calculateOvertimeAllowance(basePay, overtimeDays);
            weekendAllowance = calculateWeekendAllowance(basePay, weekendDays);
        }

        Long annualAllowance = isAnnualAllowanceMonth(payrollDate)
                ? calculateAnnualAllowance(basePay, resolveRemainingVacationDays(vacation))
                : null;

        return new PayrollAllowanceSummary(overtimeAllowance, weekendAllowance, annualAllowance);
    }

    private long countOvertimeDays(List<WorkEntity> workRecords, YearMonth payrollMonth) {
        return workRecords.stream()
                .filter(work -> isInPayrollMonth(work, payrollMonth))
                .filter(this::isPayrollOvertimeWorkDay)
                .map(WorkEntity::getWorkDate)
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }

    private long countWeekendDays(List<WorkEntity> workRecords, YearMonth payrollMonth) {
        return workRecords.stream()
                .filter(work -> isInPayrollMonth(work, payrollMonth))
                .filter(this::isPayrollWeekendWorkDay)
                .map(WorkEntity::getWorkDate)
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }

    private boolean isInPayrollMonth(WorkEntity work, YearMonth payrollMonth) {
        return work.getWorkDate() != null && YearMonth.from(work.getWorkDate()).equals(payrollMonth);
    }

    private boolean isOvertimeWorkDay(WorkEntity work) {
        if (work.getFinishWork() != null && work.getFinishWork().toLocalTime().isAfter(OVERTIME_START_TIME)) {
            return true;
        }

        if (containsText(work.getWorkStatus(), "??⑤젏")) {
            return true;
        }

        return work.getAllowance() != null && containsText(work.getAllowance().getAllowanceName(), "??⑤젏");
    }

    private boolean isWeekendWorkDay(WorkEntity work) {
        if (work.getWorkDate() == null) {
            return false;
        }

        DayOfWeek dayOfWeek = work.getWorkDate().getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private boolean isPayrollOvertimeWorkDay(WorkEntity work) {
        if (work.getFinishWork() != null && work.getFinishWork().toLocalTime().isAfter(OVERTIME_START_TIME)) {
            return true;
        }

        return containsAnyText(
                work.getWorkStatus(),
                "\uC57C\uADFC",
                "\uC5F0\uC7A5",
                "night",
                "over"
        ) || (work.getAllowance() != null && containsAnyText(
                work.getAllowance().getAllowanceName(),
                "\uC57C\uADFC",
                "\uC5F0\uC7A5",
                "night",
                "over"
        ));
    }

    private boolean isPayrollWeekendWorkDay(WorkEntity work) {
        return containsAnyText(
                work.getWorkStatus(),
                "\uC8FC\uB9D0\uADFC\uBB34",
                "\uD734\uC77C\uADFC\uBB34",
                "weekend",
                "holiday"
        ) || (work.getAllowance() != null && containsAnyText(
                work.getAllowance().getAllowanceName(),
                "\uC8FC\uB9D0",
                "\uD734\uC77C",
                "weekend",
                "holiday"
        ));
    }

    private int resolveRemainingVacationDays(VacationEntity vacation) {
        if (vacation == null || vacation.getRemainingVacation() == null || vacation.getRemainingVacation() < 0) {
            return 0;
        }

        return vacation.getRemainingVacation();
    }

    private boolean containsText(String source, String keyword) {
        return source != null && source.contains(keyword);
    }

    private boolean containsAnyText(String source, String... keywords) {
        if (source == null || source.isBlank()) {
            return false;
        }

        String normalizedSource = source.toLowerCase();

        for (String keyword : keywords) {
            if (keyword != null && normalizedSource.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private long calculateOvertimeAllowance(Long basePay, long overtimeDays) {
        if (overtimeDays <= 0) {
            return 0L;
        }

        BigDecimal weightedHours = OVERTIME_HOURS_PER_DAY
                .multiply(BigDecimal.valueOf(overtimeDays))
                .multiply(OVERTIME_MULTIPLIER);

        return calculateAllowanceAmount(basePay, weightedHours);
    }

    private long calculateWeekendAllowance(Long basePay, long weekendDays) {
        if (weekendDays <= 0) {
            return 0L;
        }

        BigDecimal weightedHours = DAILY_WORK_HOURS
                .multiply(BigDecimal.valueOf(weekendDays))
                .multiply(WEEKEND_MULTIPLIER);

        return calculateAllowanceAmount(basePay, weightedHours);
    }

    private long calculateAnnualAllowance(Long basePay, int remainingVacationDays) {
        if (remainingVacationDays <= 0) {
            return 0L;
        }

        BigDecimal hours = DAILY_WORK_HOURS.multiply(BigDecimal.valueOf(remainingVacationDays));
        return calculateAllowanceAmount(basePay, hours);
    }

    private boolean isAnnualAllowanceMonth(LocalDate payrollDate) {
        return payrollDate != null && payrollDate.getMonthValue() == 4;
    }

    private long calculateAllowanceAmount(Long basePay, BigDecimal weightedHours) {
        BigDecimal hourlyWage = BigDecimal.valueOf(basePay)
                .divide(STANDARD_MONTHLY_WORK_HOURS, 10, RoundingMode.HALF_UP);

        return hourlyWage
                .multiply(weightedHours)
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    private TransactionSalaryDTO toSalaryResponse(
            TransactionEntity transaction,
            Integer resolvedSalaryLedgerId,
            SalaryLedgerEntity salaryLedger,
            UserEntity user,
            SalaryEntity salary,
            Long resolvedBasePay,
            PayrollAllowanceSummary allowanceSummary
    ) {
        Long salaryAmount = salaryLedger == null ? null : salaryLedger.getSalaryAmount();

        if (salaryAmount == null && transaction.getTransactionPrice() != null) {
            salaryAmount = transaction.getTransactionPrice().longValue();
        }

        return TransactionSalaryDTO.builder()
                .transactionId(transaction.getTransactionId())
                .vendorId(transaction.getVendorId())
                .salaryLedgerId(resolvedSalaryLedgerId != null ? resolvedSalaryLedgerId : transaction.getSalaryLedgerId())
                .transactionNum(transaction.getTransactionNum())
                .transactionType(transaction.getTransactionType())
                .transactionPrice(transaction.getTransactionPrice())
                .transactionMemo(transaction.getTransactionMemo())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .userId(user == null ? null : user.getUserId())
                .userName(user == null ? null : user.getUserName())
                .employeeId(user == null ? null : user.getEmployeeId())
                .departmentId(user == null ? null : user.getDepartmentId())
                .departmentName(resolveDepartmentName(user))
                .gradeId(user == null ? null : user.getGradeId())
                .gradeName(resolveGradeName(user))
                .basePay(resolvedBasePay)
                .bankTransferId(salaryLedger == null ? null : salaryLedger.getBankTransferId())
                .salaryDate(salaryLedger == null ? null : salaryLedger.getSalaryDate())
                .salaryAmount(salaryAmount)
                .overtimeAllowance(allowanceSummary.getOvertimeAllowance())
                .weekendAllowance(allowanceSummary.getWeekendAllowance())
                .annualAllowance(allowanceSummary.getAnnualAllowance())
                .build();
    }

    private String resolveDepartmentName(UserEntity user) {
        if (user == null || user.getDepartment() == null) {
            return null;
        }

        return user.getDepartment().getDepartmentName();
    }

    private String resolveGradeName(UserEntity user) {
        if (user == null || user.getGrade() == null) {
            return null;
        }

        return user.getGrade().getGradeName();
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Numeric search conditions require a numeric value.");
        }
    }

    private LocalDate parseLocalDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date search conditions require yyyy-MM-dd.");
        }
    }

    private Pageable createPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "transactionId"));
    }

    private Page<TransactionSalaryDTO> toPage(List<TransactionSalaryDTO> items, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), items.size());
        List<TransactionSalaryDTO> content = start >= items.size() ? List.of() : items.subList(start, end);

        return new PageImpl<>(content, pageable, items.size());
    }

    private static final class PayrollAllowanceSummary {
        private final Long overtimeAllowance;
        private final Long weekendAllowance;
        private final Long annualAllowance;

        private PayrollAllowanceSummary(Long overtimeAllowance, Long weekendAllowance, Long annualAllowance) {
            this.overtimeAllowance = overtimeAllowance;
            this.weekendAllowance = weekendAllowance;
            this.annualAllowance = annualAllowance;
        }

        private static PayrollAllowanceSummary empty() {
            return new PayrollAllowanceSummary(0L, 0L, null);
        }

        private Long getOvertimeAllowance() {
            return overtimeAllowance;
        }

        private Long getWeekendAllowance() {
            return weekendAllowance;
        }

        private Long getAnnualAllowance() {
            return annualAllowance;
        }
    }
}
