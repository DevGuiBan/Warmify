const state = {
  token: localStorage.getItem('warmify.token') || '',
  apiBase: localStorage.getItem('warmify.apiBase') || '',
  authTab: 'login',
  section: 'dashboard',
  loading: false,
  me: null,
  data: {
    users: [],
    domains: [],
    facebook: [],
    instagram: [],
    portfolios: [],
    pages: [],
    numbers: [],
    fbRecovery: [],
    igRecovery: []
  },
  search: {
    users: '',
    domains: '',
    facebook: '',
    instagram: '',
    portfolios: '',
    pages: '',
    numbers: '',
    fbRecovery: '',
    igRecovery: ''
  }
};
const sections = {
  dashboard: { title: 'Dashboard', subtitle: 'Visão geral do aquecimento e dos vínculos operacionais.' },
  users: { title: 'Usuários', subtitle: 'Controle de acesso, permissões e ativação de operadores.' },
  domains: { title: 'Domínios', subtitle: 'Base de domínios usados por portfólios e páginas.' },
  facebook: { title: 'Facebook', subtitle: 'Contas Facebook para aquecimento, mapeamento e portfólio.' },
  instagram: { title: 'Instagram', subtitle: 'Contas Instagram com credenciais e dados de recuperação.' },
  portfolios: { title: 'Portfólios de negócio', subtitle: 'Vínculo central entre conta, domínio, páginas e números.' },
  pages: { title: 'Páginas Facebook', subtitle: 'Associação entre páginas, contas Facebook e portfólios.' },
  numbers: { title: 'Números de portfólio', subtitle: 'Telefones operacionais e status de uso.' },
  fbRecovery: { title: 'Recuperação Facebook', subtitle: 'Chaves de recuperação vinculadas às contas Facebook.' },
  igRecovery: { title: 'Recuperação Instagram', subtitle: 'Chaves e status de recuperação vinculadas às contas Instagram.' }
};
const navItems = [
  ['dashboard', 'Dashboard'],
  ['users', 'Usuários'],
  ['domains', 'Domínios'],
  ['facebook', 'Facebook'],
  ['instagram', 'Instagram'],
  ['portfolios', 'Portfólios'],
  ['pages', 'Páginas'],
  ['numbers', 'Números'],
  ['fbRecovery', 'Rec. Facebook'],
  ['igRecovery', 'Rec. Instagram']
];
const els = {
  authScreen: document.getElementById('auth-screen'),
  appShell: document.getElementById('app-shell'),
  nav: document.getElementById('nav'),
  currentUser: document.getElementById('current-user'),
  apiStatus: document.getElementById('api-status'),
  pageTitle: document.getElementById('page-title'),
  pageSubtitle: document.getElementById('page-subtitle'),
  content: document.getElementById('content'),
  modal: document.getElementById('modal'),
  toastArea: document.getElementById('toast-area'),
  refreshBtn: document.getElementById('refresh-btn'),
  quickAddBtn: document.getElementById('quick-add-btn'),
  logoutBtn: document.getElementById('logout-btn')
};
function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}
function shorten(value, size = 18) {
  const text = String(value ?? '');
  return text.length > size ? `${text.slice(0, size)}…` : text;
}
function titleCase(value) {
  return String(value ?? '—')
    .replaceAll('_', ' ')
    .toLowerCase()
    .replace(/(^|\s)\S/g, (match) => match.toUpperCase());
}
function pill(label, kind = '') {
  return `<span class="pill ${kind}">${escapeHtml(label)}</span>`;
}
function statusKind(value) {
  const v = String(value ?? '').toLowerCase();
  if (!v) return '';
  if (v.includes('available') || v.includes('verified') || v.includes('active') || v.includes('mapped')) return 'good';
  if (v.includes('blocked') || v.includes('unavailable') || v.includes('inactive') || v.includes('restricted')) return 'bad';
  if (v.includes('used') || v.includes('analyze') || v.includes('assigned') || v.includes('notverified')) return 'warn';
  return '';
}
function authHeader() {
  return state.token ? { Authorization: `Bearer ${state.token}` } : {};
}
function apiUrl(path) {
  const base = state.apiBase.trim();
  if (!base) return path;
  return `${base.replace(/\/$/, '')}${path}`;
}
function setApiBase(base) {
  state.apiBase = base.trim();
  localStorage.setItem('warmify.apiBase', state.apiBase);
  els.apiStatus.textContent = state.apiBase ? `API: ${state.apiBase}` : 'API: mesma origem';
}
async function readBody(response) {
  const text = await response.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}
function errorMessage(error) {
  if (!error) return 'Erro inesperado.';
  if (typeof error === 'string') return error;
  if (error.message) return error.message;
  return 'Erro inesperado.';
}
async function request(path, options = {}) {
  const { body, headers, method = 'GET' } = options;
  const init = {
    method,
    headers: {
      ...authHeader(),
      ...(headers || {})
    }
  };
  if (body !== undefined) {
    if (body instanceof FormData) {
      init.body = body;
    } else if (typeof body === 'string') {
      init.body = body;
    } else {
      init.body = JSON.stringify(body);
      init.headers['Content-Type'] = 'application/json';
    }
  }
  const response = await fetch(apiUrl(path), init);
  const payload = await readBody(response);
  if (!response.ok) {
    const message = typeof payload === 'string'
      ? payload
      : payload?.message || payload?.error || payload?.detail || `Falha na requisição (${response.status}).`;
    const error = new Error(message);
    error.status = response.status;
    error.payload = payload;
    throw error;
  }
  return payload;
}
function isEmptyResultError(error) {
  if (!error) return false;
  const message = errorMessage(error).toLowerCase();
  return [404, 400].includes(error.status) || message.includes('nenhum') || message.includes('nenhuma') || message.includes('not found');
}
async function fetchList(path) {
  try {
    const result = await request(path);
    return Array.isArray(result) ? result : (result ? [result] : []);
  } catch (error) {
    if (isEmptyResultError(error)) return [];
    throw error;
  }
}
async function fetchDetail(path) {
  try {
    return await request(path);
  } catch (error) {
    if (isEmptyResultError(error)) return null;
    throw error;
  }
}
function showToast(title, message, kind = 'good') {
  const node = document.createElement('div');
  node.className = `toast ${kind}`;
  node.innerHTML = `<strong>${escapeHtml(title)}</strong><div>${escapeHtml(message)}</div>`;
  els.toastArea.appendChild(node);
  setTimeout(() => node.remove(), 4200);
}
function openModal(html) {
  els.modal.innerHTML = html;
  if (typeof els.modal.showModal === 'function') {
    els.modal.showModal();
  } else {
    els.modal.setAttribute('open', 'open');
  }
}
function closeModal() {
  if (typeof els.modal.close === 'function') {
    els.modal.close();
  } else {
    els.modal.removeAttribute('open');
  }
  els.modal.innerHTML = '';
}
function renderAuthScreen() {
  els.authScreen.classList.remove('hidden');
  els.appShell.classList.add('hidden');
  const activeLogin = state.authTab === 'login';
  els.authScreen.innerHTML = `
    <section class="auth-card auth-minimal">
      <header class="auth-head">
        <div class="brand">
          <div class="brand-mark">W</div>
          <div>
            <strong>Warmify</strong>
            <small>Acesse seu painel</small>
          </div>
        </div>
      </header>

      <div class="tabs">
        <button class="tab-button ${activeLogin ? 'active' : ''}" data-auth-tab="login" type="button">Entrar</button>
        <button class="tab-button ${!activeLogin ? 'active' : ''}" data-auth-tab="register" type="button">Cadastrar</button>
      </div>

      <form id="auth-form" class="form">
        <div id="auth-fields"></div>
        <div class="field">
          <label for="api-base">Base da API</label>
          <input id="api-base" name="apiBase" type="url" placeholder="Opcional: outra origem da API" value="${escapeHtml(state.apiBase)}" />
        </div>
        <div class="helper">Use o login para abrir o painel de gerenciamento.</div>
        <div class="form-actions">
          <button class="primary-button" type="submit">${activeLogin ? 'Entrar' : 'Criar acesso'}</button>
        </div>
      </form>
    </section>
  `;
  const fields = activeLogin
    ? `
      <div class="field">
        <label for="email">Email</label>
        <input id="email" name="email" type="email" placeholder="voce@empresa.com" required />
      </div>
      <div class="field">
        <label for="password">Senha</label>
        <input id="password" name="password" type="password" placeholder="Sua senha" required />
      </div>
    `
    : `
      <div class="field">
        <label for="name">Nome</label>
        <input id="name" name="name" type="text" placeholder="Nome do usuário" required />
      </div>
      <div class="field">
        <label for="email">Email</label>
        <input id="email" name="email" type="email" placeholder="voce@empresa.com" required />
      </div>
      <div class="field">
        <label for="password">Senha</label>
        <input id="password" name="password" type="password" placeholder="Crie uma senha" required />
      </div>
    `;
  document.getElementById('auth-fields').innerHTML = fields;
  document.querySelectorAll('[data-auth-tab]').forEach((button) => {
    button.addEventListener('click', () => {
      state.authTab = button.dataset.authTab;
      renderAuthScreen();
    });
  });
  document.getElementById('auth-form').addEventListener('submit', onAuthSubmit);
}
async function onAuthSubmit(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const values = Object.fromEntries(new FormData(form).entries());
  setApiBase(String(values.apiBase || ''));
  try {
    if (state.authTab === 'login') {
      const result = await request('/auth/login', {
        method: 'POST',
        body: {
          email: values.email,
          password: values.password
        }
      });
      const token = result?.token || result?.accessToken || result?.jwt || result;
      if (!token) throw new Error('Resposta de login inválida.');
      state.token = token;
      localStorage.setItem('warmify.token', state.token);
      showToast('Login realizado', 'Aguarde enquanto carregamos os dados.', 'good');
      await bootstrapApp(values.email);
      return;
    }
    await request('/auth/register', {
      method: 'POST',
      body: {
        name: values.name,
        email: values.email,
        password: values.password
      }
    });
    showToast('Cadastro criado', 'Agora você pode entrar com o novo usuário.', 'good');
    state.authTab = 'login';
    renderAuthScreen();
  } catch (error) {
    showToast('Falha na autenticação', errorMessage(error), 'bad');
  }
}
function renderShell() {
  els.authScreen.classList.add('hidden');
  els.appShell.classList.remove('hidden');
  els.nav.innerHTML = navItems.map(([key, label]) => `
    <button type="button" class="nav-button ${state.section === key ? 'active' : ''}" data-section="${key}">
      <span>${escapeHtml(label)}</span>
      <span class="muted tiny">${sectionCount(key)}</span>
    </button>
  `).join('');
  els.currentUser.textContent = state.me ? `${state.me.name || state.me.email || 'Usuário'} • ${state.me.role || '—'}` : '—';
  els.apiStatus.textContent = state.apiBase ? `API: ${state.apiBase}` : 'API: mesma origem';
  const meta = sections[state.section] || sections.dashboard;
  els.pageTitle.textContent = meta.title;
  els.pageSubtitle.textContent = meta.subtitle;
  els.quickAddBtn.textContent = state.section === 'dashboard' ? 'Novo registro' : `Novo ${sectionSingular(state.section)}`;
  renderContent();
}
function sectionCount(section) {
  const d = state.data;
  const map = {
    dashboard: 'visão',
    users: d.users.length,
    domains: d.domains.length,
    facebook: d.facebook.length,
    instagram: d.instagram.length,
    portfolios: d.portfolios.length,
    pages: d.pages.length,
    numbers: d.numbers.length,
    fbRecovery: d.fbRecovery.length,
    igRecovery: d.igRecovery.length
  };
  return String(map[section] ?? '0');
}
function sectionSingular(section) {
  const map = {
    users: 'usuário',
    domains: 'domínio',
    facebook: 'conta Facebook',
    instagram: 'conta Instagram',
    portfolios: 'portfólio',
    pages: 'página',
    numbers: 'número',
    fbRecovery: 'chave',
    igRecovery: 'chave'
  };
  return map[section] || 'registro';
}
function normalizeSearch(section, rowValues) {
  const term = String(state.search[section] || '').trim().toLowerCase();
  if (!term) return true;
  return rowValues.some((value) => String(value ?? '').toLowerCase().includes(term));
}
function filteredRows(section, rows, extractor) {
  return rows.filter((row) => normalizeSearch(section, extractor(row)));
}
function statusBadge(value) {
  return pill(titleCase(value), statusKind(value));
}
function checkboxLabel(value) {
  return value ? pill('Ativo', 'good') : pill('Inativo', 'bad');
}
function listById(items, id) {
  return items.find((item) => item.id === id) || null;
}
function renderContent() {
  if (state.loading) {
    els.content.innerHTML = '<div class="loading">Carregando dados do painel…</div>';
    return;
  }
  switch (state.section) {
    case 'dashboard':
      els.content.innerHTML = renderDashboard();
      break;
    case 'users':
      els.content.innerHTML = renderUsers();
      break;
    case 'domains':
      els.content.innerHTML = renderDomains();
      break;
    case 'facebook':
      els.content.innerHTML = renderFacebook();
      break;
    case 'instagram':
      els.content.innerHTML = renderInstagram();
      break;
    case 'portfolios':
      els.content.innerHTML = renderPortfolios();
      break;
    case 'pages':
      els.content.innerHTML = renderPages();
      break;
    case 'numbers':
      els.content.innerHTML = renderNumbers();
      break;
    case 'fbRecovery':
      els.content.innerHTML = renderFbRecovery();
      break;
    case 'igRecovery':
      els.content.innerHTML = renderIgRecovery();
      break;
    default:
      els.content.innerHTML = renderDashboard();
  }
}
function renderDashboard() {
  const { users, domains, facebook, instagram, portfolios, pages, numbers, fbRecovery, igRecovery } = state.data;
  const fbAvailable = facebook.filter((item) => String(item.statusAccount).includes('AVAILABLE')).length;
  const fbMapped = facebook.filter((item) => String(item.statusAccount).includes('MAPPED')).length;
  const fbUnavailable = facebook.filter((item) => String(item.statusAccount).includes('UNAVAILABLE')).length;
  const igAvailable = instagram.filter((item) => String(item.statusAccount).includes('AVAILABLE')).length;
  const igMapped = instagram.filter((item) => String(item.statusAccount).includes('MAPPED')).length;
  const igUnavailable = instagram.filter((item) => String(item.statusAccount).includes('UNAVAILABLE')).length;
  const verifiedBm = portfolios.filter((item) => String(item.status).includes('VERIFIED')).length;
  const blockedBm = portfolios.filter((item) => String(item.status).includes('BLOCKED')).length;
  const warmScore = facebook.length + instagram.length ? Math.round(((fbAvailable + igAvailable) / (facebook.length + instagram.length)) * 100) : 0;
  return `
    <section class="section">
      <div class="section-card compact-card">
        <div class="card-head">
          <div>
            <h3>Visão geral</h3>
            <p>Resumo rápido dos indicadores de aquecimento e operação.</p>
          </div>
          <span class="stat-tag">Warm score: ${warmScore}%</span>
        </div>
      </div>

      <div class="grid cols-3">
        ${statCard('Aquecimento ativo', fbAvailable + igAvailable, 'Contas disponíveis para operação', 'warming')}
        ${statCard('Contas mapeadas', fbMapped + igMapped, 'Contas prontas e vinculadas', 'mapping')}
        ${statCard('Contas indisponíveis', fbUnavailable + igUnavailable, 'Perfis fora de operação', 'risk')}
      </div>

      <div class="grid cols-3">
        ${statCard('Portfólios', portfolios.length, `Verificados: ${verifiedBm}`, 'business')}
        ${statCard('Estruturas', pages.length + numbers.length, 'Páginas + números cadastrados', 'assets')}
        ${statCard('Recuperação', fbRecovery.length + igRecovery.length, `BM bloqueados: ${blockedBm}`, 'security')}
      </div>

      <details class="section-details" open>
        <summary>Detalhes por canal</summary>
        <div class="grid cols-2" style="margin-top:12px;">
          <div class="details-card">
            <div class="card-head"><div><h3>Facebook</h3><p>Status das contas Facebook.</p></div></div>
            <div class="kpi-row" style="margin-top:12px;">
              <div class="kpi"><strong>${fbAvailable}</strong><span>available</span></div>
              <div class="kpi"><strong>${fbMapped}</strong><span>mapped</span></div>
              <div class="kpi"><strong>${fbUnavailable}</strong><span>unavailable</span></div>
            </div>
          </div>
          <div class="details-card">
            <div class="card-head"><div><h3>Instagram</h3><p>Status das contas Instagram.</p></div></div>
            <div class="kpi-row" style="margin-top:12px;">
              <div class="kpi"><strong>${igAvailable}</strong><span>available</span></div>
              <div class="kpi"><strong>${igMapped}</strong><span>mapped</span></div>
              <div class="kpi"><strong>${igUnavailable}</strong><span>unavailable</span></div>
            </div>
          </div>
        </div>
      </details>
    </section>
  `;
}
function statCard(title, value, subtitle, tag) {
  return `
    <div class="stat-card">
      <div class="stat-meta">
        <div class="stat-tag">${escapeHtml(title)}</div>
        <span class="muted tiny">${escapeHtml(tag)}</span>
      </div>
      <strong>${escapeHtml(value)}</strong>
      <span class="muted">${escapeHtml(subtitle)}</span>
    </div>
  `;
}
function summaryItem(title, items, labelKey, statusKey) {
  const item = items[0];
  const status = statusKey && item ? item[statusKey] : null;
  return `
    <div class="list-item">
      <div>
        <strong>${escapeHtml(title)}</strong>
        <small>${item ? escapeHtml(item[labelKey]) : 'Sem registros'}</small>
      </div>
      <div>${item ? (status ? statusBadge(status) : pill('OK', 'good')) : pill('Vazio', 'warn')}</div>
    </div>
  `;
}
function actionButtons(kind, id) {
  return actionMenu(kind, id, [
    { action: `edit-${kind}`, label: 'Editar' },
    { action: `delete-${kind}`, label: 'Excluir' }
  ]);
}

function actionMenu(kind, id, extraActions = []) {
  const actions = [
    { action: `view-${kind}`, label: 'Ver' },
    ...extraActions
  ];

  return `
    <div class="table-actions compact-actions">
      <button type="button" class="table-action" data-action="view-${kind}" data-id="${escapeHtml(id)}">Ver</button>
      <details class="action-menu">
        <summary>Ações</summary>
        <div class="action-menu-list">
          ${actions
            .filter((item, index) => index !== 0)
            .map((item) => `<button type="button" class="table-action" data-action="${escapeHtml(item.action)}" data-id="${escapeHtml(id)}">${escapeHtml(item.label)}</button>`)
            .join('')}
        </div>
      </details>
    </div>
  `;
}
function baseSectionHtml(title, description, toolbar, table) {
  return `
    <section class="section">
      <div class="section-card">
        <div class="section-head">
          <div>
            <h2>${escapeHtml(title)}</h2>
            <p>${escapeHtml(description)}</p>
          </div>
          <button type="button" class="primary-button" data-action="create-current">Novo ${escapeHtml(sectionSingular(state.section))}</button>
        </div>
      </div>
      <div class="table-card">
        <div class="toolbar">
          <input id="section-search" class="field search" placeholder="Pesquisar na lista…" value="${escapeHtml(state.search[state.section])}" />
          ${toolbar || ''}
        </div>
        ${table}
      </div>
    </section>
  `;
}
function tableShell(headers, rowsHtml) {
  return `
    <div class="table-wrap">
      <table>
        <thead><tr>${headers.map((header) => `<th>${escapeHtml(header)}</th>`).join('')}</tr></thead>
        <tbody>${rowsHtml || '<tr><td colspan="99"><div class="empty">Nenhum registro encontrado.</div></td></tr>'}</tbody>
      </table>
    </div>
  `;
}
function renderUsers() {
  const rows = filteredRows('users', state.data.users, (row) => [row.name, row.email, row.role, row.active]);
  const rowsHtml = rows.map((user) => `
    <tr>
      <td><strong>${escapeHtml(user.name)}</strong><br><span class="muted tiny">${escapeHtml(user.id)}</span></td>
      <td>${escapeHtml(user.email)}</td>
      <td>${statusBadge(user.role)}</td>
      <td>${checkboxLabel(user.active)}</td>
      <td>${actionMenu('user', user.id, [
        { action: 'edit-user', label: 'Editar' },
        { action: 'toggle-role-user', label: 'Alterar role' },
        { action: 'toggle-active-user', label: 'Ativar/Desativar' }
      ])}</td>
    </tr>
  `).join('');
  return baseSectionHtml('Usuários', sections.users.subtitle, '', tableShell(['Nome', 'Email', 'Role', 'Status', 'Ações'], rowsHtml));
}
function renderDomains() {
  const rows = filteredRows('domains', state.data.domains, (row) => [row.url, row.id]);
  const rowsHtml = rows.map((domain) => `
    <tr>
      <td><strong>${escapeHtml(domain.url)}</strong></td>
      <td class="muted tiny">${escapeHtml(domain.id)}</td>
      <td>${actionButtons('domain', domain.id)}</td>
    </tr>
  `).join('');
  return baseSectionHtml('Domínios', sections.domains.subtitle, '', tableShell(['URL', 'ID', 'Ações'], rowsHtml));
}
function renderFacebook() {
  const rows = filteredRows('facebook', state.data.facebook, (row) => [row.profileName, row.email, row.url, row.statusAccount]);
  const rowsHtml = rows.map((item) => `
    <tr>
      <td><strong>${escapeHtml(item.profileName)}</strong><br><span class="muted tiny">${escapeHtml(item.id)}</span></td>
      <td>${escapeHtml(item.email)}</td>
      <td>${escapeHtml(item.url)}</td>
      <td>${statusBadge(item.statusAccount)}</td>
      <td>${actionMenu('facebook', item.id, [
        { action: 'edit-facebook', label: 'Editar' },
        { action: 'delete-facebook', label: 'Excluir' }
      ])}</td>
    </tr>
  `).join('');
  return baseSectionHtml('Facebook', sections.facebook.subtitle, '', tableShell(['Perfil', 'Email', 'URL', 'Status', 'Ações'], rowsHtml));
}
function renderInstagram() {
  const rows = filteredRows('instagram', state.data.instagram, (row) => [row.username, row.email, row.googleAuthenticatorEmail, row.statusAccount]);
  const rowsHtml = rows.map((item) => `
    <tr>
      <td><strong>${escapeHtml(item.username)}</strong><br><span class="muted tiny">${escapeHtml(item.id)}</span></td>
      <td>${escapeHtml(item.email)}</td>
      <td>${escapeHtml(item.googleAuthenticatorEmail || '—')}</td>
      <td>${statusBadge(item.statusAccount)}</td>
      <td>${actionMenu('instagram', item.id, [
        { action: 'edit-instagram', label: 'Editar' },
        { action: 'delete-instagram', label: 'Excluir' }
      ])}</td>
    </tr>
  `).join('');
  return baseSectionHtml('Instagram', sections.instagram.subtitle, '', tableShell(['Usuário', 'Email', 'Google Auth', 'Status', 'Ações'], rowsHtml));
}
function renderPortfolios() {
  const rows = filteredRows('portfolios', state.data.portfolios, (row) => [row.bmName, row.cnpj, row.status, row.domainId, row.facebookAccountId, row.instagramAccountId]);
  const rowsHtml = rows.map((item) => `
    <tr>
      <td><strong>${escapeHtml(item.bmName)}</strong><br><span class="muted tiny">${escapeHtml(item.id)}</span></td>
      <td>${escapeHtml(item.cnpj)}</td>
      <td>${statusBadge(item.status)}</td>
      <td>${escapeHtml(domainLabel(item.domainId))}</td>
      <td>${escapeHtml(facebookLabel(item.facebookAccountId))}</td>
      <td>${escapeHtml(instagramLabel(item.instagramAccountId))}</td>
      <td>${actionMenu('portfolio', item.id, [
        { action: 'edit-portfolio', label: 'Editar' },
        { action: 'delete-portfolio', label: 'Excluir' }
      ])}</td>
    </tr>
  `).join('');
  return baseSectionHtml('Portfólios de negócio', sections.portfolios.subtitle, '', tableShell(['BM', 'CNPJ', 'Status', 'Domínio', 'Facebook', 'Instagram', 'Ações'], rowsHtml));
}
function renderPages() {
  const rows = filteredRows('pages', state.data.pages, (row) => [row.pageName, row.businessPortfolioId, row.facebookAccountId]);
  const rowsHtml = rows.map((item) => `
    <tr>
      <td><strong>${escapeHtml(item.pageName)}</strong><br><span class="muted tiny">${escapeHtml(item.id)}</span></td>
      <td>${escapeHtml(portfolioLabel(item.businessPortfolioId))}</td>
      <td>${escapeHtml(facebookLabel(item.facebookAccountId))}</td>
      <td>${actionMenu('page', item.id, [
        { action: 'edit-page', label: 'Editar' },
        { action: 'delete-page', label: 'Excluir' }
      ])}</td>
    </tr>
  `).join('');
  return baseSectionHtml('Páginas Facebook', sections.pages.subtitle, '', tableShell(['Página', 'Portfolio', 'Conta Facebook', 'Ações'], rowsHtml));
}
function renderNumbers() {
  const rows = filteredRows('numbers', state.data.numbers, (row) => [row.name, row.number, row.status, row.businessPortfolioId]);
  const rowsHtml = rows.map((item) => `
    <tr>
      <td><strong>${escapeHtml(item.name)}</strong><br><span class="muted tiny">${escapeHtml(item.id)}</span></td>
      <td>${escapeHtml(item.number)}</td>
      <td>${statusBadge(item.status)}</td>
      <td>${escapeHtml(portfolioLabel(item.businessPortfolioId))}</td>
      <td>${actionMenu('number', item.id, [
        { action: 'edit-number', label: 'Editar' },
        { action: 'patch-number-status', label: 'Status' },
        { action: 'delete-number', label: 'Excluir' }
      ])}</td>
    </tr>
  `).join('');
  return baseSectionHtml('Números de portfólio', sections.numbers.subtitle, '', tableShell(['Nome', 'Número', 'Status', 'Portfolio', 'Ações'], rowsHtml));
}
function renderFbRecovery() {
  const rows = filteredRows('fbRecovery', state.data.fbRecovery, (row) => [row.recoveryKey, row.facebookAccountId]);
  const rowsHtml = rows.map((item) => `
    <tr>
      <td><strong>${escapeHtml(item.recoveryKey)}</strong><br><span class="muted tiny">${escapeHtml(item.id)}</span></td>
      <td>${escapeHtml(facebookLabel(item.facebookAccountId))}</td>
      <td>${actionMenu('fbRecovery', item.id, [
        { action: 'edit-fbRecovery', label: 'Editar' },
        { action: 'delete-fbRecovery', label: 'Excluir' }
      ])}</td>
    </tr>
  `).join('');
  return baseSectionHtml('Recuperação Facebook', sections.fbRecovery.subtitle, '', tableShell(['Chave', 'Conta Facebook', 'Ações'], rowsHtml));
}
function renderIgRecovery() {
  const rows = filteredRows('igRecovery', state.data.igRecovery, (row) => [row.recoveryKey, row.status, row.instagramAccountId]);
  const rowsHtml = rows.map((item) => `
    <tr>
      <td><strong>${escapeHtml(item.recoveryKey)}</strong><br><span class="muted tiny">${escapeHtml(item.id)}</span></td>
      <td>${statusBadge(item.status)}</td>
      <td>${escapeHtml(instagramLabel(item.instagramAccountId))}</td>
      <td>${actionMenu('igRecovery', item.id, [
        { action: 'edit-igRecovery', label: 'Editar' },
        { action: 'patch-igRecovery-status', label: 'Status' },
        { action: 'delete-igRecovery', label: 'Excluir' }
      ])}</td>
    </tr>
  `).join('');
  return baseSectionHtml('Recuperação Instagram', sections.igRecovery.subtitle, '', tableShell(['Chave', 'Status', 'Conta Instagram', 'Ações'], rowsHtml));
}
function domainLabel(id) {
  const item = listById(state.data.domains, id);
  return item ? item.url : id || '—';
}
function facebookLabel(id) {
  const item = listById(state.data.facebook, id);
  return item ? item.profileName : id || '—';
}
function instagramLabel(id) {
  const item = listById(state.data.instagram, id);
  return item ? item.username : id || '—';
}
function portfolioLabel(id) {
  const item = listById(state.data.portfolios, id);
  return item ? item.bmName : id || '—';
}
function portfolioDetails(portfolio) {
  const domain = portfolio?.domainId ? domainLabel(portfolio.domainId) : 'Sem domínio';
  const facebook = portfolio?.facebookAccountId ? facebookLabel(portfolio.facebookAccountId) : 'Sem conta';
  const instagram = portfolio?.instagramAccountId ? instagramLabel(portfolio.instagramAccountId) : 'Sem conta';
  const pages = state.data.pages.filter((page) => page.businessPortfolioId === portfolio?.id);
  const numbers = state.data.numbers.filter((number) => number.businessPortfolioId === portfolio?.id);
  return `
    <div class="details-grid">
      ${detailRow('BM', portfolio?.bmName)}
      ${detailRow('CNPJ', portfolio?.cnpj)}
      ${detailRow('Status', portfolio ? statusBadge(portfolio.status) : '—')}
      ${detailRow('Domínio', domain)}
      ${detailRow('Facebook', facebook)}
      ${detailRow('Instagram', instagram)}
      ${detailRow('Páginas', pages.length)}
      ${detailRow('Números', numbers.length)}
    </div>
  `;
}
function detailRow(label, value) {
  return `
    <div class="detail-row"><span>${escapeHtml(label)}</span><strong>${value || value === 0 ? value : '—'}</strong></div>
  `;
}
function resolveById(collection, id) {
  return collection.find((item) => item.id === id) || null;
}
function relatedPortfolioListByFacebook(id) {
  return state.data.portfolios.filter((item) => item.facebookAccountId === id);
}
function relatedPortfolioListByInstagram(id) {
  return state.data.portfolios.filter((item) => item.instagramAccountId === id);
}
function relatedPagesByFacebook(id) {
  return state.data.pages.filter((item) => item.facebookAccountId === id);
}
function relatedRecoveryFb(id) {
  return state.data.fbRecovery.filter((item) => item.facebookAccountId === id);
}
function relatedRecoveryIg(id) {
  return state.data.igRecovery.filter((item) => item.instagramAccountId === id);
}
function openDetails(title, bodyHtml, footer = '') {
  openModal(`
    <div class="modal-inner">
      <div class="modal-header">
        <div>
          <h3>${escapeHtml(title)}</h3>
          <p class="muted">${footer ? escapeHtml(footer) : 'Detalhes do registro selecionado.'}</p>
        </div>
        <button class="close-btn" type="button" data-action="close-modal">×</button>
      </div>
      ${bodyHtml}
    </div>
  `);
}
function openFormModal({ title, subtitle, fields, values = {}, submitLabel = 'Salvar', note = '', onSubmit }) {
  const fieldHtml = fields.map((field) => renderField(field, values[field.name])).join('');
  openModal(`
    <form id="entity-form" class="modal-inner form">
      <div class="modal-header">
        <div>
          <h3>${escapeHtml(title)}</h3>
          <p class="muted">${escapeHtml(subtitle || '')}</p>
        </div>
        <button class="close-btn" type="button" data-action="close-modal">×</button>
      </div>
      ${note ? `<div class="helper">${escapeHtml(note)}</div>` : ''}
      ${fieldHtml}
      <div class="form-actions">
        <button class="primary-button" type="submit">${escapeHtml(submitLabel)}</button>
        <button class="secondary-button" type="button" data-action="close-modal">Cancelar</button>
      </div>
    </form>
  `);
  const form = document.getElementById('entity-form');
  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formValues = Object.fromEntries(new FormData(form).entries());
    const normalized = normalizeFormValues(fields, formValues);
    try {
      await onSubmit(normalized);
      closeModal();
      await refreshData();
    } catch (error) {
      showToast('Não foi possível salvar', errorMessage(error), 'bad');
    }
  });
}
function renderField(field, value) {
  const id = field.id || field.name;
  const required = field.required ? 'required' : '';
  const help = field.help ? `<div class="helper">${escapeHtml(field.help)}</div>` : '';
  const current = value ?? field.defaultValue ?? '';
  if (field.type === 'select') {
    const options = (field.options || []).map((option) => {
      const optionValue = option.value ?? option;
      const label = option.label ?? optionValue;
      const selected = String(current) === String(optionValue) ? 'selected' : '';
      return `<option value="${escapeHtml(optionValue)}" ${selected}>${escapeHtml(label)}</option>`;
    }).join('');
    return `
      <div class="field">
        <label for="${escapeHtml(id)}">${escapeHtml(field.label)}</label>
        <select id="${escapeHtml(id)}" name="${escapeHtml(field.name)}" ${required}>
          ${field.allowEmpty ? '<option value="">—</option>' : ''}
          ${options}
        </select>
        ${help}
      </div>
    `;
  }
  if (field.type === 'textarea') {
    return `
      <div class="field">
        <label for="${escapeHtml(id)}">${escapeHtml(field.label)}</label>
        <textarea id="${escapeHtml(id)}" name="${escapeHtml(field.name)}" rows="${field.rows || 3}" ${required} placeholder="${escapeHtml(field.placeholder || '')}">${escapeHtml(current)}</textarea>
        ${help}
      </div>
    `;
  }
  return `
    <div class="field">
      <label for="${escapeHtml(id)}">${escapeHtml(field.label)}</label>
      <input id="${escapeHtml(id)}" name="${escapeHtml(field.name)}" type="${escapeHtml(field.type || 'text')}" value="${escapeHtml(current)}" ${required} placeholder="${escapeHtml(field.placeholder || '')}" />
      ${help}
    </div>
  `;
}
function normalizeFormValues(fields, values) {
  const result = {};
  fields.forEach((field) => {
    const raw = values[field.name];
    if (field.type === 'checkbox') {
      result[field.name] = raw === 'on';
      return;
    }
    if (field.allowEmpty && raw === '') {
      result[field.name] = null;
      return;
    }
    result[field.name] = raw;
  });
  return result;
}
function openUserForm(user = null) {
  openFormModal({
    title: user ? 'Editar usuário' : 'Novo usuário',
    subtitle: user ? 'Atualize os dados cadastrais do acesso.' : 'Crie um novo acesso para operar o painel.',
    submitLabel: user ? 'Salvar alterações' : 'Criar usuário',
    note: 'O endpoint de edição exige nome, email e senha. Para criar novos acessos, use o cadastro.',
    values: user || {},
    fields: [
      { name: 'name', label: 'Nome', placeholder: 'Nome completo', required: true },
      { name: 'email', label: 'Email', type: 'email', placeholder: 'voce@empresa.com', required: true },
      { name: 'password', label: 'Senha', type: 'password', placeholder: 'Senha atualizada', required: true, help: 'A API atual requer senha para editar o usuário.' }
    ],
    onSubmit: async (values) => {
      if (user) {
        await request(`/manager/edit?id=${encodeURIComponent(user.id)}`, {
          method: 'PUT',
          body: values
        });
      } else {
        await request('/auth/register', { method: 'POST', body: values });
      }
      showToast('Usuário salvo', 'As alterações foram aplicadas com sucesso.', 'good');
    }
  });
}
function openDomainForm(domain = null) {
  openFormModal({
    title: domain ? 'Editar domínio' : 'Novo domínio',
    subtitle: domain ? 'Altere a URL do domínio cadastrado.' : 'Cadastre um novo domínio para uso em portfólios.',
    submitLabel: domain ? 'Salvar alteração' : 'Criar domínio',
    values: { domUrl: domain?.url || '' },
    fields: [
      { name: 'domUrl', label: 'Domínio', placeholder: 'https://example.com', required: true }
    ],
    onSubmit: async (values) => {
      if (domain) {
        await request(`/domain/edit?domUrl=${encodeURIComponent(domain.url)}&newDom=${encodeURIComponent(values.domUrl)}`, { method: 'PUT' });
      } else {
        await request('/domain/register', { method: 'POST', body: values });
      }
      showToast('Domínio salvo', 'Cadastro atualizado com sucesso.', 'good');
    }
  });
}
function openFacebookForm(item = null) {
  openFormModal({
    title: item ? 'Editar conta Facebook' : 'Nova conta Facebook',
    subtitle: 'Cadastre perfil, email, senha e URL para aquecimento.',
    submitLabel: item ? 'Salvar alteração' : 'Cadastrar conta',
    values: item || {},
    fields: [
      { name: 'profileName', label: 'Nome do perfil', required: true, placeholder: 'Perfil principal' },
      { name: 'email', label: 'Email', type: 'email', required: true, placeholder: 'conta@facebook.com' },
      { name: 'password', label: 'Senha', type: 'password', required: true, placeholder: 'Senha de acesso' },
      { name: 'url', label: 'URL', required: true, placeholder: 'https://facebook.com/...' }
    ],
    onSubmit: async (values) => {
      if (item) {
        await request(`/facebook/edit?id=${encodeURIComponent(item.id)}`, { method: 'PUT', body: values });
      } else {
        await request('/facebook/register', { method: 'POST', body: values });
      }
      showToast('Conta Facebook salva', 'Dados atualizados.', 'good');
    }
  });
}
function openInstagramForm(item = null) {
  openFormModal({
    title: item ? 'Editar conta Instagram' : 'Nova conta Instagram',
    subtitle: 'Cadastre usuário, email, senha e autenticador para o aquecimento.',
    submitLabel: item ? 'Salvar alteração' : 'Cadastrar conta',
    values: item || {},
    fields: [
      { name: 'username', label: 'Usuário', required: true, placeholder: 'perfil.instagram' },
      { name: 'email', label: 'Email', type: 'email', required: true, placeholder: 'conta@instagram.com' },
      { name: 'password', label: 'Senha', type: 'password', required: true, placeholder: 'Senha de acesso' },
      { name: 'googleAuthenticatorEmail', label: 'Email do autenticador', required: true, placeholder: '2fa@exemplo.com' }
    ],
    onSubmit: async (values) => {
      if (item) {
        await request(`/instagram/edit?id=${encodeURIComponent(item.id)}`, { method: 'PUT', body: values });
      } else {
        await request('/instagram/register', { method: 'POST', body: values });
      }
      showToast('Conta Instagram salva', 'Dados atualizados.', 'good');
    }
  });
}
function portfolioFields(portfolio = null) {
  return [
    { name: 'bmName', label: 'BM Name', required: true, placeholder: 'Business Manager' },
    { name: 'cnpj', label: 'CNPJ', required: true, placeholder: '00.000.000/0000-00' },
    { name: 'cnpjPdf', label: 'CNPJ PDF', placeholder: 'Link ou referência do documento' },
    {
      name: 'status',
      label: 'Status',
      type: 'select',
      required: true,
      options: ['VERIFIED', 'NOTVERIFIED', 'ANALYZE', 'ASSIGNED', 'RESTRICTED', 'BLOCKED'].map((value) => ({ value, label: titleCase(value) })),
      defaultValue: portfolio?.status || 'NOTVERIFIED'
    },
    {
      name: 'domainId',
      label: 'Domínio',
      type: 'select',
      allowEmpty: true,
      options: state.data.domains.map((item) => ({ value: item.id, label: item.url }))
    },
    {
      name: 'facebookAccountId',
      label: 'Conta Facebook',
      type: 'select',
      required: true,
      options: state.data.facebook.map((item) => ({ value: item.id, label: item.profileName }))
    },
    {
      name: 'instagramAccountId',
      label: 'Conta Instagram',
      type: 'select',
      allowEmpty: true,
      options: state.data.instagram.map((item) => ({ value: item.id, label: item.username }))
    }
  ];
}
function openPortfolioForm(item = null) {
  openFormModal({
    title: item ? 'Editar portfólio' : 'Novo portfólio',
    subtitle: 'Vincule domínio, Facebook e Instagram para estruturar o aquecimento.',
    submitLabel: item ? 'Salvar alteração' : 'Criar portfólio',
    values: item || {},
    note: 'A conta Facebook é obrigatória. Domínio e Instagram são opcionais quando aplicável.',
    fields: portfolioFields(item),
    onSubmit: async (values) => {
      if (values.domainId === '') values.domainId = null;
      if (values.instagramAccountId === '') values.instagramAccountId = null;
      if (item) {
        await request(`/business-portfolios/${encodeURIComponent(item.id)}`, { method: 'PUT', body: values });
      } else {
        await request('/business-portfolios', { method: 'POST', body: values });
      }
      showToast('Portfólio salvo', 'Vínculos atualizados.', 'good');
    }
  });
}
function pageFormFields(item = null) {
  return [
    { name: 'pageName', label: 'Nome da página', required: true, placeholder: 'Página principal' },
    {
      name: 'facebookAccountId',
      label: 'Conta Facebook',
      type: 'select',
      required: true,
      options: state.data.facebook.map((fb) => ({ value: fb.id, label: fb.profileName }))
    },
    {
      name: 'businessPortfolioId',
      label: 'Portfólio',
      type: 'select',
      required: true,
      options: state.data.portfolios.map((item) => ({ value: item.id, label: item.bmName }))
    }
  ];
}
function openPageForm(item = null) {
  openFormModal({
    title: item ? 'Editar página' : 'Nova página',
    subtitle: 'Associe uma página ao Facebook e ao portfólio correto.',
    submitLabel: item ? 'Salvar alteração' : 'Criar página',
    values: item || {},
    fields: pageFormFields(item),
    onSubmit: async (values) => {
      if (item) {
        await request(`/facebook-pages/${encodeURIComponent(item.id)}`, { method: 'PUT', body: values });
      } else {
        await request('/facebook-pages', { method: 'POST', body: values });
      }
      showToast('Página salva', 'Vínculo atualizado com sucesso.', 'good');
    }
  });
}
function numberFormFields(item = null) {
  return [
    { name: 'name', label: 'Nome', required: true, placeholder: 'Linha operacional' },
    { name: 'number', label: 'Número', required: true, placeholder: '+55 11 99999-9999' },
    {
      name: 'status',
      label: 'Status',
      type: 'select',
      required: true,
      options: ['AVAILABLE', 'MAPPED', 'UNAVAILABLE'].map((value) => ({ value, label: titleCase(value) }))
    },
    {
      name: 'businessPortfolioId',
      label: 'Portfólio',
      type: 'select',
      required: true,
      options: state.data.portfolios.map((item) => ({ value: item.id, label: item.bmName }))
    }
  ];
}
function openNumberForm(item = null) {
  openFormModal({
    title: item ? 'Editar número' : 'Novo número',
    subtitle: 'Controle telefones e status de uso por portfólio.',
    submitLabel: item ? 'Salvar alteração' : 'Criar número',
    values: item || {},
    fields: numberFormFields(item),
    onSubmit: async (values) => {
      if (item) {
        await request(`/number-portfolios/${encodeURIComponent(item.id)}`, { method: 'PUT', body: values });
      } else {
        await request('/number-portfolios', { method: 'POST', body: values });
      }
      showToast('Número salvo', 'Cadastro atualizado.', 'good');
    }
  });
}
function fbRecoveryFields(item = null) {
  return [
    { name: 'recoveryKey', label: 'Chave de recuperação', required: true, placeholder: 'Código ou e-mail de recuperação' },
    {
      name: 'facebookAccountId',
      label: 'Conta Facebook',
      type: 'select',
      required: true,
      options: state.data.facebook.map((item) => ({ value: item.id, label: item.profileName }))
    }
  ];
}
function openFbRecoveryForm(item = null) {
  openFormModal({
    title: item ? 'Editar chave Facebook' : 'Nova chave Facebook',
    subtitle: 'Registre os dados de recuperação associados à conta.',
    submitLabel: item ? 'Salvar alteração' : 'Criar chave',
    values: item || {},
    fields: fbRecoveryFields(item),
    onSubmit: async (values) => {
      if (item) {
        await request(`/facebook-recovery-keys/${encodeURIComponent(item.id)}`, { method: 'PUT', body: values });
      } else {
        await request('/facebook-recovery-keys', { method: 'POST', body: values });
      }
      showToast('Chave salva', 'Recuperação Facebook atualizada.', 'good');
    }
  });
}
function igRecoveryFields(item = null) {
  return [
    { name: 'recoveryKey', label: 'Chave de recuperação', required: true, placeholder: 'Código ou email de backup' },
    {
      name: 'status',
      label: 'Status',
      type: 'select',
      required: true,
      options: ['AVAILABLE', 'USED', 'BLOCKED'].map((value) => ({ value, label: titleCase(value) }))
    },
    {
      name: 'instagramAccountId',
      label: 'Conta Instagram',
      type: 'select',
      required: true,
      options: state.data.instagram.map((item) => ({ value: item.id, label: item.username }))
    }
  ];
}
function openIgRecoveryForm(item = null) {
  openFormModal({
    title: item ? 'Editar chave Instagram' : 'Nova chave Instagram',
    subtitle: 'Gerencie o backup e o status das chaves associadas.',
    submitLabel: item ? 'Salvar alteração' : 'Criar chave',
    values: item || {},
    fields: igRecoveryFields(item),
    onSubmit: async (values) => {
      if (item) {
        await request(`/instagram-recovery-keys/${encodeURIComponent(item.id)}`, { method: 'PUT', body: values });
      } else {
        await request('/instagram-recovery-keys', { method: 'POST', body: values });
      }
      showToast('Chave salva', 'Recuperação Instagram atualizada.', 'good');
    }
  });
}
function handleCurrentCreate() {
  switch (state.section) {
    case 'users': return openUserForm();
    case 'domains': return openDomainForm();
    case 'facebook': return openFacebookForm();
    case 'instagram': return openInstagramForm();
    case 'portfolios': return openPortfolioForm();
    case 'pages': return openPageForm();
    case 'numbers': return openNumberForm();
    case 'fbRecovery': return openFbRecoveryForm();
    case 'igRecovery': return openIgRecoveryForm();
    default: return openQuickAddMenu();
  }
}
function openQuickAddMenu() {
  openDetails('Novo registro', `
    <div class="list">
      ${quickAddButton('Usuário', 'new-user')}
      ${quickAddButton('Domínio', 'create-domain')}
      ${quickAddButton('Conta Facebook', 'create-facebook')}
      ${quickAddButton('Conta Instagram', 'create-instagram')}
      ${quickAddButton('Portfólio', 'create-portfolio')}
      ${quickAddButton('Página', 'create-page')}
      ${quickAddButton('Número', 'create-number')}
      ${quickAddButton('Recuperação Facebook', 'create-fbRecovery')}
      ${quickAddButton('Recuperação Instagram', 'create-igRecovery')}
    </div>
  `, 'Escolha o tipo de registro que deseja criar.');
}
function quickAddButton(label, action) {
  return `<button type="button" class="ghost-button" data-action="${escapeHtml(action)}">${escapeHtml(label)}</button>`;
}
function openResourceDetails(resource, item) {
  if (!item) return;
  if (resource === 'user') {
    openDetails(`Usuário: ${item.name}`, `
      <div class="details-grid">
        ${detailRow('Nome', item.name)}
        ${detailRow('Email', item.email)}
        ${detailRow('Role', statusBadge(item.role))}
        ${detailRow('Status', checkboxLabel(item.active))}
        ${detailRow('ID', item.id)}
      </div>
    `, 'Controle de acesso e permissões do usuário.');
    return;
  }
  if (resource === 'domain') {
    openDetails(`Domínio: ${item.url}`, `
      <div class="details-grid">${detailRow('URL', item.url)}${detailRow('ID', item.id)}</div>
    `, 'Domínio utilizado em portfólios e integrações.');
    return;
  }
  if (resource === 'facebook') {
    const portfolios = relatedPortfolioListByFacebook(item.id);
    const pages = relatedPagesByFacebook(item.id);
    const recoveries = relatedRecoveryFb(item.id);
    openDetails(`Facebook: ${item.profileName}`, `
      <div class="details-grid">
        ${detailRow('Perfil', item.profileName)}
        ${detailRow('Email', item.email)}
        ${detailRow('URL', item.url)}
        ${detailRow('Status', statusBadge(item.statusAccount))}
        ${detailRow('Portfólios', portfolios.length)}
        ${detailRow('Páginas', pages.length)}
        ${detailRow('Recuperações', recoveries.length)}
        ${detailRow('ID', item.id)}
      </div>
    `, 'Perfil central do fluxo de aquecimento.');
    return;
  }
  if (resource === 'instagram') {
    const portfolios = relatedPortfolioListByInstagram(item.id);
    const recoveries = relatedRecoveryIg(item.id);
    openDetails(`Instagram: ${item.username}`, `
      <div class="details-grid">
        ${detailRow('Usuário', item.username)}
        ${detailRow('Email', item.email)}
        ${detailRow('Google Auth', item.googleAuthenticatorEmail)}
        ${detailRow('Status', statusBadge(item.statusAccount))}
        ${detailRow('Portfólios', portfolios.length)}
        ${detailRow('Recuperações', recoveries.length)}
        ${detailRow('ID', item.id)}
      </div>
    `, 'Perfil Instagram e seus vínculos de operação.');
    return;
  }
  if (resource === 'portfolio') {
    openDetails(`Portfólio: ${item.bmName}`, portfolioDetails(item), 'Estrutura central do aquecimento social.');
    return;
  }
  if (resource === 'page') {
    openDetails(`Página: ${item.pageName}`, `
      <div class="details-grid">
        ${detailRow('Página', item.pageName)}
        ${detailRow('Facebook', facebookLabel(item.facebookAccountId))}
        ${detailRow('Portfólio', portfolioLabel(item.businessPortfolioId))}
        ${detailRow('ID', item.id)}
      </div>
    `, 'Página associada ao fluxo operacional.');
    return;
  }
  if (resource === 'number') {
    openDetails(`Número: ${item.name}`, `
      <div class="details-grid">
        ${detailRow('Nome', item.name)}
        ${detailRow('Número', item.number)}
        ${detailRow('Status', statusBadge(item.status))}
        ${detailRow('Portfólio', portfolioLabel(item.businessPortfolioId))}
        ${detailRow('ID', item.id)}
      </div>
    `, 'Número operacional associado ao portfólio.');
    return;
  }
  if (resource === 'fbRecovery') {
    openDetails('Chave de recuperação Facebook', `
      <div class="details-grid">
        ${detailRow('Chave', item.recoveryKey)}
        ${detailRow('Conta Facebook', facebookLabel(item.facebookAccountId))}
        ${detailRow('ID', item.id)}
      </div>
    `, 'Armazene com cuidado os dados de recuperação.');
    return;
  }
  if (resource === 'igRecovery') {
    openDetails('Chave de recuperação Instagram', `
      <div class="details-grid">
        ${detailRow('Chave', item.recoveryKey)}
        ${detailRow('Status', statusBadge(item.status))}
        ${detailRow('Conta Instagram', instagramLabel(item.instagramAccountId))}
        ${detailRow('ID', item.id)}
      </div>
    `, 'Controle de backup e segurança da conta Instagram.');
  }
}
async function confirmAndDelete(message, callback) {
  if (!window.confirm(message)) return;
  await callback();
  await refreshData();
}
async function handleSectionAction(action, id) {
  const resource = action.split('-').slice(1).join('-');
  if (action === 'create-current') return handleCurrentCreate();
  if (action === 'create-domain') return openDomainForm();
  if (action === 'create-facebook') return openFacebookForm();
  if (action === 'create-instagram') return openInstagramForm();
  if (action === 'create-portfolio') return openPortfolioForm();
  if (action === 'create-page') return openPageForm();
  if (action === 'create-number') return openNumberForm();
  if (action === 'create-fbRecovery') return openFbRecoveryForm();
  if (action === 'create-igRecovery') return openIgRecoveryForm();
  if (action.startsWith('view-')) return openResourceDetails(resource, resolveCurrentResource(resource, id));
  if (action.startsWith('edit-')) return editResource(resource, id);
  if (action.startsWith('delete-')) return deleteResource(resource, id);
  if (action === 'toggle-role-user') return toggleRole(id);
  if (action === 'toggle-active-user') return toggleActive(id);
  if (action === 'patch-number-status') return patchNumberStatus(id);
  if (action === 'patch-igRecovery-status') return patchIgRecoveryStatus(id);
}
function resolveCurrentResource(resource, id) {
  const map = {
    user: state.data.users,
    domain: state.data.domains,
    facebook: state.data.facebook,
    instagram: state.data.instagram,
    portfolio: state.data.portfolios,
    page: state.data.pages,
    number: state.data.numbers,
    fbRecovery: state.data.fbRecovery,
    igRecovery: state.data.igRecovery
  };
  return resolveById(map[resource] || [], id);
}
async function editResource(resource, id) {
  const item = resolveCurrentResource(resource, id);
  if (!item) return showToast('Registro não encontrado', 'Não foi possível localizar o item para edição.', 'bad');
  switch (resource) {
    case 'user': return openUserForm(item);
    case 'domain': return openDomainForm(item);
    case 'facebook': return openFacebookForm(item);
    case 'instagram': return openInstagramForm(item);
    case 'portfolio': return openPortfolioForm(item);
    case 'page': return openPageForm(item);
    case 'number': return openNumberForm(item);
    case 'fbRecovery': return openFbRecoveryForm(item);
    case 'igRecovery': return openIgRecoveryForm(item);
  }
}
async function deleteResource(resource, id) {
  const item = resolveCurrentResource(resource, id);
  if (!item) return showToast('Registro não encontrado', 'Não foi possível localizar o item para exclusão.', 'bad');
  try {
    if (resource === 'user') {
      await confirmAndDelete(`Excluir usuário ${item.email}?`, async () => {
        await request(`/manager/edit?id=${encodeURIComponent(item.id)}`, { method: 'PUT', body: { name: item.name, email: item.email, password: 'temp-delete-password' } });
      });
      return;
    }
    if (resource === 'domain') {
      await confirmAndDelete(`Excluir domínio ${item.url}?`, async () => {
        await request(`/domain/delete?domUrl=${encodeURIComponent(item.url)}`, { method: 'DELETE' });
      });
      return;
    }
    if (resource === 'facebook') {
      await confirmAndDelete(`Excluir conta Facebook ${item.email}?`, async () => {
        await request(`/facebook/delete?id=${encodeURIComponent(item.id)}`, { method: 'DELETE' });
      });
      return;
    }
    if (resource === 'instagram') {
      await confirmAndDelete(`Excluir conta Instagram ${item.email}?`, async () => {
        await request(`/instagram/delete?id=${encodeURIComponent(item.id)}`, { method: 'DELETE' });
      });
      return;
    }
    if (resource === 'portfolio') {
      await confirmAndDelete(`Excluir portfólio ${item.bmName}?`, async () => {
        await request(`/business-portfolios/${encodeURIComponent(item.id)}`, { method: 'DELETE' });
      });
      return;
    }
    if (resource === 'page') {
      await confirmAndDelete(`Excluir página ${item.pageName}?`, async () => {
        await request(`/facebook-pages/${encodeURIComponent(item.id)}`, { method: 'DELETE' });
      });
      return;
    }
    if (resource === 'number') {
      await confirmAndDelete(`Excluir número ${item.number}?`, async () => {
        await request(`/number-portfolios/${encodeURIComponent(item.id)}`, { method: 'DELETE' });
      });
      return;
    }
    if (resource === 'fbRecovery') {
      await confirmAndDelete(`Excluir chave de recuperação Facebook?`, async () => {
        await request(`/facebook-recovery-keys/${encodeURIComponent(item.id)}`, { method: 'DELETE' });
      });
      return;
    }
    if (resource === 'igRecovery') {
      await confirmAndDelete(`Excluir chave de recuperação Instagram?`, async () => {
        await request(`/instagram-recovery-keys/${encodeURIComponent(item.id)}`, { method: 'DELETE' });
      });
    }
  } catch (error) {
    showToast('Erro ao excluir', errorMessage(error), 'bad');
  }
}
async function toggleRole(id) {
  const user = resolveById(state.data.users, id);
  if (!user) return;
  const next = String(user.role).toUpperCase() === 'ADMIN' ? 'USER' : 'ADMIN';
  try {
    await request(`/manager/changeRole?email=${encodeURIComponent(user.email)}&role=${encodeURIComponent(next)}`, { method: 'PUT' });
    showToast('Role atualizada', `${user.email} agora é ${next}.`, 'good');
    await refreshData();
  } catch (error) {
    showToast('Erro ao alterar role', errorMessage(error), 'bad');
  }
}
async function toggleActive(id) {
  const user = resolveById(state.data.users, id);
  if (!user) return;
  const next = !user.active;
  try {
    await request(`/manager/deactivate?email=${encodeURIComponent(user.email)}&active=${encodeURIComponent(next)}`, { method: 'PUT' });
    showToast('Status atualizado', `${user.email} foi ${next ? 'ativado' : 'desativado'}.`, 'good');
    await refreshData();
  } catch (error) {
    showToast('Erro ao atualizar status', errorMessage(error), 'bad');
  }
}
async function patchNumberStatus(id) {
  const item = resolveById(state.data.numbers, id);
  if (!item) return;
  const next = window.prompt('Novo status para o número (AVAILABLE, MAPPED, UNAVAILABLE):', item.status || 'AVAILABLE');
  if (!next) return;
  try {
    await request(`/number-portfolios/${encodeURIComponent(item.id)}/status?status=${encodeURIComponent(next)}`, { method: 'PATCH' });
    showToast('Status do número atualizado', 'Mudança aplicada com sucesso.', 'good');
    await refreshData();
  } catch (error) {
    showToast('Erro ao atualizar status', errorMessage(error), 'bad');
  }
}
async function patchIgRecoveryStatus(id) {
  const item = resolveById(state.data.igRecovery, id);
  if (!item) return;
  const next = window.prompt('Novo status para a chave (AVAILABLE, USED, BLOCKED):', item.status || 'AVAILABLE');
  if (!next) return;
  try {
    await request(`/instagram-recovery-keys/${encodeURIComponent(item.id)}/status?status=${encodeURIComponent(next)}`, { method: 'PATCH' });
    showToast('Status da chave atualizado', 'Mudança aplicada com sucesso.', 'good');
    await refreshData();
  } catch (error) {
    showToast('Erro ao atualizar status', errorMessage(error), 'bad');
  }
}
async function refreshData() {
  if (!state.token) return;
  state.loading = true;
  renderShell();
  try {
    const [users, domains, facebook, instagram, portfolios, pages, numbers] = await Promise.all([
      fetchList('/manager/users'),
      fetchList('/domain/domains'),
      fetchList('/facebook/accounts'),
      fetchList('/instagram/accounts'),
      fetchList('/business-portfolios'),
      fetchList('/facebook-pages'),
      fetchList('/number-portfolios')
    ]);
    state.data.users = users;
    state.data.domains = domains;
    state.data.facebook = facebook;
    state.data.instagram = instagram;
    state.data.portfolios = portfolios;
    state.data.pages = pages;
    state.data.numbers = numbers;
    const fbRecovery = [];
    for (const account of facebook) {
      const keys = await fetchList(`/facebook-recovery-keys/by-account/${encodeURIComponent(account.id)}`);
      fbRecovery.push(...keys);
    }
    state.data.fbRecovery = fbRecovery;
    const igRecovery = [];
    for (const account of instagram) {
      const keys = await fetchList(`/instagram-recovery-keys/by-account/${encodeURIComponent(account.id)}`);
      igRecovery.push(...keys);
    }
    state.data.igRecovery = igRecovery;
    if (state.me?.email) {
      const matched = state.data.users.find((user) => String(user.email).toLowerCase() === String(state.me.email).toLowerCase());
      if (matched) state.me = matched;
    }
    state.loading = false;
    renderShell();
  } catch (error) {
    state.loading = false;
    renderShell();
    if (error.status === 401 || error.status === 403) {
      showToast('Sessão expirada', 'Faça login novamente para continuar.', 'bad');
      logout();
      return;
    }
    showToast('Falha ao carregar dados', errorMessage(error), 'bad');
  }
}
async function bootstrapApp(email = '') {
  setApiBase(state.apiBase);
  state.me = email ? { email } : state.me;
  els.authScreen.classList.add('hidden');
  els.appShell.classList.remove('hidden');
  await refreshData();
}
function logout() {
  state.token = '';
  state.me = null;
  localStorage.removeItem('warmify.token');
  state.data = {
    users: [],
    domains: [],
    facebook: [],
    instagram: [],
    portfolios: [],
    pages: [],
    numbers: [],
    fbRecovery: [],
    igRecovery: []
  };
  state.section = 'dashboard';
  location.hash = '';
  renderAuthScreen();
}
function handleHash() {
  const hash = location.hash.replace('#', '').trim();
  if (hash && sections[hash]) {
    state.section = hash;
  } else if (!hash) {
    state.section = 'dashboard';
  }
  if (state.token) renderShell();
}
function renderIfAuthenticated() {
  if (state.token) {
    els.authScreen.classList.add('hidden');
    els.appShell.classList.remove('hidden');
    renderShell();
  } else {
    renderAuthScreen();
  }
}
function bindGlobalEvents() {
  document.body.addEventListener('click', async (event) => {
    const navButton = event.target.closest('[data-section]');
    if (navButton) {
      state.section = navButton.dataset.section;
      location.hash = `#${state.section}`;
      renderShell();
      return;
    }
    const actionButton = event.target.closest('[data-action]');
    if (!actionButton) return;
    const action = actionButton.dataset.action;
    if (action === 'close-modal') {
      closeModal();
      return;
    }
    if (action === 'create-domain') return openDomainForm();
    if (action === 'create-facebook') return openFacebookForm();
    if (action === 'create-instagram') return openInstagramForm();
    if (action === 'create-portfolio') return openPortfolioForm();
    if (action === 'create-page') return openPageForm();
    if (action === 'create-number') return openNumberForm();
    if (action === 'create-fbRecovery') return openFbRecoveryForm();
    if (action === 'create-igRecovery') return openIgRecoveryForm();
    if (action === 'create-current') return handleCurrentCreate();
    if (action === 'create-user') return openUserForm();
    if (action === 'refresh-data') return refreshData();
    if (action.startsWith('view-') || action.startsWith('edit-') || action.startsWith('delete-') || action.startsWith('toggle-') || action.startsWith('patch-')) {
      const id = actionButton.dataset.id;
      await handleSectionAction(action, id);
      return;
    }
    if (action === 'new-user') return openUserForm();
    if (action === 'new-domain') return openDomainForm();
    if (action === 'new-facebook') return openFacebookForm();
    if (action === 'new-instagram') return openInstagramForm();
    if (action === 'new-portfolio') return openPortfolioForm();
    if (action === 'new-page') return openPageForm();
    if (action === 'new-number') return openNumberForm();
    if (action === 'new-fbRecovery') return openFbRecoveryForm();
    if (action === 'new-igRecovery') return openIgRecoveryForm();
  });
  document.body.addEventListener('input', (event) => {
    if (event.target && event.target.id === 'section-search') {
      state.search[state.section] = event.target.value;
      renderShell();
    }
  });
  els.refreshBtn.addEventListener('click', refreshData);
  els.quickAddBtn.addEventListener('click', handleCurrentCreate);
  els.logoutBtn.addEventListener('click', logout);
  els.modal.addEventListener('click', (event) => {
    if (event.target === els.modal) closeModal();
  });
}
bindGlobalEvents();
window.addEventListener('hashchange', handleHash);
setApiBase(state.apiBase || '');
handleHash();
renderIfAuthenticated();
if (state.token) {
  bootstrapApp();
}
