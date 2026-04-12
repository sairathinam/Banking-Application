
package com.Controller;

import com.DTO.LoanApplicationRequest;
import com.DTO.LoanResponse;
import com.DTO.RepaymentRequest;
import com.Repo.LoanRepaymentHistoryRepository;
import com.model.LoanRepaymentHistory;
import com.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final LoanRepaymentHistoryRepository repaymentHistoryRepository;

    public LoanController(LoanService loanService,
                          LoanRepaymentHistoryRepository repaymentHistoryRepository) {
        this.loanService = loanService;
        this.repaymentHistoryRepository = repaymentHistoryRepository;
    }

   
    @PostMapping("/apply")
    public ResponseEntity<LoanResponse> applyForLoan(
            @RequestBody LoanApplicationRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(loanService.applyForLoan(email, request));
    }

   
    @GetMapping
    public ResponseEntity<List<LoanResponse>> getMyLoans(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(loanService.getMyLoans(email));
    }

   
    @PostMapping("/{loanId}/repay")
    public ResponseEntity<LoanResponse> repayLoan(
            @PathVariable Long loanId,
            @RequestBody RepaymentRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(loanService.makeRepayment(email, loanId, request));
    }

    
    @GetMapping("/{loanId}/history")
    public ResponseEntity<List<LoanRepaymentHistory>> getRepaymentHistory(
            @PathVariable Long loanId) {
        return ResponseEntity.ok(
                repaymentHistoryRepository.findByLoanIdOrderByPaidAtDesc(loanId));
    }
}