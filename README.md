# 🏦 NeoBank –  Banking Application

A full-stack digital banking  application with a **Spring Boot REST API** backend and a **plain HTML/CSS/JS** frontend. The app supports user banking operations (deposit, withdraw, loans, transaction history) and a separate **Admin Panel** for managing users, accounts, and loan approvals.

---

## 📸 Features

### 👤 User Portal (`index1.html`)
- Register & Login with JWT-based authentication
- View account balance and account number on Dashboard
- Deposit & Withdraw money
- View full Transaction History
- Apply for a Loan (Personal, Education, Home)
- View My Loans – track status, EMI amount, tenure, interest rate
- Pay EMI on active loans
- View Loan Repayment History (principal paid, interest paid, balance remaining)

### 🛠️ Admin Panel (`admin1.html`)
- Admin Login (separate from user login)
- Dashboard with stats: Total Users, Total Accounts, Total Loans, Total Transactions
- View all Users and their details
- View all Accounts and Transactions per account
- View all Loan applications
- Approve loans (set amount, interest rate, tenure)
- Reject loans (with reason)

---

## 🧰 Tech Stack

### Backend
| Layer | Technology |
|---|---|
| Framework | Java , Spring Boot 2.7.14 |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL  (`banking_db`) |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| Packaging | Maven (WAR) |
| Other |  Spring Validation |


### Frontend
| Layer | Technology |
|---|---|
| Pages | Plain HTML5 |
| Styling |  CSS  |
| Logic | Vanilla JavaScript (ES6+, `fetch` API) |
| Auth Storage | `localStorage` (JWT token) |

---

## 🗄️ Database Structure

> Database name: `banking_db`  
> Tables are auto-created by Hibernate (`ddl-auto=update`)

### `users`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK) | Auto-increment |
| email | VARCHAR | Unique, not null |
| password | VARCHAR | BCrypt hashed |
| full_name | VARCHAR | |
| phone_number | VARCHAR | |
| gender | VARCHAR | |
| address | VARCHAR | |
| role | VARCHAR | `USER` or `ADMIN` |

### `accounts`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK) | Auto-increment |
| account_number | VARCHAR | Unique |
| account_type | VARCHAR | e.g., SAVINGS |
| balance | DECIMAL | Default 0 |
| user_id | BIGINT (FK) | → users.id |

### `transactions`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK) | Auto-increment |
| type | VARCHAR | `DEPOSIT` / `WITHDRAW` |
| amount | DECIMAL | |
| description | VARCHAR | |
| created_at | DATETIME | |
| account_id | BIGINT (FK) | → accounts.id |

### `loans`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK) | Auto-increment |
| loan_number | VARCHAR | Unique |
| user_id | BIGINT (FK) | → users.id |
| disbursed_account_id | BIGINT (FK) | → accounts.id |
| requested_amount | DECIMAL | |
| approved_amount | DECIMAL | |
| interest_rate | DECIMAL | |
| tenure_in_months | INT | |
| status | ENUM | `PENDING`, `ACTIVE`, `REJECTED`, `CLOSED` |
| purpose | VARCHAR | |
| emi_amount | DECIMAL | |
| remaining_balance | DECIMAL | |
| remaining_months | INT | |
| rejection_reason | VARCHAR | |
| application_date | DATE | |
| approval_date | DATE | |
| disbursement_date | DATE | |
| created_at | DATETIME | |

### `loan_repayment_history`
| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK) | Auto-increment |
| loan_id | BIGINT (FK) | → loans.id |
| amount_paid | DECIMAL | |
| principal_paid | DECIMAL | |
| interest_paid | DECIMAL | |
| balance_after_payment | DECIMAL | |
| months_remaining_after_payment | INT | |
| paid_at | DATETIME | |
| remarks | VARCHAR | |

---

## 🚀 How to Run

### Prerequisites
- Java 
- Maven
- MySQL Server running locally
- A browser (Chrome recommended)

### Step 1 – Set up the Database
```sql
CREATE DATABASE banking_db;
```
Hibernate will auto-create all tables on first run.

### Step 2 – Configure Database Credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_db?useSSL=false
spring.datasource.username=root
spring.datasource.password=root   # change to your MySQL password
```

### Step 3 – Run the Backend
```bash
cd SB-BankApplication
./mvnw spring-boot:run
```
The API will start at: **`http://localhost:4067`**

### Step 4 – Open the Frontend
Open either HTML file directly in your browser:
- **User Portal:** Open `index1.html`
- **Admin Panel:** Open `admin1.html`

> No web server needed — the frontend communicates with the backend via `fetch()` calls to `http://localhost:4067/api`.

---

## 🔐 API Endpoints (Summary)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT |
| GET | `/api/accounts/dashboard` | Get user's account & balance |
| POST | `/api/accounts/{accNo}/deposit` | Deposit money |
| POST | `/api/accounts/{accNo}/withdraw` | Withdraw money |
| GET | `/api/accounts/{accNo}/transactions` | Get transaction history |
| POST | `/api/loans/apply` | Apply for a loan |
| GET | `/api/loans` | Get user's loans |
| POST | `/api/loans/{id}/repay` | Pay EMI |
| GET | `/api/loans/{id}/history` | Get repayment history |
| GET | `/api/admin/stats` | Admin dashboard stats |
| GET | `/api/admin/users` | All users |
| GET | `/api/admin/accounts` | All accounts |
| GET | `/api/admin/loans` | All loans |
| POST | `/api/admin/loans/{id}/approve` | Approve a loan |
| POST | `/api/admin/loans/{id}/reject` | Reject a loan |

---

## 📁 Project Structure

```
SB-BankApplication/
├── src/main/java/com/
│   ├── Controller/         # REST Controllers (Account, Loan, User, Admin)
│   ├── DTO/                # Request/Response DTOs
│   ├── model/              # JPA Entities (User, Account, Loan, Transaction...)
│   ├── Repo/               # Spring Data JPA Repositories
│   ├── service/            # Business Logic
│   ├── security/           # JWT Filter, SecurityConfig, CorsConfig
│   └── SbBankApplication.java
└── src/main/resources/
    └── application.properties

Frontend/
├── index1.html             # User Portal
├── admin1.html             # Admin Panel
└── js/
    ├── config.js           # API base URL
    ├── auth.js             # Login / Register
    ├── user.js             # Dashboard, Transactions, Loans
    ├── admin.js            # Admin Panel logic
    └── helpers.js          # Shared utilities, headers, logout

## 📸 Project Screenshots

### 👤 User
![User login]<img width="1363" height="681" alt="user login" src="https://github.com/user-attachments/assets/2b821ca4-e3a5-4eab-a5ea-00f85f3e07be" />
![User Registration]<img width="1341" height="664" alt="user registration" src="https://github.com/user-attachments/assets/349316dd-a47a-483d-b113-ad3e7f0f3e67" />
![User Dashboard]<img width="1322" height="675" alt="user dashboard" src="https://github.com/user-attachments/assets/57ed51dd-8d47-408f-b762-477a6c93ff73" />
![Loan Application]<img width="1342" height="672" alt="user loanapplication" src="https://github.com/user-attachments/assets/06a52ad5-b48b-4a0e-b000-caab48163fb2" />
![Loan Management]<img width="816" height="682" alt="user loanmanagement" src="https://github.com/user-attachments/assets/544dea30-f3b0-499c-95bc-157c2dd58663" />
### 👤 admin
![admin login]
<img width="1352" height="669" alt="admin login" src="https://github.com/user-attachments/assets/cff41c6f-d31b-44ee-9a44-b3a0bd15d735" />
![admin Dashboard]
<img width="1346" height="674" alt="admin dashboard" src="https://github.com/user-attachments/assets/675e0796-3433-4d96-ac7f-2ea06fe300d4" />
![loan management]
<img width="1348" height="682" alt="admin loans" src="https://github.com/user-attachments/assets/438fa2ad-878f-4251-a14d-e6b73a33910d" />
<img width="1339" height="669" alt="admin loanApproval" src="https://github.com/user-attachments/assets/7396b20d-60e8-4616-8f19-6a401082761f" />
<img width="1340" height="661" alt="admin loan details" src="https://github.com/user-attachments/assets/a0e14b71-32b3-40ae-8030-2a00c05cfa9f" />
![Accounts management]
<img width="1307" height="664" alt="admin accounts" src="https://github.com/user-attachments/assets/836ce839-6540-4d27-92be-fb9ac1d0595f" />
<img width="1330" height="648" alt="admin usertransactions" src="https://github.com/user-attachments/assets/5cade32e-eb0c-49cd-b8d1-2124237ec5bd" />
![User management]
<img width="1208" height="550" alt="admin user" src="https://github.com/user-attachments/assets/ef3d89eb-7ed8-4494-9063-3d44b2258078" />



---

