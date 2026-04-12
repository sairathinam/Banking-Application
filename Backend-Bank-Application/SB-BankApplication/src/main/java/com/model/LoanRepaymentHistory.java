package com.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_repayment_history")
public class LoanRepaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "loan_id", nullable = false)
    
    private Loan loan;

    private BigDecimal amountPaid;
    private BigDecimal balanceAfterPayment;
    private Integer monthsRemainingAfterPayment;
    private LocalDateTime paidAt = LocalDateTime.now();
    private String remarks;
    private BigDecimal interestPaid;
    private BigDecimal principalPaid;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Loan getLoan() { return loan; }
    public void setLoan(Loan loan) { this.loan = loan; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public BigDecimal getBalanceAfterPayment() { return balanceAfterPayment; }
    public void setBalanceAfterPayment(BigDecimal balanceAfterPayment) { this.balanceAfterPayment = balanceAfterPayment; }
    public Integer getMonthsRemainingAfterPayment() { return monthsRemainingAfterPayment; }
    public void setMonthsRemainingAfterPayment(Integer m) { this.monthsRemainingAfterPayment = m; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
	public BigDecimal getInterestPaid() {
		return interestPaid;
	}
	public void setInterestPaid(BigDecimal interestPaid) {
		this.interestPaid = interestPaid;
	}
	public BigDecimal getPrincipalPaid() {
		return principalPaid;
	}
	public void setPrincipalPaid(BigDecimal principalPaid) {
		this.principalPaid = principalPaid;
	}
}

