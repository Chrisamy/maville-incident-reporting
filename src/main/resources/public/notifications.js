/* notifications.js
   Small client-side notification UI for demo.
   - Renders notifications per wrapper
   - Toggles dropdown and positions it
   - Simple local storage persistence
*/

function _timeAgo(ts) {
  const s = Math.floor((Date.now() - ts) / 1000);
  if (s < 60) return `${s}s`;
  if (s < 3600) return `${Math.floor(s / 60)}m`;
  if (s < 86400) return `${Math.floor(s / 3600)}h`;
  return `${Math.floor(s / 86400)}j`;
}

const NotificationStore = (function () {
  const KEY = 'maville_notifications_v1';
  function _load() {
    try { return JSON.parse(localStorage.getItem(KEY) || '[]'); } catch (e) { return []; }
  }
  function _save(items) { localStorage.setItem(KEY, JSON.stringify(items)); }
  function list(role) { return _load().filter(i => i.role === role).sort((a,b)=>b.time-a.time); }
  function add(role, item) {
    const items = _load();
    item.id = item.id || ('n_' + Date.now() + '_' + Math.floor(Math.random()*1000));
    item.role = role;
    item.time = item.time || Date.now();
    item.read = !!item.read;
    items.push(item);
    _save(items);
    return item;
  }
  function markAllRead(role) {
    const items = _load().map(i => i.role === role ? ({...i, read: true}) : i);
    _save(items);
  }
  function markRead(role, id) {
    const items = _load().map(i => (i.role===role && i.id===id) ? ({...i, read:true}) : i);
    _save(items);
  }
  function unreadCount(role) { return _load().filter(i => i.role === role && !i.read).length; }
  function ensureSeed(role) {
    const items = _load();
    if (items.some(i => i.role === role)) return;
    add(role, { title: 'Requête traitée', text: 'Votre requête #683... a été assignée à un prestataire.', time: Date.now()-3600000, read:false, type:'success' });
    add(role, { title: 'Nouvelle mise à jour', text: 'Des travaux sont prévus dans votre arrondissement.', time: Date.now()-7200000, read:false, type:'info' });
    add(role, { title: 'Action requise', text: 'Veuillez compléter les informations manquantes.', time: Date.now()-86400000, read:false, type:'warn' });
  }
  return { list, add, markAllRead, markRead, unreadCount, ensureSeed };
})();

function _renderItem(item, role, onClickMarkRead) {
  const li = document.createElement('li');
  li.className = 'notif-item' + (item.read ? '' : ' unread') + ` type-${item.type || 'info'}`;
  li.dataset.id = item.id;

  const icon = document.createElement('div'); icon.className = 'notif-icon';
  icon.textContent = item.type === 'success' ? '✔' : (item.type === 'warn' ? '!' : 'i');

  const meta = document.createElement('div'); meta.className = 'notif-meta';
  const title = document.createElement('div'); title.className = 'notif-title'; title.textContent = item.title;
  const desc = document.createElement('div'); desc.className = 'notif-desc'; desc.textContent = item.text;
  const time = document.createElement('div'); time.className = 'notif-time'; time.textContent = 'Il y a ' + _timeAgo(item.time);

  meta.appendChild(title); meta.appendChild(desc); meta.appendChild(time);
  li.appendChild(icon); li.appendChild(meta);

  li.addEventListener('click', () => {
    NotificationStore.markRead(role, item.id);
    li.classList.remove('unread');
    if (typeof onClickMarkRead === 'function') onClickMarkRead();
  });

  return li;
}

function _positionDropdown(btn, dd) {
  const rect = btn.getBoundingClientRect();
  const top = Math.max(rect.bottom + 8, 8);
  const right = Math.max(window.innerWidth - rect.right + 8, 8);
  dd.style.position = 'fixed';
  dd.style.top = `${top}px`;
  dd.style.right = `${right}px`;
}

function initNotificationsPerWrapper(wrapper) {
  const btn = wrapper.querySelector('.notif-btn');
  const dd = wrapper.querySelector('.notif-dropdown');
  const listEl = wrapper.querySelector('.notif-list');
  const emptyEl = wrapper.querySelector('#notifEmpty') || wrapper.querySelector('.notif-empty');
  const badge = wrapper.querySelector('.notif-badge');
  const role = wrapper.dataset.role || wrapper.getAttribute('data-role') || window.PAGE_ROLE || 'resident';

  NotificationStore.ensureSeed(role);

  function refreshList() {
    const items = NotificationStore.list(role);
    listEl.innerHTML = '';
    if (!items || items.length === 0) {
      if (emptyEl) emptyEl.style.display = 'block';
      if (badge) badge.textContent = '0';
      return;
    }
    if (emptyEl) emptyEl.style.display = 'none';
    items.forEach(item => listEl.appendChild(_renderItem(item, role, updateBadge)));
    updateBadge();
  }

  function updateBadge() {
    const count = NotificationStore.unreadCount(role);
    if (!badge) return;
    badge.textContent = String(count || 0);
    badge.style.display = count ? 'inline-flex' : '';
  }

  function showDropdown() { dd.classList.add('show'); dd.setAttribute('aria-hidden','false'); btn.setAttribute('aria-expanded','true'); _positionDropdown(btn, dd); refreshList(); }
  function hideDropdown() { dd.classList.remove('show'); dd.setAttribute('aria-hidden','true'); btn.setAttribute('aria-expanded','false'); }

  btn.addEventListener('click', (e) => { e.stopPropagation(); if (dd.classList.contains('show')) hideDropdown(); else showDropdown(); });

  const markAll = wrapper.querySelector('[data-action="mark-all"]');
  if (markAll) markAll.addEventListener('click', (e) => { e.stopPropagation(); NotificationStore.markAllRead(role); refreshList(); });

  document.addEventListener('pointerdown', (e) => { if (!wrapper.contains(e.target) && dd.classList.contains('show')) hideDropdown(); });
  window.addEventListener('resize', () => { if (dd.classList.contains('show')) _positionDropdown(btn, dd); });

  refreshList();
}

function initAllNotifications() {
  const wrappers = document.querySelectorAll('.notif-wrapper');
  wrappers.forEach(w => { if (w.dataset._notifInit) return; w.dataset._notifInit = '1'; initNotificationsPerWrapper(w); });
}

if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', initAllNotifications); else initAllNotifications();

