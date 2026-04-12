package com.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String loanNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "disbursed_account_id")
    private Account disbursedToAccount;

    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal interestRate;
    private Integer tenureInMonths;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.PENDING;

    private String purpose;

    private LocalDate applicationDate = LocalDate.now();
    private LocalDate approvalDate;
    private LocalDate disbursementDate;

    private BigDecimal emiAmount;
    private BigDecimal remainingBalance;
    private Integer remainingMonths;

    private String rejectionReason;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoanRepaymentHistory> repaymentHistory = new ArrayList<>();

    public Long getId() { 
    	return id; 
    	}
    public void setId(Long id) { 
    	this.id = id; 
    	}
    public String getLoanNumber() { 
    	return loanNumber; 
    	}
    public void setLoanNumber(String loanNumber) { 
    	this.loanNumber = loanNumber; 
    	}
    public User getUser() { 
    	return user; 
    	}
    public void setUser(User user) { 
    	this.user = user; 
    	}
    public Account getDisbursedToAccount() { 
    	return disbursedToAccount; 
    	}
    public void setDisbursedToAccount(Account disbursedToAccount) { 
    	this.disbursedToAccount = disbursedToAccount; 
    	}
    public BigDecimal getRequestedAmount() { 
    	return requestedAmount; 
    	}
    public void setRequestedAmount(BigDecimal requestedAmount) { 
    	this.requestedAmount = requestedAmount; 
    	}
    public BigDecimal getApprovedAmount() { 
    	return approvedAmount; 
    	}
    public void setApprovedAmount(BigDecimal approvedAmount) { 
    	this.approvedAmount = approvedAmount; 
    	}
    public BigDecimal getInterestRate() { 
    	return interestRate; 
    	}
    public void setInterestRate(BigDecimal interestRate) { 
    	this.interestRate = interestRate; 
    	}
    public Integer getTenureInMonths() { 
    	return tenureInMonths; 
    	}
    public void setTenureInMonths(Integer tenureInMonths) { 
    	this.tenureInMonths = tenureInMonths; 
    	}
    public LoanStatus getStatus() { 
    	return status; 
    	}
    public void setStatus(LoanStatus status) { 
    	this.status = status; 
    	}
    public String getPurpose() { 
    	return purpose; 
    	}
    public void setPurpose(String purpose) { 
    	this.purpose = purpose; 
    	}
    public LocalDate getApplicationDate() { 
    	return applicationDate; 
    	}
    public void setApplicationDate(LocalDate applicationDate) { 
    	this.applicationDate = applicationDate; 
    	}
    public LocalDate getApprovalDate() { 
    	return approvalDate; 
    	}
    public void setApprovalDate(LocalDate approvalDate) { 
    	this.approvalDate = approvalDate; 
    	}
    public LocalDate getDisbursementDate() { 
    	return disbursementDate; 
    	}
    public void setDisbursementDate(LocalDate disbursementDate) { 
    	this.disbursementDate = disbursementDate; 
    	}
    public BigDecimal getEmiAmount() { 
    	return emiAmount; 
    	}
    public void setEmiAmount(BigDecimal emiAmount) { 
    	this.emiAmount = emiAmount; 
    	}
    public BigDecimal getRemainingBalance() { 
    	return remainingBalance; 
    	}
    public void setRemainingBalance(BigDecimal remainingBalance) { 
    	this.remainingBalance = remainingBalance; 
    	}
    public Integer getRemainingMonths() { 
    	return remainingMonths; 
    	}
    public void setRemainingMonths(Integer remainingMonths) { 
    	this.remainingMonths = remainingMonths; 
    	}
    public String getRejectionReason() { 
    	return rejectionReason; 
    	}
    public void setRejectionReason(String rejectionReason) { 
    	this.rejectionReason = rejectionReason; 
    	}
    public LocalDateTime getCreatedAt() { 
    	return createdAt; 
    	}
    public void setCreatedAt(LocalDateTime createdAt) { 
    	this.createdAt = createdAt; 
    	}
    public List<LoanRepaymentHistory> getRepaymentHistory() { 
    	return repaymentHistory; 
    	}
    public void setRepaymentHistory(List<LoanRepaymentHistory> repaymentHistory) { 
    	this.repaymentHistory = repaymentHistory; 
    	}
}