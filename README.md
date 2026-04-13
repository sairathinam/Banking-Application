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
```

---

