function getHeaders() {
  const token = localStorage.getItem("token");
  return {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${token}`
  };
}

function showPage(page) {
  document.querySelectorAll('#dashboard,#transactions,#loanApplication,#myLoans').forEach(el => {
    el.classList.add('hidden');
  });
  document.getElementById(page).classList.remove('hidden');

  document.getElementById('pageTitle').textContent = 
    page === 'dashboard' ? 'Dashboard' :
    page === 'transactions' ? 'Transactions' :
    page === 'loanApplication' ? 'Apply for Loan' : 'My Loans';

  if (page === 'transactions') loadTransactions();
  if (page === 'myLoans') loadMyLoans();
}

function logout() {
  if (confirm("Logout from NeoBank?")) {
    localStorage.clear();
    location.reload();
  }
}

function getHeaders() {
   return {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${currentToken}`
   };
}

function showToast(msg){
   alert(msg);
}