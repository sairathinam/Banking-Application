package com.service;

import com.DTO.LoanApplicationRequest;
import com.DTO.LoanApprovalRequest;
import com.DTO.LoanResponse;
import com.DTO.RepaymentRequest;
import com.Repo.AccountRepository;
import com.Repo.LoanRepaymentHistoryRepository;
import com.Repo.LoanRepository;
import com.Repo.TransactionRepository;
import com.Repo.Userrepo;
import com.model.*;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanServiceIml implements LoanService {

    private final LoanRepository loanRepository;
    private final Userrepo userRepo;
    private final LoanEligibilityChecker loanEligibilityChecker;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepaymentHistoryRepository repaymentHistoryRepository;

    public LoanServiceIml(LoanRepository loanRepository,
                          Userrepo userRepo,
                          LoanEligibilityChecker loanEligibilityChecker,
                          AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          LoanRepaymentHistoryRepository repaymentHistoryRepository) {
        this.loanRepository = loanRepository;
        this.userRepo = userRepo;
        this.loanEligibilityChecker = loanEligibilityChecker;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.repaymentHistoryRepository = repaymentHistoryRepository;
    }

    // ===================== USER OPERATIONS =====================

    @Transactional
    @Override
    public LoanResponse applyForLoan(String email, LoanApplicationRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        loanEligibilityChecker.checkEligibility(user, request.getRequestedAmount());

        Loan loan = new Loan();
        loan.setLoanNumber("LN" + System.currentTimeMillis());
        loan.setUser(user);
        loan.setRequestedAmount(request.getRequestedAmount());
        loan.setTenureInMonths(request.getTenureInMonths());
        loan.setPurpose(request.getPurpose());
        loan.setStatus(LoanStatus.PENDING);
        loan.setCreatedAt(LocalDateTime.now());

        // Link the account number if provided
        if (request.getAccountNumber() != null) {
            accountRepository.findByAccountNumber(request.getAccountNumber())
                    .ifPresent(loan::setDisbursedToAccount);
        }

        Loan savedLoan = loanRepository.save(loan);

        LoanResponse response = mapToResponse(savedLoan);
        response.setMessage("Loan application submitted. Waiting for bank approval.");
        return response;
    }

    @Override
    public List<LoanResponse> getMyLoans(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return loanRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

//    @Transactional
//    @Override
//    public LoanResponse makeRepayment(String email, Long loanId, RepaymentRequest request) {
//        User user = userRepo.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Loan loan = loanRepository.findById(loanId)
//                .orElseThrow(() -> new RuntimeException("Loan not found"));
//
//        if (!loan.getUser().getId().equals(user.getId())) {
//            throw new RuntimeException("You can only repay your own loan");
//        }
//
//        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.DISBURSED) {
//            throw new RuntimeException("This loan is not active for repayment");
//        }
//
//        // FIX: null-safe remaining balance check
//        if (loan.getRemainingBalance() == null) {
//            throw new RuntimeException("Loan balance not set. Please contact admin.");
//        }
//
//        BigDecimal amount = request.getAmount();
//        BigDecimal newBalance = loan.getRemainingBalance().subtract(amount);
//        int newRemainingMonths = Math.max(0, (loan.getRemainingMonths() != null ? loan.getRemainingMonths() : 0) - 1);
//
//        loan.setRemainingBalance(newBalance.max(BigDecimal.ZERO));
//        loan.setRemainingMonths(newRemainingMonths);
//
//        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
//            loan.setStatus(LoanStatus.CLOSED);
//        }
//
//        loanRepository.save(loan);
//
//        // FIX: save repayment history record
//        LoanRepaymentHistory history = new LoanRepaymentHistory();
//        history.setLoan(loan);
//        history.setAmountPaid(amount);
//        history.setBalanceAfterPayment(loan.getRemainingBalance());
//        history.setMonthsRemainingAfterPayment(newRemainingMonths);
//        history.setPaidAt(LocalDateTime.now());
//        history.setRemarks("EMI payment");
//        repaymentHistoryRepository.save(history);
//
//        // Record as a transaction on the user's account
//        if (loan.getDisbursedToAccount() != null) {
//            Transaction tx = new Transaction();
//            tx.setType("LOAN_EMI");
//            tx.setAmount(amount);
//            tx.setDescription("EMI payment for loan " + loan.getLoanNumber());
//            tx.setAccount(loan.getDisbursedToAccount());
//            transactionRepository.save(tx);
//        }
//
//        LoanResponse response = mapToResponse(loan);
//        response.setMessage("EMI payment of ₹" + amount + " successful. Remaining balance: ₹" + loan.getRemainingBalance());
//        return response;
//    }
    @Transactional
    @Override
    public LoanResponse makeRepayment(String email, Long loanId, RepaymentRequest request) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only repay your own loan");
        }

        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.DISBURSED) {
            throw new RuntimeException("This loan is not active for repayment");
        }

        if (loan.getRemainingBalance() == null) {
            throw new RuntimeException("Loan balance not set.");
        }

       
        BigDecimal emi = loan.getEmiAmount();

       
        BigDecimal monthlyRate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

       
        BigDecimal interest = loan.getRemainingBalance()
                .multiply(monthlyRate)
                .setScale(2, RoundingMode.HALF_UP);

       
        BigDecimal principalPaid = emi.subtract(interest);

       
        if (principalPaid.compareTo(loan.getRemainingBalance()) > 0) {
            principalPaid = loan.getRemainingBalance();
            emi = principalPaid.add(interest);
        }

      
        BigDecimal newBalance = loan.getRemainingBalance()
                .subtract(principalPaid)
                .max(BigDecimal.ZERO);

        int newRemainingMonths = Math.max(0,
                (loan.getRemainingMonths() != null ? loan.getRemainingMonths() : 0) - 1);

        loan.setRemainingBalance(newBalance);
        loan.setRemainingMonths(newRemainingMonths);

        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.CLOSED);
        }

        loanRepository.save(loan);

       
        LoanRepaymentHistory history = new LoanRepaymentHistory();
        history.setLoan(loan);
        history.setAmountPaid(emi);
        history.setInterestPaid(interest);        // 🔥 NEW
        history.setPrincipalPaid(principalPaid);  // 🔥 NEW
        history.setBalanceAfterPayment(newBalance);
        history.setMonthsRemainingAfterPayment(newRemainingMonths);
        history.setPaidAt(LocalDateTime.now());
        history.setRemarks("EMI payment");
        repaymentHistoryRepository.save(history);

       
        if (loan.getDisbursedToAccount() != null) {
            Transaction tx = new Transaction();
            tx.setType("LOAN_EMI");
            tx.setAmount(emi);
            tx.setDescription("EMI payment for loan " + loan.getLoanNumber());
            tx.setAccount(loan.getDisbursedToAccount());
            transactionRepository.save(tx);
        }

        LoanResponse response = mapToResponse(loan);
        response.setMessage("EMI paid. Interest: ₹" + interest +
                ", Principal: ₹" + principalPaid +
                ", Remaining: ₹" + newBalance);

        return response;
    }

    // ===================== ADMIN OPERATIONS =====================

    @Override
    public List<LoanResponse> getAllPendingLoans() {
        return loanRepository.findByStatus(LoanStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public LoanResponse approveLoan(Long loanId, LoanApprovalRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException("Only PENDING loans can be approved");
        }

        // Set admin-provided values
        loan.setApprovedAmount(request.getApprovedAmount());
        loan.setInterestRate(request.getInterestRate());
        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovalDate(LocalDate.now());
        loan.setDisbursementDate(LocalDate.now());

        // FIX: calculate EMI using standard formula: EMI = P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal emi = calculateEMI(request.getApprovedAmount(),
                request.getInterestRate(),
                loan.getTenureInMonths());
        loan.setEmiAmount(emi);
        loan.setRemainingBalance(request.getApprovedAmount());
        loan.setRemainingMonths(loan.getTenureInMonths());
        loan.setStatus(LoanStatus.ACTIVE);

        // FIX: credit the approved amount to the user's account
        if (loan.getDisbursedToAccount() != null) {
            Account account = loan.getDisbursedToAccount();
            account.setBalance(account.getBalance().add(request.getApprovedAmount()));
            accountRepository.save(account);

            Transaction tx = new Transaction();
            tx.setType("LOAN_DISBURSEMENT");
            tx.setAmount(request.getApprovedAmount());
            tx.setDescription("Loan disbursement for " + loan.getLoanNumber());
            tx.setAccount(account);
            transactionRepository.save(tx);
        }

        loanRepository.save(loan);

        LoanResponse response = mapToResponse(loan);
        response.setMessage("Loan approved. Amount ₹" + request.getApprovedAmount()
                + " disbursed. Monthly EMI: ₹" + emi);
        return response;
    }

    @Transactional
    @Override
    public LoanResponse rejectLoan(Long loanId, String reason) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException("Only PENDING loans can be rejected");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setRejectionReason(reason);
        loanRepository.save(loan);

        LoanResponse response = mapToResponse(loan);
        response.setMessage("Loan rejected: " + reason);
        return response;
    }

  
     
    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal annualRate, int tenureMonths) {
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            // Zero interest: simple division
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }
        MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), mc);
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal pow = onePlusR.pow(tenureMonths, mc);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(pow);
        BigDecimal denominator = pow.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private LoanResponse mapToResponse(Loan loan) {
        LoanResponse res = new LoanResponse();
        res.setLoanId(loan.getId());
        res.setLoanNumber(loan.getLoanNumber());
        res.setRequestedAmount(loan.getRequestedAmount());
        res.setApprovedAmount(loan.getApprovedAmount());
        res.setInterestRate(loan.getInterestRate());
        res.setTenureInMonths(loan.getTenureInMonths());
        res.setRemainingMonths(loan.getRemainingMonths());
        res.setRemainingBalance(loan.getRemainingBalance());
        res.setEmiAmount(loan.getEmiAmount());
        res.setStatus(loan.getStatus().name());
        res.setPurpose(loan.getPurpose());
        res.setApplicationDate(loan.getApplicationDate());
        res.setApprovalDate(loan.getApprovalDate());
        res.setDisbursementDate(loan.getDisbursementDate());
        res.setRepaymentHistory(loan.getRepaymentHistory());
        return res;
    }
}