package com.DTO;

import java.math.BigDecimal;

public class AccountSummary {
	private Long accountId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String fullName;
    

    public AccountSummary(Long accountId, String accountNumber, String accountType, BigDecimal balance, String fullName) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.fullName = fullName;
    }

    public String getFullName() {
		return fullName;
    }

	// Getters and Setters
    public Long getAccountId() { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
	

}
