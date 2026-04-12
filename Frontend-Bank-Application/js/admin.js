async function adminLogin() {
      const email = document.getElementById('adminEmail').value.trim();
      const password = document.getElementById('adminPassword').value;

      if (!email || !password) return alert("Please enter email and password");

      try {
        const res = await fetch(`${API_BASE}/auth/login`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, password })
        });

        const data = await res.json();

        if (res.ok && data.token) {
          const isAdmin = data.email === "admin@gmail.com" || 
                          (data.role && data.role.toUpperCase() === "ADMIN");

          if (!isAdmin) {
            return alert("Access Denied! This account does not have Admin privileges.");
          }

          currentToken = data.token;
          localStorage.setItem("adminToken", currentToken);

          document.getElementById('adminName').textContent = data.fullName || "Administrator";

          document.getElementById('adminLoginScreen').classList.add('hidden');
          document.getElementById('adminMain').classList.remove('hidden');

          loadAdminDashboard();
          showPage('adminDashboard');
        } else {
          alert(data.message || "Invalid credentials");
        }
      } catch (err) {
        console.error(err);
        alert("Login failed. Check if backend is running.");
      }
    }

    function showPage(page) {
      // Hide all pages
      document.querySelectorAll('#adminDashboard, #users, #loans, #accounts').forEach(el => {
        el.classList.add('hidden');
      });

      const activePage = document.getElementById(page);
      if (activePage) activePage.classList.remove('hidden');

     
      const titles = {
        'adminDashboard': 'Admin Dashboard',
        'users': 'All Users',
        'loans': 'All Loans',
        'accounts': 'All Accounts'
      };
      document.getElementById('pageTitle').textContent = titles[page] || 'Admin Panel';
    }

    async function loadAdminDashboard() {
      try {
        const res = await fetch(`${API_BASE}/admin/dashboard`, { headers: getHeaders() });
        const data = await res.json();

        document.getElementById('totalUsers').textContent = data.totalUsers || 0;
        document.getElementById('totalAccounts').textContent = data.totalAccounts || 0;
        document.getElementById('totalLoans').textContent = data.totalLoans || 0;
        document.getElementById('totalTransactions').textContent = data.totalTransactions || 0;

      } catch (err) {
        console.error("Dashboard load failed:", err);
      }
    }

    
    async function loadAllUsers() {
      showPage('users');
      
      try {
        const res = await fetch(`${API_BASE}/admin/users`, { headers: getHeaders() });
        const users = await res.json();

        const tbody = document.getElementById('usersTableBody');
        tbody.innerHTML = '';

        if (users.length === 0) {
          tbody.innerHTML = `<tr><td colspan="7" class="p-8 text-center text-zinc-400">No users found</td></tr>`;
          return;
        }

        users.forEach(user => {
          const tr = document.createElement('tr');
          tr.className = "hover:bg-zinc-800 cursor-pointer";
          
          tr.innerHTML = `
            <td class="p-5 font-mono text-sm">${user.id}</td>
            <td class="p-5">${user.fullName || '<span class="text-zinc-500">Not Provided</span>'}</td>
            <td class="p-5">${user.email}</td>
            <td class="p-5">${user.phoneNumber || '<span class="text-zinc-500">Not Provided</span>'}</td>
            <td class="p-5">${user.gender || '<span class="text-zinc-500">Not Provided</span>'}</td>
            <td class="p-5">${user.address || '<span class="text-zinc-500">Not Provided</span>'}</td>
            <td class="p-5">
              <span class="px-4 py-1 rounded-full text-xs font-medium 
                ${user.role === 'ADMIN' ? 'bg-red-900 text-red-400' : 'bg-emerald-900 text-emerald-400'}">
                ${user.role || 'USER'}
              </span>
            </td>
          `;

          tr.onclick = () => showUserDetails(user);
          tbody.appendChild(tr);
        });

      } catch (err) {
        console.error("Users Load Error:", err);
        document.getElementById('usersTableBody').innerHTML = 
          `<tr><td colspan="7" class="p-8 text-center text-red-400">Failed to load users</td></tr>`;
      }
    }

    let currentUserId = null;

async function showUserDetails(user) {
  currentUserId = user.id;
  
  const modal = document.getElementById('userDetailsModal');
  modal.classList.remove('hidden');
  modal.classList.add('flex');

 
  document.getElementById('modalUserName').textContent = user.fullName || "User";
  document.getElementById('modalUserEmail').textContent = user.email;

  document.getElementById('userInfoContent').innerHTML = `
    <div class="flex justify-between"><span class="text-zinc-400">User ID</span><span class="font-mono">${user.id}</span></div>
    <div class="flex justify-between"><span class="text-zinc-400">Phone</span><span>${user.phoneNumber || 'Not Provided'}</span></div>
    <div class="flex justify-between"><span class="text-zinc-400">Gender</span><span>${user.gender || 'Not Provided'}</span></div>
    <div class="flex justify-between"><span class="text-zinc-400">Address</span><span>${user.address || 'Not Provided'}</span></div>
    <div class="flex justify-between"><span class="text-zinc-400">Role</span><span class="capitalize">${user.role || 'USER'}</span></div>
  `;

  
  await Promise.all([
    loadUserAccounts(user.id),
    loadUserLoans(user.id),
    loadUserTransactions(user.id)
  ]);
}


async function loadUserAccounts(userId) {
  const container = document.getElementById('userAccountsList');
  container.innerHTML = '<p class="text-zinc-400">Loading accounts...</p>';

  try {
    const res = await fetch(`${API_BASE}/admin/users/${userId}/accounts`, { headers: getHeaders() });
    const accounts = await res.json();

    container.innerHTML = '';

    if (accounts.length === 0) {
      container.innerHTML = `<p class="text-zinc-400">No accounts found</p>`;
      return;
    }

    accounts.forEach(acc => {
      const div = document.createElement('div');
      div.className = "bg-zinc-800 p-5 rounded-2xl";
      div.innerHTML = `
        <p class="font-mono text-sm">${acc.accountNumber}</p>
        <p class="text-xl font-semibold mt-1">${acc.accountType}</p>
        <p class="text-emerald-400 text-2xl mt-3">₹${Number(acc.balance).toLocaleString('en-IN')}</p>
      `;
      container.appendChild(div);
    });
  } catch (e) {
    container.innerHTML = `<p class="text-red-400">Failed to load accounts</p>`;
  }
}


async function loadUserLoans(userId) {
  const container = document.getElementById('userLoansList');
  container.innerHTML = '<p class="text-zinc-400">Loading loans...</p>';

  try {
    const res = await fetch(`${API_BASE}/admin/users/${userId}/loans`, { headers: getHeaders() });
    const loans = await res.json();

    container.innerHTML = '';

    if (loans.length === 0) {
      container.innerHTML = `<p class="text-zinc-400">No loans found</p>`;
      return;
    }

    loans.forEach(loan => {
      const div = document.createElement('div');
      div.className = "bg-zinc-800 p-5 rounded-2xl";
      div.innerHTML = `
        <p class="text-sm text-zinc-400">${loan.loanNumber || `#${loan.loanId}`}</p>
        <p class="text-xl font-semibold mt-1">₹${Number(loan.requestedAmount || loan.approvedAmount || 0).toLocaleString('en-IN')}</p>
        <span class="inline-block mt-3 px-4 py-1 rounded-full text-xs ${loan.status === 'APPROVED' ? 'bg-emerald-900 text-emerald-400' : 'bg-amber-900 text-amber-400'}">
          ${loan.status}
        </span>
      `;
      container.appendChild(div);
    });
  } catch (e) {
    container.innerHTML = `<p class="text-red-400">Failed to load loans</p>`;
  }
}

async function loadUserTransactions(userId) {
  const tbody = document.getElementById('userTransactionsBody');
  tbody.innerHTML = `<tr><td colspan="5" class="p-8 text-center">Loading transactions...</td></tr>`;

  try {
    const res = await fetch(`${API_BASE}/admin/users/${userId}/transactions`, { headers: getHeaders() });
    const transactions = await res.json();

    tbody.innerHTML = '';

    if (transactions.length === 0) {
      tbody.innerHTML = `<tr><td colspan="5" class="p-8 text-center text-zinc-400">No transactions found</td></tr>`;
      return;
    }

    transactions.forEach(tx => {
      const row = document.createElement('tr');
      const isPositive = tx.type === 'DEPOSIT' || tx.type === 'LOAN_DISBURSEMENT';
      
      row.innerHTML = `
        <td class="p-4 text-zinc-400">${new Date(tx.createdAt).toLocaleDateString('en-IN')}</td>
        <td class="p-4">
          <span class="px-4 py-1 rounded-full text-xs ${isPositive ? 'bg-emerald-900 text-emerald-400' : 'bg-red-900 text-red-400'}">
            ${tx.type}
          </span>
        </td>
        
        <td class="p-4">${tx.description || ''}</td>
        <td class="p-4 text-right font-semibold ${isPositive ? 'text-emerald-400' : 'text-red-400'}">
          ${isPositive ? '+' : '-'} ₹${Number(tx.amount).toLocaleString('en-IN')}
        </td>
      `;
      tbody.appendChild(row);
    });
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="5" class="p-8 text-center text-red-400">Failed to load transactions</td></tr>`;
  }
}

function closeUserModal() {
  const modal = document.getElementById('userDetailsModal');
  modal.classList.add('hidden');
  modal.classList.remove('flex');
}

async function loadAllLoans() {
  showPage('loans');
  
  try {
    const res = await fetch(`${API_BASE}/admin/loans`, { headers: getHeaders() });
    const loans = await res.json();

    console.log("=== LOANS JSON ===", loans);   // For debugging

    const container = document.getElementById('loansList');
    container.innerHTML = '';

    if (!loans || loans.length === 0) {
      container.innerHTML = `<p class="text-zinc-400 text-center py-20 text-xl">No loans found</p>`;
      return;
    }

    loans.forEach(loan => {
      const card = document.createElement('div');
      card.className = "bg-zinc-900 rounded-3xl p-8 border border-transparent hover:border-amber-500 transition-all cursor-pointer";

      const status = loan.status || 'PENDING';
      const statusClass = status === 'APPROVED' ? 'bg-emerald-900 text-emerald-400' : 
                          status === 'REJECTED' ? 'bg-red-900 text-red-400' : 
                          'bg-amber-900 text-amber-400';

      const amount = loan.approvedAmount || loan.requestedAmount || 0;

      card.innerHTML = `
        <div class="flex justify-between items-start">
          <div>
            <p class="text-sm text-zinc-400">${loan.loanNumber || `Loan #${loan.loanId}`}</p>
            <p class="text-4xl font-bold mt-2">₹${Number(amount).toLocaleString('en-IN')}</p>
          </div>
          <span class="px-6 py-2 rounded-2xl text-sm font-medium ${statusClass}">
            ${status}
          </span>
        </div>

        <div class="mt-6 grid grid-cols-2 gap-y-4 text-sm">
          <div><span class="text-zinc-400">Purpose:</span> ${loan.purpose || 'Not Specified'}</div>
          <div><span class="text-zinc-400">Interest Rate:</span> ${loan.interestRate || 0}%</div>
          <div><span class="text-zinc-400">Tenure:</span> ${loan.tenureInMonths || 'N/A'} months</div>
          <div><span class="text-zinc-400">EMI:</span> ₹${Number(loan.emiAmount || 0).toLocaleString('en-IN')}</div>
          ${loan.remainingBalance ? `
          <div><span class="text-zinc-400">Remaining:</span> ₹${Number(loan.remainingBalance).toLocaleString('en-IN')}</div>` : ''}
        </div>

        <button onclick="showLoanDetails(${loan.loanId})" 
          class="mt-8 w-full py-4 bg-zinc-800 hover:bg-zinc-700 rounded-2xl text-sm font-medium transition">
          View Full Details
        </button>
      `;

      container.appendChild(card);
    });

  } catch (err) {
    console.error(err);
    document.getElementById('loansList').innerHTML = `<p class="text-red-400 text-center py-12">Failed to load loans</p>`;
  }
}

   
let currentLoanId = null;

function approveLoan(loanId) {
  currentLoanId = loanId;
  document.getElementById('approvalModalTitle').textContent = `Approve Loan #${loanId}`;
  
  document.getElementById('approveAmount').value = "";
  document.getElementById('approveInterest').value = "12.5";
  document.getElementById('approveTenure').value = "24";

  const modal = document.getElementById('approvalModal');
  modal.classList.remove('hidden');
  modal.classList.add('flex');
}

async function submitApproval() {
  const approvedAmount = document.getElementById('approveAmount').value;
  const interestRate = document.getElementById('approveInterest').value;
  const tenureInMonths = document.getElementById('approveTenure').value;

  if (!approvedAmount || !interestRate || !tenureInMonths) {
    showToast("Please fill all fields", "error");
    return;
  }

  try {
    const requestBody = {
      approvedAmount: parseFloat(approvedAmount),
      interestRate: parseFloat(interestRate),
      tenureInMonths: parseInt(tenureInMonths)
    };

    const res = await fetch(`${API_BASE}/admin/loans/${currentLoanId}/approve`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify(requestBody)
    });

    if (res.ok) {
      closeApprovalModal();
      showToast("✅ Loan Approved Successfully!", "success");
      loadAllLoans();
    } else {
      const data = await res.json().catch(() => ({}));
      showToast("❌ " + (data.message || "Approval failed"), "error");
    }
  } catch (err) {
    console.error(err);
    showToast("Failed to approve loan", "error");
  }
}

function closeApprovalModal() {
  const modal = document.getElementById('approvalModal');
  modal.classList.add('hidden');
  modal.classList.remove('flex');
}

function rejectLoan(loanId) {
  currentLoanId = loanId;
  document.getElementById('rejectModalTitle').textContent = `Reject Loan #${loanId}`;
  document.getElementById('rejectReason').value = "";

  const modal = document.getElementById('rejectModal');
  modal.classList.remove('hidden');
  modal.classList.add('flex');
}

async function submitReject() {
  const reason = document.getElementById('rejectReason').value.trim();

  if (!reason) {
    showToast("Please enter rejection reason", "error");
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/admin/loans/${currentLoanId}/reject`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify({ reason: reason })
    });

    if (res.ok) {
      closeRejectModal();
      showToast("❌ Loan Rejected Successfully", "success");
      loadAllLoans();
    } else {
      const data = await res.json().catch(() => ({}));
      showToast("❌ " + (data.message || "Rejection failed"), "error");
    }
  } catch (err) {
    console.error(err);
    showToast("Failed to reject loan", "error");
  }
}

function closeRejectModal() {
  const modal = document.getElementById('rejectModal');
  modal.classList.add('hidden');
  modal.classList.remove('flex');
}
    
async function loadAllAccounts() {
  showPage('accounts');
  
  try {
    const res = await fetch(`${API_BASE}/admin/accounts`, { headers: getHeaders() });
    const accounts = await res.json();

    const tbody = document.getElementById('accountsTableBody');
    tbody.innerHTML = '';

    if (accounts.length === 0) {
      tbody.innerHTML = `<tr><td colspan="5" class="p-8 text-center text-zinc-400">No accounts found</td></tr>`;
      return;
    }

    accounts.forEach(acc => {
      const userName = acc.user?.fullName || acc.user?.email || acc.userId || 'N/A';

      const tr = document.createElement('tr');
      tr.className = "hover:bg-zinc-800";
      tr.innerHTML = `
        <td class="p-5 font-mono">${acc.accountNumber || 'N/A'}</td>
        <td class="p-5">${acc.accountType || 'SAVINGS'}</td>
        <td class="p-5">${acc.id}</td>
        <td class="p-5 text-right font-semibold text-emerald-400">
          ₹${Number(acc.balance || 0).toLocaleString('en-IN')}
        </td>
        <td class="p-5">
          <button onclick="showTransactions(${acc.id}, '${acc.accountNumber}', '${acc.accountType}')" 
            class="px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 rounded-2xl text-sm font-medium">
            View Transactions (${acc.transactions ? acc.transactions.length : 0})
          </button>
        </td>
      `;
      tbody.appendChild(tr);
    });

  } catch (err) {
    console.error("Accounts Load Error:", err);
  }
}

async function showTransactions(accountId, accountNumber, accountType) {
  const modal = document.getElementById('transactionModal');
  const tbody = document.getElementById('transactionTableBody');

  document.getElementById('modalAccountNumber').textContent = `Account: ${accountNumber}`;
  document.getElementById('modalAccountType').textContent = accountType;

  tbody.innerHTML = '<tr><td colspan="4" class="p-8 text-center">Loading transactions...</td></tr>';

  modal.classList.remove('hidden');
  modal.classList.add('flex');

  try {
    
    const res = await fetch(`${API_BASE}/admin/accounts`, { headers: getHeaders() });
    const accounts = await res.json();
    
    const account = accounts.find(a => a.id === accountId);

    if (!account || !account.transactions || account.transactions.length === 0) {
      tbody.innerHTML = `<tr><td colspan="4" class="p-8 text-center text-zinc-400">No transactions found</td></tr>`;
      return;
    }

    tbody.innerHTML = '';

    account.transactions.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)); // Latest first

    account.transactions.forEach(tx => {
      const row = document.createElement('tr');
      const isPositive = tx.type === 'DEPOSIT' || tx.type === 'LOAN_DISBURSEMENT';
      
      row.innerHTML = `
        <td class="p-4 text-zinc-400">${new Date(tx.createdAt).toLocaleDateString('en-IN')}</td>
        <td class="p-4">
          <span class="px-4 py-1 rounded-full text-xs font-medium 
            ${isPositive ? 'bg-emerald-900 text-emerald-400' : 'bg-red-900 text-red-400'}">
            ${tx.type}
          </span>
        </td>
        <td class="p-4">${tx.description || 'No description'}</td>
        <td class="p-4 text-right font-semibold ${isPositive ? 'text-emerald-400' : 'text-red-400'}">
          ${isPositive ? '+' : '-'} ₹${Number(tx.amount).toLocaleString('en-IN')}
        </td>
      `;
      tbody.appendChild(row);
    });

  } catch (err) {
    console.error(err);
    tbody.innerHTML = `<tr><td colspan="4" class="p-8 text-center text-red-400">Failed to load transactions</td></tr>`;
  }
}


function closeTransactionModal() {
  const modal = document.getElementById('transactionModal');
  modal.classList.add('hidden');
  modal.classList.remove('flex');
}


function viewAccountTransactions(accountNumber, accountId) {
  

  fetch(`${API_BASE}/admin/accounts`, { headers: getHeaders() })
    .then(res => res.json())
    .then(accounts => {
      const account = accounts.find(a => a.accountNumber === accountNumber);

      if (!account || !account.transactions || account.transactions.length === 0) {
        alert("No transactions found for this account.");
        return;
      }

      let message = `Transactions for Account: ${accountNumber}\n\n`;
      
      account.transactions.forEach((tx, i) => {
        const sign = (tx.type === 'DEPOSIT' || tx.type === 'LOAN_DISBURSEMENT') ? '+' : '-';
        message += `${i+1}. ${tx.type} | ${sign}₹${Number(tx.amount).toLocaleString('en-IN')} | ${tx.description || ''}\n`;
        if (tx.createdAt) message += `   Date: ${new Date(tx.createdAt).toLocaleString()}\n\n`;
      });

      alert(message);
    })
    .catch(err => {
      console.error(err);
      alert("Failed to load transactions.");
    });
}
   function viewAccountTransactions(accountNumber, accountId) {
  

  fetch(`${API_BASE}/admin/accounts`, { headers: getHeaders() })
    .then(res => res.json())
    .then(accounts => {
      const account = accounts.find(a => a.accountNumber === accountNumber);

      if (!account || !account.transactions || account.transactions.length === 0) {
        alert("No transactions found for this account.");
        return;
      }

      let message = `Transactions for Account: ${accountNumber}\n\n`;
      
      account.transactions.forEach((tx, i) => {
        const sign = (tx.type === 'DEPOSIT' || tx.type === 'LOAN_DISBURSEMENT') ? '+' : '-';
        message += `${i+1}. ${tx.type} | ${sign}₹${Number(tx.amount).toLocaleString('en-IN')} | ${tx.description || ''}\n`;
        if (tx.createdAt) message += `   Date: ${new Date(tx.createdAt).toLocaleString()}\n\n`;
      });

      alert(message);
    })
    .catch(err => {
      console.error(err);
      alert("Failed to load transactions.");
    });
}
    function adminLogout() {
      if (confirm("Logout from Admin Panel?")) {
        localStorage.clear();
        window.location.href = "index.html";
      }
    }

    
    window.onload = () => {
      currentToken = localStorage.getItem("adminToken");
      if (currentToken) {
        document.getElementById('adminLoginScreen').classList.add('hidden');
        document.getElementById('adminMain').classList.remove('hidden');
        loadAdminDashboard();
        showPage('adminDashboard');
      }
    };
    async function showLoanDetails(loanId) {
  const modal = document.getElementById('loanModal');
  const content = document.getElementById('loanModalContent');
  const actions = document.getElementById('loanModalActions');

  content.innerHTML = `<p class="text-center py-12 text-zinc-400">Loading loan details...</p>`;
  modal.classList.remove('hidden');
  modal.classList.add('flex');

  try {
    const res = await fetch(`${API_BASE}/admin/loans`, { headers: getHeaders() });
    const loans = await res.json();
    const loan = loans.find(l => l.loanId === loanId);

    if (!loan) {
      content.innerHTML = `<p class="text-red-400">Loan not found</p>`;
      return;
    }

    const amount = loan.approvedAmount || loan.requestedAmount || 0;
    const isPending = loan.status === 'PENDING';

    content.innerHTML = `
      <div class="space-y-6">
        <div class="flex justify-between items-center">
          <div>
            <p class="text-zinc-400">Loan Number</p>
            <p class="text-2xl font-mono font-bold">${loan.loanNumber || `#${loan.loanId}`}</p>
          </div>
          <span class="px-6 py-2 rounded-2xl text-lg font-medium
            ${loan.status === 'APPROVED' ? 'bg-emerald-900 text-emerald-400' : 
              loan.status === 'REJECTED' ? 'bg-red-900 text-red-400' : 'bg-amber-900 text-amber-400'}">
            ${loan.status}
          </span>
        </div>

        <div class="grid grid-cols-2 gap-6 text-sm">
          <div><span class="text-zinc-400">Requested Amount:</span> ₹${Number(loan.requestedAmount || 0).toLocaleString('en-IN')}</div>
          ${loan.approvedAmount ? `<div><span class="text-zinc-400">Approved Amount:</span> ₹${Number(loan.approvedAmount).toLocaleString('en-IN')}</div>` : ''}
          <div><span class="text-zinc-400">Interest Rate:</span> ${loan.interestRate || 0}%</div>
          <div><span class="text-zinc-400">Tenure:</span> ${loan.tenureInMonths || 'N/A'} months</div>
          <div><span class="text-zinc-400">EMI Amount:</span> ₹${Number(loan.emiAmount || 0).toLocaleString('en-IN')}</div>
          <div><span class="text-zinc-400">Application Date:</span> ${loan.applicationDate || 'N/A'}</div>
        <div><span class="text-zinc-400">Approval Date:</span> ${loan.approvalDate || 'N/A'}</div>
        <div><span class="text-zinc-400">Disbursement Date:</span> ${loan.disbursementDate || 'N/A'}</div>
          <div><span class="text-zinc-400">Remaining Months:</span> ${loan.remainingMonths || 'N/A'}</div>
          ${loan.remainingBalance ? `<div><span class="text-zinc-400">Remaining Balance:</span> ₹${Number(loan.remainingBalance).toLocaleString('en-IN')}</div>` : ''}

        </div>

        <div>
          <p class="text-zinc-400 mb-1">Purpose</p>
          <p class="bg-zinc-800 p-4 rounded-2xl">${loan.purpose || 'Not Specified'}</p>
        </div>
        ${loan.repaymentHistory && loan.repaymentHistory.length > 0 ? `
  <div>
    <p class="text-zinc-400 mb-3">Repayment History</p>

    <div class="bg-zinc-800 rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead class="bg-zinc-700">
          <tr>
            <th class="p-3 text-left">Date</th>
            <th class="p-3 text-right">Paid</th>
            <th class="p-3 text-right">Principal</th>
            <th class="p-3 text-right">Interest</th>
            <th class="p-3 text-right">Balance</th>
            <th class="p-3 text-right">Months Left</th>

          </tr>
        </thead>

        <tbody>
          ${loan.repaymentHistory.map(r => `
            <tr class="border-t border-zinc-700">
              <td class="p-3">
                ${new Date(r.paidAt).toLocaleDateString('en-IN')}
              </td>

              <td class="p-3 text-right text-emerald-400">
                ₹${Number(r.amountPaid).toLocaleString('en-IN')}
              </td>
              <td class="p-3 text-right">
              ₹${Number(r.principalPaid || 0).toLocaleString('en-IN')}
               </td>

                <td class="p-3 text-right text-amber-400">
                 ₹${Number(r.interestPaid || 0).toLocaleString('en-IN')}
               </td>

              <td class="p-3 text-right">
                ₹${Number(r.balanceAfterPayment).toLocaleString('en-IN')}
              </td>

              <td class="p-3 text-right">
                ${r.monthsRemainingAfterPayment}
              </td>
            </tr>
          `).join('')}
        </tbody>

      </table>
    </div>
  </div>
` : ''}

        ${loan.message ? `
        <div>
          <p class="text-zinc-400 mb-1">Message / Note</p>
          <p class="bg-zinc-800 p-4 rounded-2xl">${loan.message}</p>
        </div>` : ''}
      </div>
    `;

    
    if (isPending) {
      actions.innerHTML = `
        <button onclick="approveLoan(${loan.loanId}); closeLoanModal()" 
          class="px-8 py-4 bg-emerald-600 hover:bg-emerald-700 rounded-2xl font-medium">
          Approve Loan
        </button>
        <button onclick="rejectLoan(${loan.loanId}); closeLoanModal()" 
          class="px-8 py-4 bg-red-600 hover:bg-red-700 rounded-2xl font-medium">
          Reject Loan
        </button>
      `;
    } else {
      actions.innerHTML = `<button onclick="closeLoanModal()" class="px-8 py-4 bg-zinc-700 hover:bg-zinc-600 rounded-2xl">Close</button>`;
    }

  } catch (err) {
    console.error(err);
    content.innerHTML = `<p class="text-red-400 text-center">Failed to load details</p>`;
  }
}

function closeLoanModal() {
  const modal = document.getElementById('loanModal');
  modal.classList.add('hidden');
  modal.classList.remove('flex');

}
function showToast(message, type = "success") {
  const toast = document.getElementById('toast');
  const toastIcon = document.getElementById('toastIcon');
  const toastMessage = document.getElementById('toastMessage');

  toastMessage.textContent = message;

  if (type === "success") {
    toastIcon.innerHTML = '✅';
    toast.style.borderColor = '#10b981';
  } else if (type === "error") {
    toastIcon.innerHTML = '❌';
    toast.style.borderColor = '#ef4444';
  } else {
    toastIcon.innerHTML = '⚠️';
    toast.style.borderColor = '#eab308';
  }

  toast.classList.remove('hidden');
  toast.style.transform = 'translateY(0)';
  toast.style.opacity = '1';

  
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(20px)';
    setTimeout(() => {
      toast.classList.add('hidden');
    }, 300);
  }, 4000);
}
window.adminLogin = adminLogin;
window.adminLogout = adminLogout;

window.showPage = showPage;
window.loadAdminDashboard = loadAdminDashboard;

window.loadAllUsers = loadAllUsers;
window.showUserDetails = showUserDetails;
window.closeUserModal = closeUserModal;

window.loadUserAccounts = loadUserAccounts;
window.loadUserLoans = loadUserLoans;
window.loadUserTransactions = loadUserTransactions;

window.loadAllLoans = loadAllLoans;
window.showLoanDetails = showLoanDetails;
window.closeLoanModal = closeLoanModal;

window.approveLoan = approveLoan;
window.submitApproval = submitApproval;
window.closeApprovalModal = closeApprovalModal;

window.rejectLoan = rejectLoan;
window.submitReject = submitReject;
window.closeRejectModal = closeRejectModal;

window.loadAllAccounts = loadAllAccounts;

window.showTransactions = showTransactions;
window.closeTransactionModal = closeTransactionModal;

window.showToast = showToast;

