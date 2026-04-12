package com.service;

import com.model.User;
import com.model.Loan;
import com.model.LoanStatus;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class LoanEligibilityChecker {

    public void checkEligibility(User user, BigDecimal requestedAmount) {
        
       
        if (user.getAccounts() == null || user.getAccounts().isEmpty()) {
            throw new RuntimeException("You must have an active bank account to apply for a loan.");
        }

        
        if (requestedAmount.compareTo(new BigDecimal("5000000")) > 0) {
            throw new RuntimeException("Maximum allowed loan amount is ₹50,00,000.");
        }

       
        long activeLoans = user.getLoans() != null ? 
                user.getLoans().stream()
                    .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE || 
                                   loan.getStatus() == LoanStatus.DISBURSED)
                    .count() : 0;

        if (activeLoans >= 2) {
            throw new RuntimeException("You already have the maximum number of active loans allowed.");
        }

       
        boolean hasGoodBalance = user.getAccounts().stream()
                .anyMatch(acc -> acc.getBalance().compareTo(new BigDecimal("500")) >= 0);

        if (!hasGoodBalance) {
            throw new RuntimeException("Insufficient account activity for loan eligibility.");
        }

        System.out.println("✅ Loan eligibility check passed for user: " + user.getEmail());
    }
}