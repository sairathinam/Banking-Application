package com.DTO;

import java.util.List;

public class LoginResponse {
    private String token;
    private String fullName;
    private String email;
    private String message;
    private List<AccountSummary> accounts;  
    private List<LoanResponse> loans;
    private String role;

  
    public LoginResponse() {}

    public LoginResponse(String token, String fullName, String email, String message, String role) {
        this.token = token;
        this.fullName = fullName;
        this.email = email;
        this.message = message;
        this.role = role;
    }

   
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<AccountSummary> getAccounts() { return accounts; }
    public void setAccounts(List<AccountSummary> accounts) { this.accounts = accounts; }

    public List<LoanResponse> getLoans() { return loans; }
    public void setLoans(List<LoanResponse> loans) { this.loans = loans; }

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}