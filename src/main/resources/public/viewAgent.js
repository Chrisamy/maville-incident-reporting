const API_URL = "JSON_files/problems.json";

// helper to display priority tokens as French labels
function displayPriority(token) {
  if (!token || token === 'notAssigned' || token === 'N/A') return '—';
  switch (token) {
    case 'low': return 'FAIBLE';
    case 'medium': return 'MOYENNE';
    case 'high': return 'ÉLEVÉE';
    default: return token;
  }
}

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
            // attach id as data attribute to make row lookup reliable
            if (item.id) row.dataset.id = String(item.id).trim();
            // Format date to YYYY-MM-DD if available
            let dateStr = 'N/A';
            if (item.date) {
              try {
                const d = new Date(Number(item.date));
                if (!isNaN(d)) {
                  const yy = d.getFullYear();
                  const mm = String(d.getMonth()+1).padStart(2,'0');
                  const dd = String(d.getDate()).padStart(2,'0');
                  dateStr = `${yy}-${mm}-${dd}`;
                }
              } catch (e) { dateStr = item.date; }
            }

            // Render cells (no description column)
            const priorityLabel = displayPriority(item.priority);
            row.innerHTML = `
      <td class="col-id">${item.id || 'N/A'}</td>
      <td class="col-location">${item.location || 'N/A'}</td>
      <td class="col-borough">${item.boroughId || item.permitcategory || 'N/A'}</td>
      <td class="col-priority"><span class="enumPriority">${priorityLabel}</span></td>
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

    // helper to update a single row's status in-place without refetching
    function updateRowStatus(id, newStatus) {
        if (!id) return;
        const row = tbody.querySelector(`tr[data-id="${id}"]`);
        if (row) {
            const statusCell = row.querySelector('.col-status');
            if (statusCell) statusCell.textContent = newStatus || '';
        } else {
            // fallback: try to find by id cell text
            const rows = Array.from(tbody.querySelectorAll('tr'));
            for (const r of rows) {
                const idCell = r.querySelector('.col-id');
                if (idCell && idCell.textContent.trim() === id) {
                    const statusCell = r.querySelector('.col-status');
                    if (statusCell) statusCell.textContent = newStatus || '';
                    break;
                }
            }
        }
    }

    // helper to update a single row's priority cell
    function updateRowPriority(id, newPriorityToken) {
        if (!id) return;
        const row = tbody.querySelector(`tr[data-id="${id}"]`);
        if (row) {
            const priCell = row.querySelector('.col-priority .enumPriority');
            if (priCell) priCell.textContent = displayPriority(newPriorityToken);
        } else {
            // fallback: try to find by id cell text
            const rows = Array.from(tbody.querySelectorAll('tr'));
            for (const r of rows) {
                const idCell = r.querySelector('.col-id');
                if (idCell && idCell.textContent.trim() === id) {
                    const priCell = r.querySelector('.col-priority .enumPriority');
                    if (priCell) priCell.textContent = displayPriority(newPriorityToken);
                    break;
                }
            }
        }
    }

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
      // set select values if elements are selects (fallback to text if not)
      try { if (treatWorktypeEl && 'value' in treatWorktypeEl) treatWorktypeEl.value = item.workType || 'notDefined'; else treatWorktypeEl.textContent = item.workType || ''; } catch(e) {}
      try { if (treatPriorityEl && 'value' in treatPriorityEl) treatPriorityEl.value = item.priority || 'notAssigned'; else treatPriorityEl.textContent = item.priority || ''; } catch(e) {}
      try { if (treatStatusEl && 'value' in treatStatusEl) treatStatusEl.value = item.currentStatus || item.status || 'waitingForApproval'; else treatStatusEl.textContent = item.currentStatus || item.status || ''; } catch(e) {}
      treatDescEl.textContent = item.description || '';
      // problems.json may include a date field; display it if present
      treatDateEl.textContent = item.date ? new Date(Number(item.date)).toLocaleString() : 'N/A';
      treatMessage.value = '';
      treatmentOverlay.classList.add('show');
      treatmentOverlay.setAttribute('aria-hidden','false');

      // wire actions (avoid duplicate listeners by replacing handlers)
      btnApprove.onclick = async () => {
        btnApprove.disabled = true;
        try {
          const fd = new FormData();
          fd.append('id', item.id);
          // send the internal values (not the displayed text) so backend can map enums reliably
          const selectedWorkType = (treatWorktypeEl && 'value' in treatWorktypeEl) ? treatWorktypeEl.value : (item.workType || 'notDefined');
          const selectedPriority = (treatPriorityEl && 'value' in treatPriorityEl) ? treatPriorityEl.value : (item.priority || 'notAssigned');
          fd.append('workType', selectedWorkType);
          fd.append('priority', selectedPriority);

          const res = await fetch('/api/agent-accept-problem', { method: 'POST', body: fd });
          const json = await res.json().catch(() => null);
          if (res.ok && json && json.success) {
             const newStatus = json.newStatus || 'approved';
            updateRowStatus(item.id, newStatus);
            // update priority cell if server returned it
            const newPri = (json && json.newPriority) ? json.newPriority : selectedPriority;
            updateRowPriority(item.id, newPri);
            item.priority = newPri;
            item.status = newStatus;
            item.currentStatus = newStatus;
           } else {
            console.error('Approve failed', res.status, json);
          }
        } catch (err) { console.error(err); }
        closeTreatmentModal();
      };

      btnReject.onclick = async () => {
        btnReject.disabled = true;
        try {
          const fd = new FormData();
          fd.append('id', item.id);
          // include selected workType/priority on reject as well
          try { const selectedWorkType = (treatWorktypeEl && 'value' in treatWorktypeEl) ? treatWorktypeEl.value : (item.workType || 'notDefined'); fd.append('workType', selectedWorkType); } catch(e) {}
          try { const selectedPriority = (treatPriorityEl && 'value' in treatPriorityEl) ? treatPriorityEl.value : (item.priority || 'notAssigned'); fd.append('priority', selectedPriority); } catch(e) {}
          const res = await fetch('/api/agent-refuse-problem', { method: 'POST', body: fd });
          const json = await res.json().catch(() => null);
          if (res.ok && json && json.success) {
             const newStatus = json.newStatus || 'rejected';
            updateRowStatus(item.id, newStatus);
            const newPri = (json && json.newPriority) ? json.newPriority : (selectedPriority || item.priority);
            updateRowPriority(item.id, newPri);
            item.priority = newPri;
            item.status = newStatus;
            item.currentStatus = newStatus;
           } else {
            console.error('Reject failed', res.status, json);
          }
        } catch (err) { console.error(err); }
        closeTreatmentModal();
      };

      btnModify.onclick = async () => {
        btnModify.disabled = true;
        try {
          const fd = new FormData(); fd.append('id', item.id); fd.append('message', treatMessage.value || '');
          try { const selectedWorkType = (treatWorktypeEl && 'value' in treatWorktypeEl) ? treatWorktypeEl.value : (item.workType || 'notDefined'); fd.append('workType', selectedWorkType); } catch(e) {}
          try { const selectedPriority = (treatPriorityEl && 'value' in treatPriorityEl) ? treatPriorityEl.value : (item.priority || 'notAssigned'); fd.append('priority', selectedPriority); } catch(e) {}
          const res = await fetch('/api/agent-request-modification', { method: 'POST', body: fd });
          const json = await res.json().catch(() => null);
          if (res.ok && json && json.success) {
             const newStatus = json.newStatus || 'onHold';
            updateRowStatus(item.id, newStatus);
            const newPri = (json && json.newPriority) ? json.newPriority : (selectedPriority || item.priority);
            updateRowPriority(item.id, newPri);
            item.priority = newPri;
            item.status = newStatus;
            item.currentStatus = newStatus;
           } else {
            console.error('Modify request failed', res.status, json);
          }
        } catch (err) { console.error(err); }
        closeTreatmentModal();
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

// Note: priority assigner modal removed — priority is now set from the Traiter modal.
