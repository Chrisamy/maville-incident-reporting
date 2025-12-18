const API_URL = "JSON_files/problems.json";

// Render the records array into the table body.
document.addEventListener('DOMContentLoaded', async () => {
    const tbody = document.getElementById('tableBody');

    async function loadAndRender() {
        tbody.innerHTML = '';
        // load problems from local JSON file
        const response = await fetch(API_URL);
        const listofProblem = await response.json(); //so we get the list of problems

        if (!Array.isArray(listofProblem) || listofProblem.length === 0) {
            tbody.innerHTML = `<tr><td colspan='7'>Aucun résultat trouvé</td></tr>`;
            document.getElementById('resultCount').textContent = 'Affichage de 0 requêtes';
            return;
        }

        listofProblem.forEach(item => {
            const row = document.createElement('tr');
            // Format date to YYYY-MM-DD if available
            let dateStr = 'N/A';
            if (item.date) {
              try {
                const d = new Date(item.date);
                if (!isNaN(d)) {
                  const yy = d.getFullYear();
                  const mm = String(d.getMonth()+1).padStart(2,'0');
                  const dd = String(d.getDate()).padStart(2,'0');
                  dateStr = `${yy}-${mm}-${dd}`;
                }
              } catch (e) { dateStr = item.date; }
            }

            // Render cells (no description column)
            row.innerHTML = `
      <td class="col-id">${item.id || 'N/A'}</td>
      <td class="col-location">${item.location || 'N/A'}</td>
      <td class="col-borough">${item.boroughId || item.permitcategory || 'N/A'}</td>
      <td class="col-priority"><span class="enumPriority">${item.priority || 'N/A'}</span></td>
      <td class="col-status">${item.currentStatus || item.status || 'N/A'}</td>
      <td class="col-date">${dateStr}</td>
      <td class="col-actions"></td>
    `;


            const actionsCell = row.querySelector('.col-actions');
            const btnTreat = document.createElement('button');
            btnTreat.className = 'btn-treat';
            btnTreat.textContent = 'Traiter';
            btnTreat.addEventListener('click', (e) => {
                e.stopPropagation();
                openTreatmentModal(item);
            });
            if (actionsCell) actionsCell.appendChild(btnTreat);

            tbody.appendChild(row);
        });

        document.getElementById('resultCount').textContent = `Affichage de ${listofProblem.length} requêtes`;
    }

    // initial load
    await loadAndRender();

    // Treatment modal helpers
    const treatmentOverlay = document.getElementById('treatmentModalOverlay');
    const treatmentClose = document.getElementById('treatmentCloseBtn');
    const treatIdEl = document.getElementById('treat-id');
    const treatLocationEl = document.getElementById('treat-location');
    const treatBoroughEl = document.getElementById('treat-borough');
    const treatWorktypeEl = document.getElementById('treat-worktype');
    const treatPriorityEl = document.getElementById('treat-priority');
    const treatStatusEl = document.getElementById('treat-status');
    const treatDescEl = document.getElementById('treat-description');
    const treatDateEl = document.getElementById('treat-date');
    const treatMessage = document.getElementById('treat-message');
    const btnApprove = document.getElementById('treat-approve');
    const btnReject = document.getElementById('treat-reject');
    const btnModify = document.getElementById('treat-modify');

    function openTreatmentModal(item) {
      // populate fields
      treatIdEl.textContent = item.id || '';
      treatLocationEl.textContent = item.location || '';
      treatBoroughEl.textContent = item.boroughId || item.permitcategory || '';
      treatWorktypeEl.textContent = item.workType || '';
      treatPriorityEl.textContent = item.priority || '';
      treatStatusEl.textContent = item.currentStatus || item.status || '';
      treatDescEl.textContent = item.description || '';
      // problems.json does not include a date field; display placeholder
      treatDateEl.textContent = item.date || 'N/A';
      treatMessage.value = '';
      treatmentOverlay.classList.add('show');
      treatmentOverlay.setAttribute('aria-hidden','false');

      // wire actions (avoid duplicate listeners by replacing handlers)
      btnApprove.onclick = async () => {
        btnApprove.disabled = true;
        try {
          const res = await fetch(`/submissions/${encodeURIComponent(item.id)}/approve`, { method: 'POST' });
          if (!res.ok) console.error('Approve failed', res.status);
        } catch (err) { console.error(err); }
        closeTreatmentModal();
        await loadAndRender();
      };

      btnReject.onclick = async () => {
        btnReject.disabled = true;
        try {
          const res = await fetch(`/submissions/${encodeURIComponent(item.id)}/reject`, { method: 'POST' });
          if (!res.ok) console.error('Reject failed', res.status);
        } catch (err) { console.error(err); }
        closeTreatmentModal();
        await loadAndRender();
      };

      btnModify.onclick = async () => {
        btnModify.disabled = true;
        try {
          const fd = new FormData();
          fd.append('message', treatMessage.value || '');
          const res = await fetch(`/submissions/${encodeURIComponent(item.id)}/request-modification`, { method: 'POST', body: fd });
          if (!res.ok) console.error('Modify request failed', res.status);
        } catch (err) { console.error(err); }
        closeTreatmentModal();
        await loadAndRender();
      };
    }

    function closeTreatmentModal() {
      treatmentOverlay.classList.remove('show');
      treatmentOverlay.setAttribute('aria-hidden','true');
    }

    treatmentClose.addEventListener('click', closeTreatmentModal);
    treatmentOverlay.addEventListener('pointerdown', (e) => { if (e.target === treatmentOverlay) closeTreatmentModal(); });
});

// --- Search & filters ---

// Simple client-side filters that hide rows based on text and status.
document.getElementById('searchInput').addEventListener('input', filterTable);
document.getElementById('statusFilter').addEventListener('change', filterTable);

function filterTable() {
  const search = document.getElementById('searchInput').value.toLowerCase();
  const enumStatus = document.getElementById('statusFilter').value.toLowerCase();
  const rows = document.querySelectorAll('#agentTable tbody tr');

  rows.forEach(row => {
    const cells = row.querySelectorAll('td');
    const matchSearch = [...cells].some(cell => cell.textContent.toLowerCase().includes(search));
    const matchStatus = enumStatus ? row.cells[4].textContent.toLowerCase().includes(enumStatus) : true;
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
