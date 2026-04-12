let currentToken = null;
let currentAccountNumber = null;
// Make these variables accessible from other files
window.currentToken = currentToken;
window.currentAccountNumber = currentAccountNumber;


async function loadDashboard() {
  try {
    const res = await fetch(`${API_BASE}/accounts/dashboard`, { 
      headers: getHeaders() 
    });

    if (res.ok) {
      const dashboard = await res.json();
      const accounts = dashboard.accounts || [];

      if (accounts.length > 0) {
        const acc = accounts[0];
        currentAccountNumber = acc.accountNumber;

        const balance = acc.balance || 0;
        document.getElementById('mainBalance').textContent = `₹${Number(balance).toLocaleString('en-IN')}`;
        document.getElementById('balanceTop').textContent = `₹${Number(balance).toLocaleString('en-IN')}`;

        if (document.getElementById('accountNumberDisplay')) {
          document.getElementById('accountNumberDisplay').innerHTML = 
            `Account No: <span class="font-mono text-emerald-400">${currentAccountNumber}</span>`;
        }
      }
    }
  } catch (err) {
    console.error("Dashboard Error:", err);
  }
}


async function makeDeposit() {
  const amount = parseFloat(document.getElementById('depositAmt').value);
  if (!amount || !currentAccountNumber) {
    return alert("Please enter amount");
  }

  try {
    const res = await fetch(`${API_BASE}/accounts/${currentAccountNumber}/deposit`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify({ amount })
    });

    const data = await res.json();
    if (res.ok) {
      alert("Deposit successful!");
      hideDepositModal();
      loadDashboard();
    } else {
      alert(data.error || "Deposit failed");
    }
  } catch (err) {
    console.error(err);
    alert("Deposit failed");
  }
}


async function makeWithdraw() {
  const amount = parseFloat(document.getElementById('withdrawAmt').value);
  if (!amount || !currentAccountNumber) {
    return alert("Please enter amount");
  }

  try {
    const res = await fetch(`${API_BASE}/accounts/${currentAccountNumber}/withdraw`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify({ amount })
    });

    const data = await res.json();
    if (res.ok) {
      alert("Withdrawal successful!");
      hideWithdrawModal();
      loadDashboard();
    } else {
      alert(data.error || "Withdrawal failed");
    }
  } catch (err) {
    console.error(err);
    alert("Withdrawal failed");
  }
}


async function loadTransactions() {
  if (!currentAccountNumber) {
    document.getElementById('recentTx').innerHTML = `<p class="text-amber-400">No account found.</p>`;
    return;
  }

  try {
    const res = await fetch(`${API_BASE}/accounts/${currentAccountNumber}/transactions`, { 
      headers: getHeaders() 
    });

    const transactions = await res.json();

    
    const recentContainer = document.getElementById('recentTx');
    recentContainer.innerHTML = '';

    transactions.slice(0, 5).forEach(tx => {
      const div = document.createElement('div');
      div.className = "flex justify-between items-center py-3 border-b border-zinc-800 last:border-0";
      div.innerHTML = `
        <div>
          <p class="font-medium">${tx.description || tx.type}</p>
          <p class="text-xs text-zinc-500">${tx.createdAt ? new Date(tx.createdAt).toLocaleString() : 'N/A'}</p>
        </div>
        <p class="${tx.type === 'DEPOSIT' ? 'text-emerald-500' : 'text-red-500'} font-semibold">
          ${tx.type === 'DEPOSIT' ? '+' : '-'}₹${Number(tx.amount).toLocaleString('en-IN')}
        </p>
      `;
      recentContainer.appendChild(div);
    });

   
    const tbody = document.getElementById('txTable');
    tbody.innerHTML = '';

    transactions.forEach(tx => {
      const tr = document.createElement('tr');
      tr.className = "hover:bg-zinc-800";
      tr.innerHTML = `
        <td class="p-5">${tx.createdAt ? new Date(tx.createdAt).toLocaleDateString() : 'N/A'}</td>
        <td class="p-5">${tx.description || tx.type}</td>
        <td class="p-5">
          <span class="px-4 py-1 rounded-full text-xs ${tx.type === 'DEPOSIT' ? 'bg-emerald-900 text-emerald-400' : 'bg-red-900 text-red-400'}">
            ${tx.type}
          </span>
        </td>
        <td class="p-5 text-right font-semibold ${tx.type === 'DEPOSIT' ? 'text-emerald-500' : 'text-red-500'}">
          ${tx.type === 'DEPOSIT' ? '+' : '-'}₹${Number(tx.amount).toLocaleString('en-IN')}
        </td>
      `;
      tbody.appendChild(tr);
    });

  } catch (err) {
    console.error("Transactions Error:", err);
  }
}


async function applyLoan() {
  const amount = parseFloat(document.getElementById('loanAmount').value);
  const tenure = parseInt(document.getElementById('loanTenure').value) || 12;
  const purpose = document.getElementById('loanPurpose').value;

  if (!amount || amount <= 0) return alert("Enter valid loan amount");

  const requestBody = {
    requestedAmount: amount,
    tenureInMonths: tenure,
    purpose: purpose
  };

  try {
    const res = await fetch(`${API_BASE}/loans/apply`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify(requestBody)
    });

    const data = await res.json();

    if (res.ok) {
      alert("✅ Loan application submitted successfully!");
      document.getElementById('loanAmount').value = '';
      showPage('myLoans');
      loadMyLoans();
    } else {
      alert(data.message || "Failed to submit loan");
    }
  } catch (err) {
    console.error(err);
    alert("Error submitting loan");
  }
}


async function loadMyLoans() {
    console.log("LOAD MY LOANS CALLED");
  try {
    const res = await fetch(`${API_BASE}/loans`, { headers: getHeaders() });
    const loans = await res.json();
    console.log("=== LOANS JSON ===", loans);   // For debugging

    const container = document.getElementById('myLoansContainer');
    container.innerHTML = '';

    if (!loans || loans.length === 0) {
      container.innerHTML = `<p class="text-zinc-400">No loans found.</p>`;
      return;
    }
    loans.forEach(loan => {
      const card = document.createElement('div');
      card.className = "bg-zinc-900 rounded-3xl p-8";

      const amount = loan.approvedAmount || loan.requestedAmount || 0;

      card.innerHTML = `
        <div class="flex justify-between items-start">
          <div>
            <h4 class="text-xl font-semibold">${loan.purpose || 'Personal Loan'}</h4>
             <p class="text-sm text-zinc-400 mt-1">
    Loan ID: <span class="font-mono text-emerald-400">${loan.loanId}</span>
  </p>

  <p class="text-sm text-zinc-400">
    Loan No: <span class="font-mono text-amber-400">${loan.loanNumber}</span>
  </p>

  <p class="text-4xl font-bold mt-3 text-amber-500">
   
  </p>
            <p class="text-4xl font-bold mt-3 text-amber-500">
              ₹${Number(amount).toLocaleString('en-IN')}
            </p>
          </div>
          <span class="px-5 py-2 rounded-2xl text-sm font-medium">
            ${loan.status || 'PENDING'}
          </span>
        </div>

        <div class="mt-6 grid grid-cols-2 gap-6 text-sm">
          <div>
            <p class="text-zinc-400">Tenure</p>
            <p class="font-medium">${loan.tenureInMonths || 0} months</p>
          </div>
          <div>
            <p class="text-zinc-400">Interest Rate</p>
            <p class="font-medium">${loan.interestRate || 0}%</p>
          </div>
        </div>

        <div class="mt-8 flex gap-3">
         ${loan.status && loan.status.toUpperCase() === 'ACTIVE'&& loan.emiAmount ? `
          <button onclick="repayLoan(${loan.loanId}, ${loan.emiAmount})"
          class="flex-1 bg-emerald-600 hover:bg-emerald-700 py-4 rounded-2xl font-medium">
          Pay EMI   <p class="text-sm text-zinc-400 mt-2">
           EMI: <span class="text-emerald-400 font-semibold">
         ₹${Number(loan.emiAmount).toLocaleString('en-IN')}
       </span>
       </p>
          </button>` : ''}
          <button onclick="viewRepaymentHistory(${loan.loanId})"
            class="flex-1 bg-zinc-700 py-4 rounded-2xl">
            View History
          </button>
        </div>
        <div id="history-${loan.loanId}" class="mt-6 hidden"></div>
      `;

      container.appendChild(card);
    });

  } catch (err) {
    console.error("Load My Loans Error:", err);
    document.getElementById('myLoansContainer').innerHTML =
      `<p class="text-red-400">Failed to load loans.</p>`;
  }
}




async function repayLoan(loanId, emiAmount) {
  if (!confirm(`Pay EMI of ₹${Number(emiAmount).toLocaleString('en-IN')}?`)) return;

  try {
    const res = await fetch(`${API_BASE}/loans/${loanId}/repay`, {
      method: "POST",
      headers: getHeaders(),
      body: JSON.stringify({ amount: emiAmount })
    });

    if (res.ok) {
      alert("EMI Paid Successfully!");
      loadMyLoans();
    }
  } catch (err) {
    alert("Repayment failed");
  }
}

async function viewRepaymentHistory(loanId) {
  try {
    const res = await fetch(`${API_BASE}/loans/${loanId}/history`, {
      headers: getHeaders()
    });

    if (!res.ok) throw new Error(`HTTP ${res.status}`);

    const history = await res.json();
    console.log("Repayment History:", history);

    const container = document.getElementById(`history-${loanId}`);
    if (!container) {
      console.error(`Container history-${loanId} not found`);
      return;
    }

    
    if (!container.classList.contains('hidden')) {
      container.classList.add('hidden');
      container.innerHTML = '';
      return;
    }

    
    document.querySelectorAll('[id^="history-"]').forEach(el => {
      el.classList.add('hidden');
      el.innerHTML = '';
    });

    container.classList.remove('hidden');

    if (!history || history.length === 0) {
      container.innerHTML = `<p class="text-zinc-400 p-4">No repayment history found.</p>`;
      return;
    }

    let table = `
      <div class="bg-zinc-800 rounded-2xl overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-zinc-700">
            <tr>
              <th class="p-3 text-left">Date</th>
              <th class="p-3 text-right">Amount Paid</th>
              <th class="p-3 text-right">Principal</th>
              <th class="p-3 text-right">Interest</th>
              <th class="p-3 text-right">Balance After</th>
              <th class="p-3 text-center">Months Left</th>
            </tr>
          </thead>
          <tbody>
    `;

    history.forEach(entry => {
      table += `
        <tr class="border-t border-zinc-700">
          <td class="p-3">${new Date(entry.paidAt).toLocaleDateString()}</td>
          <td class="p-3 text-emerald-400 font-semibold text-right">
            ₹${Number(entry.amountPaid || 0).toLocaleString('en-IN')}
          </td>
          <td class="p-3 text-right">
            ₹${Number(entry.principalPaid || 0).toLocaleString('en-IN')}
          </td>
          <td class="p-3 text-right text-amber-400">
            ₹${Number(entry.interestPaid || 0).toLocaleString('en-IN')}
          </td>
          <td class="p-3 text-right">
            ₹${Number(entry.balanceAfterPayment || 0).toLocaleString('en-IN')}
          </td>
          <td class="p-3 text-center">${entry.monthsRemainingAfterPayment || '-'}</td>
        </tr>
      `;
    });

    table += `</tbody></table></div>`;

    container.innerHTML = table;

  } catch (err) {
    console.error("History Error:", err);
    alert("Failed to load repayment history.");
  }
}
  
window.loadDashboard = loadDashboard;
window.makeDeposit = makeDeposit;
window.makeWithdraw = makeWithdraw;
window.loadTransactions = loadTransactions;
window.applyLoan = applyLoan;
window.loadMyLoans = loadMyLoans;
window.repayLoan = repayLoan;
window.viewRepaymentHistory = viewRepaymentHistory;

window.showDepositModal = function() {
  document.getElementById('depositModal').classList.remove('hidden');
};

window.hideDepositModal = function() {
  document.getElementById('depositModal').classList.add('hidden');
};

window.showWithdrawModal = function() {
  document.getElementById('withdrawModal').classList.remove('hidden');
};

window.hideWithdrawModal = function() {
  document.getElementById('withdrawModal').classList.add('hidden');
};


window.showPage = showPage;
window.showTab = showTab;