/**
 * ============================================================
 * SMART CAMPUS SERVICE DESK — MULTI-ROLE CLIENT CONTROLLER
 * ============================================================
 *
 * Dedicated segregated portals for:
 * - 🎓 STUDENT: Post issues, track own tickets, view admin remarks
 * - 🛠️ WORKER: View allocated tasks, start work, notify admin of completion
 * - 👑 ADMIN: Assign workers, bulk upload students via CSV, export credentials, verify & reply
 */

// Application State
const state = {
  currentUser: null,       // Loaded from sessionStorage
  selectedLoginRole: 'STUDENT',
  tickets: [],
  technicians: [],
  users: [],
  cachedCredentials: [],
  currentTicketId: null
};

// Category metadata
const CATEGORY_META = {
  ELECTRICAL: { icon: '⚡', label: 'Electrical', color: '#f59e0b' },
  INTERNET: { icon: '🌐', label: 'Internet / Wi-Fi', color: '#0ea5e9' },
  FURNITURE: { icon: '🪑', label: 'Furniture', color: '#8b5cf6' },
  CLEANING: { icon: '🧹', label: 'Cleaning', color: '#10b981' },
  OTHER: { icon: '🔧', label: 'Other Issue', color: '#64748b' }
};

const STATUS_STEPS = ['PENDING', 'ASSIGNED', 'IN_PROGRESS', 'WORKER_COMPLETED', 'RESOLVED', 'CLOSED'];

// Default Quick Demo Credentials
const DEMO_LOGINS = {
  STUDENT: { email: 'tarun@campus.edu', pass: 'Student@123', label: 'Fill Student (Tarun)' },
  WORKER: { email: 'rajesh.tech@campus.edu', pass: 'Worker@123', label: 'Fill Worker (Rajesh - Electrical)' },
  ADMIN: { email: 'admin@campus.edu', pass: 'Admin@123', label: 'Fill Admin (Full Access)' }
};

// ============================================================
// INITIALIZATION
// ============================================================
document.addEventListener('DOMContentLoaded', () => {
  initTheme();
  setupAuthEvents();
  setupModalEvents();
  setupFormEvents();
  setupFilterEvents();

  // Check existing session
  const storedUser = sessionStorage.getItem('smart_campus_user');
  if (storedUser) {
    try {
      state.currentUser = JSON.parse(storedUser);
      launchPortal();
    } catch (e) {
      sessionStorage.removeItem('smart_campus_user');
      showAuthScreen();
    }
  } else {
    showAuthScreen();
  }
});

// ============================================================
// AUTHENTICATION LOGIC
// ============================================================

function setupAuthEvents() {
  // Role tab switching on login
  document.querySelectorAll('.role-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      document.querySelectorAll('.role-tab').forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      state.selectedLoginRole = tab.dataset.role;

      const demo = DEMO_LOGINS[state.selectedLoginRole];
      document.getElementById('quick-fill-btn').textContent = demo.label;
    });
  });

  // Quick fill demo button
  document.getElementById('quick-fill-btn').addEventListener('click', () => {
    const demo = DEMO_LOGINS[state.selectedLoginRole];
    document.getElementById('login-email').value = demo.email;
    document.getElementById('login-password').value = demo.pass;
  });

  // Login form submit
  document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;

    const btn = document.getElementById('login-submit-btn');
    btn.disabled = true;
    btn.innerHTML = '<span>Verifying credentials...</span>';

    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.message || 'Invalid email or password');
      }

      state.currentUser = data;
      sessionStorage.setItem('smart_campus_user', JSON.stringify(data));
      showToast(`Welcome back, ${data.name}! (${data.role})`, 'success');
      launchPortal();

    } catch (err) {
      showToast(err.message, 'error');
    } finally {
      btn.disabled = false;
      btn.innerHTML = '<span>Secure Sign In</span>';
    }
  });

  // Logout button
  document.getElementById('logout-btn').addEventListener('click', () => {
    sessionStorage.removeItem('smart_campus_user');
    state.currentUser = null;
    showToast('Logged out successfully', 'info');
    showAuthScreen();
  });
}

function showAuthScreen() {
  document.getElementById('auth-screen').style.display = 'flex';
  document.getElementById('app-screen').style.display = 'none';

  const demo = DEMO_LOGINS[state.selectedLoginRole];
  document.getElementById('quick-fill-btn').textContent = demo.label;
}

// ============================================================
// PORTAL LAUNCH & ROLE SEGREGATION
// ============================================================

async function launchPortal() {
  document.getElementById('auth-screen').style.display = 'none';
  document.getElementById('app-screen').style.display = 'block';

  const u = state.currentUser;

  // Header display
  document.getElementById('portal-role-badge').textContent = u.role;
  document.getElementById('user-display-name').textContent = u.name;
  document.getElementById('user-display-email').textContent = `${u.email} (${u.role})`;
  document.getElementById('user-avatar').textContent = u.role === 'ADMIN' ? '👑' : u.role === 'WORKER' ? '🛠️' : '🎓';

  const subtitle = document.getElementById('portal-subtitle');
  if (u.role === 'ADMIN') subtitle.textContent = 'Administrator Command & Dispatch Center';
  else if (u.role === 'WORKER') subtitle.textContent = 'Field Technician & Work Order Desk';
  else subtitle.textContent = 'Student Facility Assistance & Resolution Portal';

  // Build role-specific navigation tabs
  buildNavigationTabs(u.role);

  // Load initial data
  await loadTechnicians();
  await loadTickets();

  if (u.role === 'ADMIN') {
    await loadAdminData();
  }
}

function buildNavigationTabs(role) {
  const container = document.getElementById('nav-tabs-container');
  container.innerHTML = '';

  let tabs = [];

  if (role === 'STUDENT') {
    tabs = [
      { id: 'create-view', label: '➕ Report New Issue', icon: '📝', active: true },
      { id: 'tickets-view', label: '📌 My Reported Issues', icon: '📋', count: true }
    ];
  } else if (role === 'WORKER') {
    tabs = [
      { id: 'tickets-view', label: '🛠️ My Allocated Tasks', icon: '⚙️', active: true, count: true }
    ];
  } else {
    // ADMIN
    tabs = [
      { id: 'tickets-view', label: '📋 Service Requests', icon: '📑', active: true, count: true },
      { id: 'accounts-view', label: '👥 Accounts & CSV Upload', icon: '📂' },
      { id: 'analytics-view', label: '📊 Campus Analytics', icon: '📈' }
    ];
  }

  tabs.forEach(t => {
    const btn = document.createElement('button');
    btn.className = `nav-tab ${t.active ? 'active' : ''}`;
    btn.dataset.tab = t.id;
    btn.innerHTML = `
      <span>${t.icon}</span> ${t.label}
      ${t.count ? '<span id="ticket-count-badge" class="badge-pill">0</span>' : ''}
    `;
    btn.addEventListener('click', () => switchTab(t.id));
    container.appendChild(btn);
  });

  // Activate default view
  const defaultTab = tabs.find(t => t.active)?.id || tabs[0].id;
  switchTab(defaultTab);

  // Hide KPI banner for students to keep interface focused
  document.getElementById('kpi-banner').style.display = role === 'STUDENT' ? 'none' : 'grid';
}

function switchTab(tabId) {
  document.querySelectorAll('.nav-tab').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.tab === tabId);
  });

  document.querySelectorAll('.tab-page').forEach(page => {
    page.classList.toggle('active', page.id === tabId);
  });
}

// ============================================================
// DATA FETCHING (Role-Filtered)
// ============================================================

async function loadTickets() {
  const u = state.currentUser;
  if (!u) return;

  const container = document.getElementById('tickets-container');
  container.innerHTML = `
    <div class="loading-state">
      <div class="spinner"></div>
      <p>Fetching tickets from database...</p>
    </div>`;

  try {
    let url = '/api/requests';

    // Role-based segregation
    if (u.role === 'STUDENT') {
      url = `/api/requests/student/${u.studentId}`;
    } else if (u.role === 'WORKER') {
      url = `/api/requests/worker/${u.technicianId}`;
    } else {
      // Admin: check filters
      const status = document.getElementById('filter-status').value;
      const category = document.getElementById('filter-category').value;
      const search = document.getElementById('search-input').value.trim();

      if (search) url = `/api/requests/search?keyword=${encodeURIComponent(search)}`;
      else if (status) url = `/api/requests?status=${status}`;
      else if (category) url = `/api/requests?category=${category}`;
    }

    const res = await fetch(url);
    const tickets = await res.json();
    state.tickets = tickets || [];

    renderTickets();
    updateKPIMetrics();
    if (u.role === 'ADMIN') renderAnalytics();

  } catch (err) {
    container.innerHTML = `
      <div class="empty-state">
        <div class="empty-icon">⚠️</div>
        <h3>Failed to load tickets</h3>
        <p>${err.message}</p>
      </div>`;
  }
}

async function loadTechnicians() {
  try {
    const res = await fetch('/api/technicians');
    state.technicians = await res.json() || [];
  } catch (e) {
    console.error('Failed to load technicians', e);
  }
}

async function loadAdminData() {
  try {
    const [usersRes, credsRes] = await Promise.all([
      fetch('/api/admin/users'),
      fetch('/api/admin/cached-credentials')
    ]);

    state.users = await usersRes.json() || [];
    state.cachedCredentials = await credsRes.json() || [];

    renderUsersTable();
    renderCredentialsTable();
  } catch (e) {
    console.error('Failed to load admin data', e);
  }
}

// ============================================================
// RENDERING TICKETS (Role-Specific Actions)
// ============================================================

function renderTickets() {
  const container = document.getElementById('tickets-container');
  const countBadge = document.getElementById('ticket-count-badge');
  if (countBadge) countBadge.textContent = state.tickets.length;

  if (state.tickets.length === 0) {
    container.innerHTML = `
      <div class="empty-state" style="grid-column: 1 / -1;">
        <div class="empty-icon">📭</div>
        <h3>No Tickets Found</h3>
        <p>${state.currentUser.role === 'STUDENT' ? 'You have not reported any issues yet.' : state.currentUser.role === 'WORKER' ? 'You have no assigned tasks right now.' : 'No requests matching filters.'}</p>
      </div>`;
    return;
  }

  container.innerHTML = state.tickets.map(t => {
    const cat = CATEGORY_META[t.category] || { icon: '🔧', label: t.category, color: '#64748b' };
    const currentStepIdx = STATUS_STEPS.indexOf(t.status);

    return `
      <div class="ticket-card" style="--status-color: ${cat.color};">
        <div>
          <div class="ticket-header">
            <span class="ticket-id">#REQ-${t.id}</span>
            <div class="ticket-badges">
              <span class="badge badge-cat">${cat.icon} ${cat.label}</span>
              <span class="badge badge-pri-${t.priority}">${t.priority}</span>
              <span class="badge badge-status-${t.status}">${t.status.replace('_', ' ')}</span>
            </div>
          </div>

          <h3 class="ticket-title">${escapeHtml(t.title)}</h3>
          <p class="ticket-desc">${escapeHtml(t.description)}</p>

          <div class="ticket-location">
            <span>📍</span> ${escapeHtml(t.location)}
          </div>

          <!-- Lifecycle Stepper -->
          <div class="stepper-container">
            <div class="stepper-bar">
              ${STATUS_STEPS.slice(0, 5).map((step, idx) => {
                let stepClass = '';
                if (idx < currentStepIdx) stepClass = 'completed';
                else if (idx === currentStepIdx) stepClass = 'active';
                return `<div class="stepper-step ${stepClass}" title="${step}">${idx < currentStepIdx ? '✓' : idx + 1}</div>`;
              }).join('')}
            </div>
            <div class="stepper-labels">
              <span>Pending</span>
              <span>Assigned</span>
              <span>Working</span>
              <span>Repaired</span>
              <span>Resolved</span>
            </div>
          </div>

          <!-- Worker repair notes box -->
          ${t.workerNotes ? `
            <div style="background: rgba(6, 182, 212, 0.08); border-left: 3px solid #06b6d4; padding: 0.5rem 0.75rem; border-radius: 4px; font-size: 0.8rem; margin: 0.5rem 0;">
              <strong>🛠️ Worker Notes:</strong> ${escapeHtml(t.workerNotes)}
            </div>
          ` : ''}

          <!-- Admin reply to student box -->
          ${t.adminRemarks ? `
            <div class="remarks-pill">
              <strong>✅ Admin Reply:</strong> ${escapeHtml(t.adminRemarks)}
            </div>
          ` : ''}
        </div>

        <div class="ticket-footer">
          <div class="reporter-tech-row">
            <div class="reporter-tag">
              <span>👤</span> <strong>${escapeHtml(t.studentName || 'Student')}</strong>
            </div>
            <div class="tech-tag ${t.technicianName ? '' : 'unassigned'}">
              <span>👨‍🔧</span> ${t.technicianName ? escapeHtml(t.technicianName) : '<em>Unassigned</em>'}
            </div>
          </div>

          <div class="ticket-actions">
            ${renderRoleSpecificActions(t)}
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function renderRoleSpecificActions(t) {
  const role = state.currentUser.role;
  const { id, status } = t;

  // 1. STUDENT VIEW
  if (role === 'STUDENT') {
    if (status === 'RESOLVED' || status === 'CLOSED') {
      return `<span style="color: #10b981; font-weight: 700; font-size: 0.8rem;">✓ Resolved by Admin</span>`;
    }
    return `<span style="color: var(--text-dim); font-size: 0.8rem;">⏳ Under Campus Processing</span>`;
  }

  // 2. WORKER VIEW
  if (role === 'WORKER') {
    if (status === 'ASSIGNED') {
      return `
        <button class="btn btn-primary btn-small" onclick="workerStartWork(${id})">
          <span>⚙️ Start Work</span>
        </button>
      `;
    } else if (status === 'IN_PROGRESS') {
      return `
        <button class="btn btn-success btn-small" onclick="openWorkerCompleteModal(${id})">
          <span>✅ Work Finished (Notify Admin)</span>
        </button>
      `;
    } else if (status === 'WORKER_COMPLETED') {
      return `<span class="badge badge-status-WORKER_COMPLETED">🔔 Notified Admin</span>`;
    } else {
      return `<span style="color: #10b981; font-size: 0.8rem;">✓ Closed</span>`;
    }
  }

  // 3. ADMIN VIEW
  if (role === 'ADMIN') {
    if (status === 'PENDING') {
      return `
        <button class="btn btn-primary btn-small" onclick="openAssignModal(${id})">
          <span>👨‍🔧 Assign Worker</span>
        </button>
      `;
    } else if (status === 'ASSIGNED') {
      return `
        <button class="btn btn-secondary btn-small" onclick="openAssignModal(${id})">
          <span>Reassign</span>
        </button>
      `;
    } else if (status === 'WORKER_COMPLETED') {
      return `
        <button class="btn btn-primary btn-small" style="background: linear-gradient(135deg, #10b981, #059669);" onclick="openAdminResolveModal(${id})">
          <span>✅ Verify & Reply to Student</span>
        </button>
      `;
    } else if (status === 'RESOLVED') {
      return `
        <button class="btn btn-secondary btn-small" onclick="adminCloseTicket(${id})">
          <span>🔒 Archive / Close</span>
        </button>
      `;
    } else {
      return `<span class="badge badge-status-${status}">${status}</span>`;
    }
  }

  return '';
}

// ============================================================
// WORKFLOW ACTIONS
// ============================================================

// Worker: Start Work (status -> IN_PROGRESS)
async function workerStartWork(id) {
  try {
    await fetch(`/api/requests/${id}/status?status=IN_PROGRESS`, { method: 'PUT' });
    showToast(`Task #${id} marked as IN_PROGRESS. Happy repairing!`, 'success');
    await loadTickets();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// Worker: Complete Work & Notify Admin
function openWorkerCompleteModal(id) {
  state.currentTicketId = id;
  const ticket = state.tickets.find(t => t.id === id);
  document.getElementById('worker-ticket-info').textContent = `Task #${id}: ${ticket ? ticket.title : ''}`;
  document.getElementById('worker-notes-input').value = '';
  document.getElementById('worker-complete-modal').classList.add('show');
}

async function confirmWorkerComplete() {
  const notes = document.getElementById('worker-notes-input').value.trim();
  if (!notes) {
    showToast('Please provide brief repair notes.', 'warning');
    return;
  }

  try {
    const res = await fetch(`/api/requests/${state.currentTicketId}/worker-complete`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ workerNotes: notes })
    });

    if (!res.ok) throw new Error('Failed to notify admin');

    showToast('Work marked completed. Admin has been notified!', 'success');
    closeAllModals();
    await loadTickets();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// Admin: Open Assign Modal
function openAssignModal(id) {
  state.currentTicketId = id;
  const ticket = state.tickets.find(t => t.id === id);
  document.getElementById('assign-ticket-info').textContent = `Ticket #${id}: ${ticket ? ticket.title : ''}`;

  const select = document.getElementById('assign-tech-select');
  select.innerHTML = state.technicians.map(tech => `
    <option value="${tech.id}">${tech.name} — ${tech.specialization}</option>
  `).join('');

  document.getElementById('assign-modal').classList.add('show');
}

async function confirmAssignWorker() {
  const techId = document.getElementById('assign-tech-select').value;
  if (!techId) return;

  try {
    const res = await fetch(`/api/requests/${state.currentTicketId}/assign/${techId}`, { method: 'PUT' });
    if (!res.ok) throw new Error('Assignment failed');

    showToast('Task allocated to worker successfully!', 'success');
    closeAllModals();
    await loadTickets();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// Admin: Open Resolve & Reply to Student Modal
function openAdminResolveModal(id) {
  state.currentTicketId = id;
  const ticket = state.tickets.find(t => t.id === id);
  document.getElementById('resolve-ticket-info').textContent = `Ticket #${id}: ${ticket ? ticket.title : ''}`;
  document.getElementById('review-worker-notes').textContent = (ticket && ticket.workerNotes) ? ticket.workerNotes : 'Repairs completed by technician.';
  document.getElementById('admin-remarks-input').value = 'Inspection verified by Admin. Facility is restored and in working order.';
  document.getElementById('admin-resolve-modal').classList.add('show');
}

async function confirmAdminResolve() {
  const remarks = document.getElementById('admin-remarks-input').value.trim();
  if (!remarks) {
    showToast('Please enter remarks for the student.', 'warning');
    return;
  }

  try {
    const res = await fetch(`/api/requests/${state.currentTicketId}/admin-resolve`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ adminRemarks: remarks })
    });

    if (!res.ok) throw new Error('Resolution update failed');

    showToast('Ticket marked RESOLVED and reply sent to student!', 'success');
    closeAllModals();
    await loadTickets();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// Admin: Close Ticket
async function adminCloseTicket(id) {
  try {
    await fetch(`/api/requests/${id}/status?status=CLOSED`, { method: 'PUT' });
    showToast(`Ticket #${id} archived as CLOSED.`, 'info');
    await loadTickets();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// ============================================================
// ADMIN ACCOUNT MANAGEMENT (CSV & USER CREATION)
// ============================================================

function renderCredentialsTable() {
  const tbody = document.getElementById('credentials-tbody');
  if (!tbody) return;

  if (state.cachedCredentials.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-dim);">No generated credentials in cache. Bulk upload students to see logins here.</td></tr>`;
    return;
  }

  tbody.innerHTML = state.cachedCredentials.map(c => `
    <tr>
      <td><strong>#${c.studentId}</strong></td>
      <td>${escapeHtml(c.name)}</td>
      <td>${escapeHtml(c.email)}</td>
      <td>${escapeHtml(c.department)}</td>
      <td>Year ${c.year}</td>
      <td><code style="background: rgba(59, 130, 246, 0.2); padding: 2px 6px; border-radius: 4px; color: #38bdf8; font-weight: 700;">${escapeHtml(c.generatedPassword)}</code></td>
    </tr>
  `).join('');
}

function renderUsersTable() {
  const tbody = document.getElementById('users-tbody');
  if (!tbody) return;

  tbody.innerHTML = state.users.map(u => `
    <tr>
      <td>#${u.id}</td>
      <td><strong>${escapeHtml(u.name)}</strong></td>
      <td>${escapeHtml(u.email)}</td>
      <td><span class="badge ${u.role === 'ADMIN' ? 'badge-pri-HIGH' : u.role === 'WORKER' ? 'badge-pri-MEDIUM' : 'badge-pri-LOW'}">${u.role}</span></td>
      <td>${u.studentId ? `Student #${u.studentId}` : u.technicianId ? `Worker #${u.technicianId}` : 'Admin Profile'}</td>
    </tr>
  `).join('');
}

function setupFormEvents() {
  // Student Issue Submission
  const form = document.getElementById('create-ticket-form');
  form?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const title = document.getElementById('ticket-title').value.trim();
    const description = document.getElementById('ticket-description').value.trim();
    const location = document.getElementById('ticket-location').value.trim();
    const category = form.querySelector('input[name="category"]:checked')?.value;
    const priority = form.querySelector('input[name="priority"]:checked')?.value;

    const payload = {
      title,
      description,
      location,
      category,
      priority,
      studentId: state.currentUser.studentId
    };

    try {
      const res = await fetch('/api/requests', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!res.ok) throw new Error('Submission failed');

      const created = await res.json();
      showToast(`Service ticket #${created.id} submitted successfully!`, 'success');
      form.reset();
      switchTab('tickets-view');
      await loadTickets();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // Bulk CSV Process
  document.getElementById('process-csv-btn')?.addEventListener('click', async () => {
    const content = document.getElementById('csv-content-input').value.trim();
    if (!content) {
      showToast('Please enter or paste student CSV records.', 'warning');
      return;
    }

    try {
      const res = await fetch('/api/admin/bulk-upload-students', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: content
      });

      const result = await res.json();
      showToast(`Bulk upload complete! Created ${result.successCount} student accounts.`, 'success');

      await loadAdminData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // Create Student Form
  document.getElementById('create-student-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('cs-name').value.trim();
    const email = document.getElementById('cs-email').value.trim();
    const department = document.getElementById('cs-dept').value.trim();
    const year = Number(document.getElementById('cs-year').value);
    const password = document.getElementById('cs-password').value.trim();

    try {
      const res = await fetch('/api/admin/create-student', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, department, year, password, role: 'STUDENT' })
      });

      if (!res.ok) throw new Error('Failed to create student');
      const cred = await res.json();

      showToast(`Student ${name} created! Password: ${cred.generatedPassword}`, 'success');
      closeAllModals();
      await loadAdminData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // Create Worker Form
  document.getElementById('create-worker-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('cw-name').value.trim();
    const email = document.getElementById('cw-email').value.trim();
    const specialization = document.getElementById('cw-spec').value.trim();
    const password = document.getElementById('cw-password').value.trim();

    try {
      const res = await fetch('/api/admin/create-worker', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, specialization, password, role: 'WORKER' })
      });

      if (!res.ok) throw new Error('Failed to create worker');

      showToast(`Worker ${name} created successfully!`, 'success');
      closeAllModals();
      await loadTechnicians();
      await loadAdminData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });

  // Form micro-interactions (category cards & location chips)
  document.querySelectorAll('.category-card').forEach(c => {
    c.addEventListener('click', () => {
      document.querySelectorAll('.category-card').forEach(x => x.classList.remove('active'));
      c.classList.add('active');
      c.querySelector('input').checked = true;
    });
  });

  document.querySelectorAll('.chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.getElementById('ticket-location').value = chip.dataset.loc;
    });
  });
}

function setupModalEvents() {
  document.getElementById('confirm-assign-btn')?.addEventListener('click', confirmAssignWorker);
  document.getElementById('confirm-worker-complete-btn')?.addEventListener('click', confirmWorkerComplete);
  document.getElementById('confirm-admin-resolve-btn')?.addEventListener('click', confirmAdminResolve);

  document.getElementById('add-student-btn')?.addEventListener('click', () => {
    document.getElementById('create-student-modal').classList.add('show');
  });

  document.getElementById('add-worker-btn')?.addEventListener('click', () => {
    document.getElementById('create-worker-modal').classList.add('show');
  });

  document.querySelectorAll('[data-close]').forEach(btn => {
    btn.addEventListener('click', closeAllModals);
  });
}

function closeAllModals() {
  document.querySelectorAll('.modal-backdrop').forEach(m => m.classList.remove('show'));
  state.currentTicketId = null;
}

function setupFilterEvents() {
  document.getElementById('filter-status')?.addEventListener('change', () => loadTickets());
  document.getElementById('filter-category')?.addEventListener('change', () => loadTickets());
  document.getElementById('refresh-tickets-btn')?.addEventListener('click', () => loadTickets());

  const searchInput = document.getElementById('search-input');
  let timer;
  searchInput?.addEventListener('input', (e) => {
    clearTimeout(timer);
    timer = setTimeout(() => loadTickets(), 350);
  });
}

// ============================================================
// METRICS & ANALYTICS
// ============================================================

function updateKPIMetrics() {
  const total = state.tickets.length;
  const pending = state.tickets.filter(t => t.status === 'PENDING').length;
  const inProgress = state.tickets.filter(t => t.status === 'IN_PROGRESS' || t.status === 'ASSIGNED').length;
  const workerCompleted = state.tickets.filter(t => t.status === 'WORKER_COMPLETED').length;
  const resolved = state.tickets.filter(t => t.status === 'RESOLVED' || t.status === 'CLOSED').length;

  document.getElementById('kpi-total').textContent = total;
  document.getElementById('kpi-pending').textContent = pending;
  document.getElementById('kpi-in-progress').textContent = inProgress;
  document.getElementById('kpi-worker-completed').textContent = workerCompleted;
  document.getElementById('kpi-resolved').textContent = resolved;
}

function renderAnalytics() {
  const catContainer = document.getElementById('analytics-category-bars');
  const statusContainer = document.getElementById('analytics-status-bars');
  if (!catContainer || !statusContainer) return;

  const total = state.tickets.length || 1;

  // Categories
  const catCounts = { ELECTRICAL: 0, INTERNET: 0, FURNITURE: 0, CLEANING: 0, OTHER: 0 };
  state.tickets.forEach(t => {
    if (catCounts[t.category] !== undefined) catCounts[t.category]++;
  });

  catContainer.innerHTML = Object.entries(catCounts).map(([cat, count]) => {
    const meta = CATEGORY_META[cat] || { icon: '🔧', label: cat, color: '#64748b' };
    const pct = Math.round((count / total) * 100);
    return `
      <div class="bar-row">
        <div class="bar-meta">
          <span>${meta.icon} ${meta.label}</span>
          <strong>${count} (${pct}%)</strong>
        </div>
        <div class="bar-track">
          <div class="bar-fill" style="width: ${pct}%; background: ${meta.color};"></div>
        </div>
      </div>
    `;
  }).join('');

  // Statuses
  const statusCounts = { PENDING: 0, ASSIGNED: 0, IN_PROGRESS: 0, WORKER_COMPLETED: 0, RESOLVED: 0, CLOSED: 0 };
  state.tickets.forEach(t => {
    if (statusCounts[t.status] !== undefined) statusCounts[t.status]++;
  });

  statusContainer.innerHTML = Object.entries(statusCounts).map(([st, count]) => {
    const pct = Math.round((count / total) * 100);
    return `
      <div class="bar-row">
        <div class="bar-meta">
          <span>${st.replace('_', ' ')}</span>
          <strong>${count} (${pct}%)</strong>
        </div>
        <div class="bar-track">
          <div class="bar-fill" style="width: ${pct}%; background: var(--status-${st.toLowerCase().replace('_', '-')}, #3b82f6);"></div>
        </div>
      </div>
    `;
  }).join('');
}

// ============================================================
// UTILITIES
// ============================================================

function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;

  const icon = type === 'success' ? '✅' : type === 'error' ? '⚠️' : 'ℹ️';
  toast.innerHTML = `<span>${icon}</span><div>${escapeHtml(message)}</div>`;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

function initTheme() {
  const saved = localStorage.getItem('theme') || 'dark';
  document.body.className = saved === 'light' ? 'light-theme' : 'dark-theme';
  document.getElementById('theme-icon').textContent = saved === 'light' ? '☀️' : '🌙';

  document.getElementById('theme-toggle-btn').addEventListener('click', () => {
    const isLight = document.body.classList.contains('light-theme');
    const newTheme = isLight ? 'dark' : 'light';
    document.body.className = `${newTheme}-theme`;
    document.getElementById('theme-icon').textContent = newTheme === 'light' ? '☀️' : '🌙';
    localStorage.setItem('theme', newTheme);
  });
}

function escapeHtml(str) {
  if (!str) return '';
  return str.replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
}
