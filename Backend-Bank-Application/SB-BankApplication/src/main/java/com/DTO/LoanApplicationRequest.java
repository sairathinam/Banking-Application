package com.DTO;

import java.math.BigDecimal;

public class LoanApplicationRequest {
	private BigDecimal requestedAmount;
    private Integer tenureInMonths;      
    private String purpose;             
    private String accountNumber;
	public BigDecimal getRequestedAmount() {
		return requestedAmount;
	}
	public void setRequestedAmount(BigDecimal requestedAmount) {
		this.requestedAmount = requestedAmount;
	}
	public Integer getTenureInMonths() {
		return tenureInMonths;
	}
	public void setTenureInMonths(Integer tenureInMonths) {
		this.tenureInMonths = tenureInMonths;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

}
