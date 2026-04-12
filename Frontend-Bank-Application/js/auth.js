async function register() {
  const fullName = document.getElementById('regFullName').value.trim();
  const email = document.getElementById('regEmail').value.trim();
  const phone = document.getElementById('regPhone').value.trim();
  const password = document.getElementById('regPassword').value;
  const gender = document.getElementById('regGender').value;
  const address = document.getElementById('regAddress').value.trim();

  if (!fullName || !email || !phone || !password) {
    return alert("Please fill all required fields");
  }

  try {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        fullName,
        email,
        phoneNumber: phone,
        password,
        gender,
        address
      })
    });

    const data = await res.json();

    if (res.ok) {
      alert("✅ Registration Successful! Please login.");
      showTab('login');
    } else {
      alert(data.message || "Registration failed");
    }
  } catch (err) {
    console.error("Register Error:", err);
    alert("Registration failed. Check console.");
  }
}

async function login() {
  const email = document.getElementById('loginEmail').value.trim();
  const password = document.getElementById('loginPassword').value;

  if (!email || !password) return alert("Please enter email and password");

  try {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });

    const data = await res.json();

    if (res.ok && data.token) {
      currentToken = data.token;
      localStorage.setItem("token", currentToken);

      document.getElementById('userNameDisplay').textContent = data.fullName || "User";

      document.getElementById('authScreen').classList.add('hidden');
      document.getElementById('mainApp').classList.remove('hidden');

      alert("Login Successful!");

      await loadDashboard();
      showPage('dashboard');
    } else {
      alert(data.message || "Login failed");
    }
  } catch (err) {
    console.error(err);
    alert("Login failed");
  }
}

function showTab(tab) {
  const loginForm = document.getElementById('loginForm');
  const registerForm = document.getElementById('registerForm');
  const loginTab = document.getElementById('loginTab');
  const registerTab = document.getElementById('registerTab');

  if (tab === 'login') {
    loginForm.classList.remove('hidden');
    registerForm.classList.add('hidden');
    loginTab.classList.add('border-b-2', 'border-emerald-500', 'text-emerald-400');
    registerTab.classList.remove('border-b-2', 'border-emerald-500', 'text-emerald-400');
  } else {
    loginForm.classList.add('hidden');
    registerForm.classList.remove('hidden');
    registerTab.classList.add('border-b-2', 'border-emerald-500', 'text-emerald-400');
    loginTab.classList.remove('border-b-2', 'border-emerald-500', 'text-emerald-400');
  }
}