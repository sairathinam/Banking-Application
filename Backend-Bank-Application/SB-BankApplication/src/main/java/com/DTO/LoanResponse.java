
package com.DTO;

import com.model.LoanRepaymentHistory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class LoanResponse {
    private Long loanId;
    private String loanNumber;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal interestRate;
    private Integer tenureInMonths;
    private Integer remainingMonths;
    private BigDecimal remainingBalance;
    private BigDecimal emiAmount;
    private String status;
    private LocalDate applicationDate;
    private LocalDate approvalDate;
    private LocalDate disbursementDate;
    private String purpose;
    private String message;
    private List<LoanRepaymentHistory> repaymentHistory;

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }
    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public Integer getTenureInMonths() { return tenureInMonths; }
    public void setTenureInMonths(Integer tenureInMonths) { this.tenureInMonths = tenureInMonths; }
    public Integer getRemainingMonths() { return remainingMonths; }
    public void setRemainingMonths(Integer remainingMonths) { this.remainingMonths = remainingMonths; }
    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }
    public BigDecimal getEmiAmount() { return emiAmount; }
    public void setEmiAmount(BigDecimal emiAmount) { this.emiAmount = emiAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }
    public LocalDate getDisbursementDate() { return disbursementDate; }
    public void setDisbursementDate(LocalDate disbursementDate) { this.disbursementDate = disbursementDate; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<LoanRepaymentHistory> getRepaymentHistory() { return repaymentHistory; }
    public void setRepaymentHistory(List<LoanRepaymentHistory> repaymentHistory) { this.repaymentHistory = repaymentHistory; }
}
