const API_URL = "JSON_files/problems.json";

// Fetch data from the API and render it in the table.
// Wrapped in try/catch so the UI shows a very friendly message on failure.
async function fetchData() {
  try {
    const response = await fetch(API_URL);
    if (!response.ok) {
      // If the server responds with a non-2xx status, show an error in the table.
      console.error('fetch failed', response.status, response.statusText);
      document.getElementById('tableBody').innerHTML = `<tr><td colspan='6'>Erreur de chargement des données (${response.status})</td></tr>`;
      return;
    }
    const data = await response.json();
    //const records = data.result.records;
      const records = data;
    populateTable(records)
  } catch (error) {
    // Unexpected error (network issue, JSON parse error, etc).
    console.error('fetchData error:', error);
    document.getElementById('tableBody').innerHTML = `<tr><td colspan='6'>Erreur de chargement des données</td></tr>`;
  }
}

// Render the records array into the table body.
function populateTable(records) {
  const tbody = document.getElementById('tableBody');
  tbody.innerHTML = '';
  if (!Array.isArray(records) || records.length === 0) {
    tbody.innerHTML = `<tr><td colspan='6'>Aucun résultat trouvé</td></tr>`;
    document.getElementById('resultCount').textContent = 'Affichage de 0 requêtes';
    return;
  }

  records.forEach(record => {
    const row = document.createElement('tr');
    // Simple fallbacks so the table stays readable.
    row.innerHTML = `
      <td>${record.id || 'N/A'}</td>
      <td>${record.location || 'N/A'}</td>
      <td>${record.boroughid || 'N/A'}</td>
      <td>${record.priority || record.permitcategory || 'N/A'}</td>
      <td><span class="enumStatus">${record.currentStatus || 'N/A'}</span></td>
      <td>${record.description || 'N/A'}</td>
    `;
    tbody.appendChild(row);
  });

  document.getElementById('resultCount').textContent = `Affichage de ${records.length} requêtes`;
}

// --- Search & filters ---

// Simple client-side filters that hide rows based on text and status.
document.getElementById('searchInput').addEventListener('input', filterTable);
document.getElementById('statusFilter').addEventListener('change', filterTable);

function filterTable() {
  const search = document.getElementById('searchInput').value.toLowerCase();
  const enumStatus = document.getElementById('statusFilter').value.toLowerCase();
  const rows = document.querySelectorAll('#permitTable tbody tr');

  rows.forEach(row => {
    const cells = row.querySelectorAll('td');
    const matchSearch = [...cells].some(cell => cell.textContent.toLowerCase().includes(search));
    const matchStatus = enumStatus ? row.cells[3].textContent.toLowerCase().includes(enumStatus) : true;
    row.style.display = matchSearch && matchStatus ? '' : 'none';
  });
}

// --- Modal and form handling ---

(function () {
  const openBtn = document.querySelector('.btn-primary');
  const overlay = document.getElementById('problemModalOverlay');
  const closeBtn = document.getElementById('modalCloseBtn');
  const cancelBtn = document.getElementById('cancelBtn');
  const form = document.getElementById('priorityAssigner');
  //const fileDrop = document.getElementById('fileDrop');
  //const fileInput = document.getElementById('fileInput');

  function openModal() {
    overlay.classList.add('show');
    overlay.setAttribute('aria-hidden', 'false');
    document.body.style.overflow = 'hidden';
  }
  function closeModal() {
    overlay.classList.remove('show');
    overlay.setAttribute('aria-hidden', 'true');
    document.body.style.overflow = '';
    form.reset();
    clearFilePreview();
  }

  if (openBtn) openBtn.addEventListener('click', (e) => { e.preventDefault(); openModal(); });
  if (closeBtn) closeBtn.addEventListener('click', closeModal);
  if (cancelBtn) cancelBtn.addEventListener('click', closeModal);
  overlay.addEventListener('click', (e) => { if (e.target === overlay) closeModal(); });
  const card = document.querySelector('.modal-card'); if (card) card.addEventListener('click', e => e.stopPropagation());

  // Submit the resident problem form. We use FormData since the form can include files.
  form.addEventListener('submit', async function (e) {
    e.preventDefault();
    if (!form.checkValidity()) {
      form.reportValidity();
      return;
    }
    const fd = new FormData(form);
      // Logging to help debug submission during development.
      try {
          console.log('FormData entries:', Object.fromEntries(fd.entries()));
      } catch (err) {
      // Some environments may not support Object.fromEntries on FormData: fallback to manual logging.
      for (const pair of fd.entries()) console.log(pair[0], pair[1]);
    }

    // Send to backend endpoint
    try {
      const response = await fetch('/api/agent-problem-set-priority', { method: 'POST', body: fd });
      if (!response.ok) console.error('Server error while sending form', response.status);
      // We don't strictly need the JSON result here, but we try to parse if the server returns it.
      const json = await response.json().catch(() => null);
      if (json) console.log('Server response:', json);
    } catch (err) {
      console.error('Submit error', err);
    }

    closeModal();
  });

  // File drop behaviour: click to open file picker, drag/drop to attach.

  // showFileName / clearFilePreview: small helpers that update the drop zone with the selected file name.
  function showFileName(file) {
    const name = document.createElement('div');
    name.className = 'file-name';
    name.textContent = file.name + ' (' + Math.round(file.size / 1024) + ' KB)';
    // remove previous small preview text if any
    const existing = fileDrop.querySelector('.file-name');
    if (existing) existing.remove();
    fileDrop.appendChild(name);
  }
  function clearFilePreview() {
    const existing = fileDrop.querySelector('.file-name');
    if (existing) existing.remove();
  }
})();

// Run initial data fetch on load.
fetchData().catch(err => console.error(err));
