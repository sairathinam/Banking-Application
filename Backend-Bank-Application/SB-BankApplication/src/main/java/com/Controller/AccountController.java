
package com.Controller;

import com.Repo.AccountRepository;
import com.Repo.TransactionRepository;
import com.Repo.Userrepo;
import com.model.Account;
import com.model.Transaction;
import com.model.User;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final Userrepo userRepo;


    public AccountController(AccountRepository accountRepository,
                             TransactionRepository transactionRepository,
                             Userrepo userRepo) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepo = userRepo;

        
    }

   
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Account> accounts = accountRepository.findByUserId(user.getId());

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("holderName", user.getFullName());
        dashboard.put("phoneNumber", user.getPhoneNumber());
        dashboard.put("email", user.getEmail());

        dashboard.put("accounts", accounts.stream().map(acc -> {
            Map<String, Object> a = new HashMap<>();
            a.put("accountNumber", acc.getAccountNumber());
            a.put("accountType", acc.getAccountType());
            a.put("balance", acc.getBalance());
            return a;
        }).collect(java.util.stream.Collectors.toList()));

        return ResponseEntity.ok(dashboard);
    }

   
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<?> deposit(@PathVariable String accountNumber,
                                     @RequestBody Map<String, BigDecimal> request,
                                     @AuthenticationPrincipal String email) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            return error(HttpStatus.FORBIDDEN, "Access denied");
        }

        BigDecimal amount = request.get("amount");

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return error(HttpStatus.BAD_REQUEST, "Invalid amount");
        }

       
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        
        Transaction tx = new Transaction();
        tx.setType("DEPOSIT");
        tx.setAmount(amount);
        tx.setDescription("Deposit to account");
        tx.setAccount(account);
        transactionRepository.save(tx);

        
        User user = account.getUser();

        Map<String, Object> response = new HashMap<>();
        response.put("newBalance", account.getBalance());

        return ResponseEntity.ok(response);
    }

   
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String accountNumber,
                                      @RequestBody Map<String, BigDecimal> request,
                                      @AuthenticationPrincipal String email) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            return error(HttpStatus.FORBIDDEN, "Access denied");
        }

        BigDecimal amount = request.get("amount");

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return error(HttpStatus.BAD_REQUEST, "Invalid amount");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            return error(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

       
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        
        Transaction tx = new Transaction();
        tx.setType("WITHDRAW");
        tx.setAmount(amount);
        tx.setDescription("Withdrawal from account");
        tx.setAccount(account);
        transactionRepository.save(tx);

        User user = account.getUser();

        Map<String, Object> response = new HashMap<>();
        response.put("newBalance", account.getBalance());

        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String accountNumber,
                                        @AuthenticationPrincipal String email) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            return error(HttpStatus.FORBIDDEN, "Access denied");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("accountNumber", account.getAccountNumber());
        response.put("balance", account.getBalance());

        return ResponseEntity.ok(response);
    }

  
    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable String accountNumber) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            return error(HttpStatus.FORBIDDEN, "Access denied");
        }

        List<Transaction> txns = transactionRepository
                .findByAccountIdOrderByCreatedAtDesc(account.getId());

        return ResponseEntity.ok(txns);
    }

    
    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(error);
    }

}
