function _timeAgo(ts) {
  const s = Math.floor((Date.now() - ts) / 1000);
  if (s < 60) return `${s}s`;
  if (s < 3600) return `${Math.floor(s / 60)}m`;
  if (s < 86400) return `${Math.floor(s / 3600)}h`;
  return `${Math.floor(s / 864000)}j`;
}

// Format timestamp for display according to locale
function formatTimestamp(ts) {
  let d;
  if (typeof ts === 'number') d = new Date(ts);
  else if (typeof ts === 'string') {
    const parsed = Date.parse(ts);
    d = isNaN(parsed) ? new Date(ts) : new Date(parsed);
  } else {
    d = new Date(ts);
  }
  if (!(d instanceof Date) || isNaN(d.getTime())) return '';

  const pad = (n) => String(n).padStart(2, '0');
  const today = new Date();
  const cmp = (a, b) => a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();

  const hours = pad(d.getHours());
  const minutes = pad(d.getMinutes());
  if (cmp(d, today)) {
    return `Aujourd\u2019hui à ${hours}:${minutes}`; // Aujourd'hui
  }
  const yesterday = new Date();
  yesterday.setDate(today.getDate() - 1);
  if (cmp(d, yesterday)) {
    return `Hier à ${hours}:${minutes}`;
  }
  // fallback full date dd/mm/yyyy hh:mm
  const day = pad(d.getDate());
  const month = pad(d.getMonth() + 1);
  const year = d.getFullYear();
  return `${day}/${month}/${year} ${hours}:${minutes}`;
}

const NotificationStore = (function () {
  const KEY = 'maville_notifications_v1';
  // simple in-memory cache to avoid repeated fetches
  const cache = {};

  function _load() {
    try { return JSON.parse(localStorage.getItem(KEY) || '[]'); } catch (e) { return []; }
  }
  function _save(items) { localStorage.setItem(KEY, JSON.stringify(items)); }

  // map server notification shape to client shape
  function _mapServer(n) {
    let time = Date.now();
    if (typeof n.time === 'number') time = n.time;
    else if (typeof n.timestamp === 'string') {
      const parsed = Date.parse(n.timestamp);
      if (!isNaN(parsed)) time = parsed;
    }
    return {
      id: n.id,
      role: n.role,
      title: n.title,
      text: n.message || n.text || '',
      time: time,
      read: !!n.read,
      type: n.type || 'info'
    };
  }

  function list(role) {
    // return cached/local list synchronously for compatibility with existing UI code
    return _load().filter(i => i.role === role).sort((a, b) => b.time - a.time);
  }

  function add(role, item) {
    //keep existing local behaviour for demos. This doesn't push to server
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
    // try to mark on server too
    const toMark = items.filter(i => i.role===role).map(i=>i.id);
    toMark.forEach(id => {
      // best-effort, ignore failures
      fetch(`/notifications/mark-read/${id}`, {method: 'POST'}).catch(()=>{});
    });
  }

  function markRead(role, id) {
    const items = _load().map(i => (i.role===role && i.id===id) ? ({...i, read:true}) : i);
    _save(items);
    // mark on server (best-effort)
    fetch(`/notifications/mark-read/${id}`, {method: 'POST'}).catch(()=>{});
  }

  function unreadCount(role) { return _load().filter(i => i.role === role && !i.read).length; }

  function ensureSeed(role) {
    const items = _load();
    if (items.some(i => i.role === role)) return;
    add(role, { title: 'Requête traitée', text: 'Votre requête #683... a été assignée à un prestataire.', time: Date.now()-3600000, read:false, type:'success' });
    add(role, { title: 'Nouvelle mise à jour', text: 'Des travaux sont prévus dans votre arrondissement.', time: Date.now()-7200000, read:false, type:'info' });
    add(role, { title: 'Action requise', text: 'Veuillez compléter les informations manquantes.', time: Date.now()-86400000, read:false, type:'warn' });
  }

  // fetch notifications from backend and store in Storage
  function syncFromServer(role, cb) {
    fetch(`/notifications?role=${encodeURIComponent(role)}`, { credentials: 'same-origin' })
      .then(r => { if (!r.ok) throw new Error('network'); return r.json(); })
      .then(data => {
        // data expected to be an array of server Notification objects
        const existingAll = _load();
        // build map of existing items by id to preserve local read states and keep local-only items
        const existingMap = {};
        existingAll.forEach(i => { if (i && i.id) existingMap[i.id] = i; });

        const mapped = (Array.isArray(data) ? data : []).map(_mapServer).map(m => ({...m, role: role}));

        // overlay server items onto existing map, preserving local read flag when present
        mapped.forEach(m => {
          const local = existingMap[m.id];
          if (local) {
            existingMap[m.id] = { ...m, role: role, read: !!local.read, time: local.time || m.time };
          } else {
            existingMap[m.id] = m;
          }
        });

        // final list contains all existing (others + local-only) plus server items (merged)
        const finalList = Object.keys(existingMap).map(k => existingMap[k]);

        _save(finalList);
        cache[role] = mapped;
        if (typeof cb === 'function') cb();
      })
      .catch(() => {
        // ignore failures; leave local storage intact
        if (typeof cb === 'function') cb();
      });
  }

  return { list, add, markAllRead, markRead, unreadCount, ensureSeed, syncFromServer };
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
  const time = document.createElement('div'); time.className = 'notif-time'; time.textContent = formatTimestamp(item.time);

  meta.appendChild(title); meta.appendChild(desc); meta.appendChild(time);
  li.appendChild(icon); li.appendChild(meta);

  li.addEventListener('click', () => {
    // mark locally and ask server to mark read
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

  function showDropdown() { dd.classList.add('show'); dd.setAttribute('aria-hidden','false'); btn.setAttribute('aria-expanded','true'); _positionDropdown(btn, dd); NotificationStore.syncFromServer(role, refreshList); }
  function hideDropdown() { dd.classList.remove('show'); dd.setAttribute('aria-hidden','true'); btn.setAttribute('aria-expanded','false'); }

  btn.addEventListener('click', (e) => { e.stopPropagation(); if (dd.classList.contains('show')) hideDropdown(); else showDropdown(); });

  const markAll = wrapper.querySelector('[data-action="mark-all"]');
  if (markAll) markAll.addEventListener('click', (e) => { e.stopPropagation(); NotificationStore.markAllRead(role); refreshList(); });

  document.addEventListener('pointerdown', (e) => { if (!wrapper.contains(e.target) && dd.classList.contains('show')) hideDropdown(); });
  window.addEventListener('resize', () => { if (dd.classList.contains('show')) _positionDropdown(btn, dd); });

  // sync from server once on init (best-effort) then render
  NotificationStore.syncFromServer(role, refreshList);
  refreshList();
}

function initAllNotifications() {
  const wrappers = document.querySelectorAll('.notif-wrapper');
  wrappers.forEach(w => { if (w.dataset._notifInit) return; w.dataset._notifInit = '1'; initNotificationsPerWrapper(w); });
}

if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', initAllNotifications); else initAllNotifications();
