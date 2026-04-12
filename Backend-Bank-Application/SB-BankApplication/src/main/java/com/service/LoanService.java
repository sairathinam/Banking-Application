
package com.service;

import com.DTO.LoanApplicationRequest;
import com.DTO.LoanApprovalRequest;
import com.DTO.LoanResponse;
import com.DTO.RepaymentRequest;
import java.util.List;

public interface LoanService {

    // User operations
    LoanResponse applyForLoan(String email, LoanApplicationRequest request);
    List<LoanResponse> getMyLoans(String email);
    LoanResponse makeRepayment(String email, Long loanId, RepaymentRequest request);

    // Admin operations
    List<LoanResponse> getAllPendingLoans();
    LoanResponse approveLoan(Long loanId, LoanApprovalRequest request);
    LoanResponse rejectLoan(Long loanId, String reason);
    List<LoanResponse> getAllLoans();
}
