
package com.Controller;

import com.DTO.LoanApprovalRequest;
import com.DTO.LoanResponse;
import com.Repo.AccountRepository;
import com.Repo.LoanRepository;
import com.Repo.TransactionRepository;
import com.Repo.Userrepo;
import com.model.Account;
import com.model.Loan;
import com.model.Transaction;
import com.model.User;
import com.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")   
public class AdminController {

    private final Userrepo userRepo;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final LoanService loanService;

    public AdminController(Userrepo userRepo,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           LoanRepository loanRepository,
                           LoanService loanService) {
        this.userRepo = userRepo;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.loanRepository = loanRepository;
        this.loanService = loanService;
    }

    // ==================== DASHBOARD SUMMARY ====================

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        long totalUsers = userRepo.count();
        long totalAccounts = accountRepository.count();
        long totalLoans = loanRepository.count();
        long totalTransactions = transactionRepository.count();
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", totalUsers);
        response.put("totalAccounts", totalAccounts);
        response.put("totalLoans", totalLoans);
        response.put("totalTransactions", totalTransactions);

        return ResponseEntity.ok(response);
    }

    // ==================== USERS ====================

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    // ==================== ACCOUNTS ====================

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountRepository.findAll());
    }

    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<List<Account>> getAccountsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(accountRepository.findByUserId(userId));
    }

    // ==================== TRANSACTIONS ====================

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }

    @GetMapping("/users/{userId}/transactions")
    public ResponseEntity<?> getTransactionsByUser(@PathVariable Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        List<Transaction> allTxns = accounts.stream()
                .flatMap(acc -> transactionRepository
                        .findByAccountIdOrderByCreatedAtDesc(acc.getId()).stream())
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(allTxns);
    }

    // ==================== LOANS ====================

    @GetMapping("/loans")
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
    	List<LoanResponse> loans = loanService.getAllLoans();
        
        // Print full JSON in console
        System.out.println("=== ALL LOANS JSON ===");
        loans.forEach(loan -> {
            System.out.println(loan);   // or use ObjectMapper for pretty print
        });
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/loans/pending")
    public ResponseEntity<List<LoanResponse>> getPendingLoans() {
        return ResponseEntity.ok(loanService.getAllPendingLoans());
    }

    @GetMapping("/users/{userId}/loans")
    public ResponseEntity<List<Loan>> getLoansByUser(@PathVariable Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(loanRepository.findByUser(user));
    }

    
    @PostMapping("/loans/{loanId}/approve")
    public ResponseEntity<LoanResponse> approveLoan(
            @PathVariable Long loanId,
            @RequestBody LoanApprovalRequest request) {
        return ResponseEntity.ok(loanService.approveLoan(loanId, request));
    }

   
    @PostMapping("/loans/{loanId}/reject")
    public ResponseEntity<LoanResponse> rejectLoan(
            @PathVariable Long loanId,
            @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "Application rejected by bank");
        return ResponseEntity.ok(loanService.rejectLoan(loanId, reason));
    }
}
