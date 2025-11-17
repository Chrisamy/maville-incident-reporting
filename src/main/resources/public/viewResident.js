const API_URL = "https://donnees.montreal.ca/api/3/action/datastore_search?resource_id=cc41b532-f12d-40fb-9f55-eb58c9a2b12b";

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
    const records = data.result.records;
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

  // Define synonyms for each EnumWorkType so we can map API values to a stable key
  const workTypeSynonyms = {
    RoadWork: ['route','routier','travaux routiers','asphalte','nid de poule','pavage','réfection','réasphaltage','pavage'],
    GazOrElectric: ['gaz','gaz naturel','électricité','ligne électrique','câble électrique','branchement gaz','gaz ou électricité'],
    ConstructionOrRenovation: ['construction','rénovation','chantier','bâtiment','travaux de construction','rénovation'],
    Landscaping: ['paysage','aménagement paysager','végétation','pelouse','arbres','plantation','élagage'],
    PublicsTransportWork: ['transport','transports en commun','métro','bus','tram','autobus','ligne de bus'],
    SignageAndLighting: ['signalisation','éclairage','lampadaire','feu de circulation','éclairage public','panneau de signalisation'],
    UndergroundWork: ['souterrain','excavation','tranchée','fouille','fossé','drain','canalisation','conduite','tranchee','excavation profonde'],
    ResidentialWork: ['résidentiel','résidence','logement','entrée privée','rue résidentielle'],
    UrbanMaintenance: ['entretien','maintenance','balayage','ramassage','collecte','entretien routier','voirie','réhabilitation'],
    TelecommunicationsMaintenance: ['télécom','réseau','fibre','fibre optique','câble','réseau télécom','installation fibre']
  };

  // Detect work type key from a raw string provided by the API
  function detectWorkType(raw) {
    if (!raw) return 'notDefined';
    const normalize = s => String(s).toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[^a-z0-9àâäéèêëîïôöùûüç-]/g, '');
    const s = normalize(raw);

    // check synonyms for each key
    for (const [key, synonyms] of Object.entries(workTypeSynonyms)) {
      for (const syn of synonyms) {
        if (s.includes(normalize(syn))) return key;
      }
    }

    // Fallback : if contains words like 'excav' or 'trench' map to UndergroundWork
    if (s.includes('excav') || s.includes('trench') || s.includes('fouille') || s.includes('tranchee')) return 'UndergroundWork';

    // no match -> 'notDefined' (Autre)
    console.debug('[detectWorkType] unmatched raw type:', raw);
    return 'notDefined';
  }

  records.forEach(record => {
    const row = document.createElement('tr');

    // Determine the raw type text the API provides
    const rawTypeText = (record.reason_category || record.permitcategory || record.permit_category || '') + '';
    // store normalized English key as a stable data attribute so filtering is reliable
    row.dataset.type = detectWorkType(rawTypeText);

    // Keep the displayed text as-is (so users see the API label)
    row.innerHTML = `
      <td>${record.id || 'N/A'}</td>
      <td>${record.boroughid || 'N/A'}</td>
      <td>${rawTypeText || 'N/A'}</td>
      <td><span class="enumStatus">${record.currentstatus || 'N/A'}</span></td>
      <td>${record.submittercategory || 'N/A'}</td>
      <td>${record.organizationname || 'N/A'}</td>
    `;

    tbody.appendChild(row);
  });

  document.getElementById('resultCount').textContent = `Affichage de ${records.length} requêtes`;
}

// --- Search & filters ---

(function(){
  const searchInput = document.getElementById('searchInput');
  const statusFilter = document.getElementById('statusFilter');
  const typeFilter = document.getElementById('typeFilter');

  if (searchInput) searchInput.addEventListener('input', filterTableResident);
  if (statusFilter) statusFilter.addEventListener('change', filterTableResident);
  if (typeFilter) typeFilter.addEventListener('change', filterTableResident);

  function filterTableResident(){
    const search = (searchInput && searchInput.value || '').toLowerCase();
    const status = (statusFilter && statusFilter.value || '').toLowerCase();
    const type = (typeFilter && typeFilter.value || ''); // English key from select

    let rows = document.querySelectorAll('#permitTable tbody tr');
    if (!rows || rows.length === 0) {
      rows = Array.from(document.querySelectorAll('#permitTable tr')).filter(r => r.querySelectorAll('th').length === 0);
    }

    rows.forEach(row => {
      const cells = row.querySelectorAll('td');
      if (!cells || cells.length === 0) { row.style.display = ''; return; }

      const matchSearch = [...cells].some(cell => cell.textContent.toLowerCase().includes(search));
      const matchStatus = status ? (row.cells[3] && row.cells[3].textContent.toLowerCase().includes(status)) : true;

      // Compare the key stored in data-type with the select's value
      let matchType = true;
      if (type) {
        const rowType = (row.dataset && row.dataset.type) || 'notDefined';
        matchType = rowType === type;
      }

      row.style.display = (matchSearch && matchStatus && matchType) ? '' : 'none';
    });
  }
})();


// --- Modal and form handling ---

(function () {
  const openBtn = document.querySelector('.btn-primary');
  const overlay = document.getElementById('problemModalOverlay');
  const closeBtn = document.getElementById('modalCloseBtn');
  const cancelBtn = document.getElementById('cancelBtn');
  const form = document.getElementById('problemForm');
  const fileDrop = document.getElementById('fileDrop');
  const fileInput = document.getElementById('fileInput');

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

    if (fileInput.files[0]) fd.append('photo', fileInput.files[0]);

    // Send to backend endpoint
    try {
      const response = await fetch('/api/resident-form-send', { method: 'POST', body: fd });
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

  if (fileDrop && fileInput) {
    fileDrop.addEventListener('click', () => fileInput.click());
    fileDrop.addEventListener('dragover', e => { e.preventDefault(); fileDrop.style.borderColor = '#a0a9b5'; });
    fileDrop.addEventListener('dragleave', e => { e.preventDefault(); fileDrop.style.borderColor = ''; });
    fileDrop.addEventListener('drop', e => {
      e.preventDefault();
      fileDrop.style.borderColor = '';
      const files = e.dataTransfer.files;
      if (files && files.length) {
        fileInput.files = files;
        showFileName(files[0]);
      }
    });
    fileInput.addEventListener('change', () => { if (fileInput.files[0]) showFileName(fileInput.files[0]); });
  }

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
