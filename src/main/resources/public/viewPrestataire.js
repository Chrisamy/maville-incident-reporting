(function(){
  // Nodes used by the prestataire form/modal
  const openBtn = document.querySelector('.topbar .right .btn-primary');
  const overlay = document.getElementById('prestataireModalOverlay');
  const closeBtn = document.getElementById('prestModalClose');
  const cancelBtn = document.getElementById('prestCancel');
  const form = document.getElementById('prestataireForm');

  const rbqDrop = document.getElementById('prestFileDropRBQ');
  const budgetDrop = document.getElementById('prestFileDropBudget');
  const planDrop = document.getElementById('prestFileDropPlan');
  const rbqInput = document.getElementById('prestFileInputRBQ');
  const budgetInput = document.getElementById('prestFileInputBudget');
  const planInput = document.getElementById('prestFileInputPlan');

  function openModal(){
    overlay.classList.add('show');
    overlay.setAttribute('aria-hidden','false');
    document.body.style.overflow='hidden';
  }
  function closeModal(){
    overlay.classList.remove('show');
    overlay.setAttribute('aria-hidden','true');
    document.body.style.overflow='';
    form.reset();
    clearPreview(rbqDrop);
    clearPreview(budgetDrop);
    clearPreview(planDrop);
  }

  if(openBtn) openBtn.addEventListener('click', e=> { e.preventDefault(); openModal(); });
  if(closeBtn) closeBtn.addEventListener('click', closeModal);
  if(cancelBtn) cancelBtn.addEventListener('click', closeModal);
  overlay.addEventListener('click', e=> { if(e.target === overlay) closeModal(); });

  const card = overlay.querySelector('.modal-card');

  if(card) card.addEventListener('click', e=> e.stopPropagation());

  // Submit handler: collect files, create FormData and send to server.
  form.addEventListener('submit', async function(e){
    e.preventDefault();
    if(!form.checkValidity()){ form.reportValidity(); return; }
    const fd = new FormData(form);
    if(rbqInput.files[0]) fd.append('rbqFile', rbqInput.files[0]);
    if(budgetInput.files[0]) fd.append('budgetPdf', budgetInput.files[0]);
    if(planInput.files[0]) fd.append('planPdf', planInput.files[0]);

    // Logging to help debug submission during development.
    try{
      console.log('Submitting prestataire form', Object.fromEntries(fd.entries()));
    }catch(e){ for(const p of fd.entries()) console.log(p[0], p[1]); }

    try{ await fetch('/api/prestataire-form-send',{ method:'POST', body:fd }); }catch(err){ console.log('submit error',err); }
    closeModal();
  });

  // Shared logic to attach drag/drop and click handlers to file-drop areas.
  [rbqDrop, budgetDrop, planDrop].forEach((drop, i)=>{
    const input = [rbqInput, budgetInput, planInput][i];
    if(!drop || !input) return;
    drop.addEventListener('click', ()=> input.click());
    drop.addEventListener('dragover', e=>{ e.preventDefault(); drop.style.borderColor='#a0a9b5'; });
    drop.addEventListener('dragleave', e=>{ e.preventDefault(); drop.style.borderColor=''; });
    drop.addEventListener('drop', e=>{ e.preventDefault(); drop.style.borderColor=''; const files = e.dataTransfer.files; if(files && files.length){ input.files = files; showFileName(drop, files[0]); } });
    input.addEventListener('change', ()=>{ if(input.files[0]) showFileName(drop, input.files[0]); });
  });

  function showFileName(dropEl, file){
    clearFilePreview(dropEl);
    const d = document.createElement('div');
    d.className='file-name';
    d.textContent = file.name + ' (' + Math.round(file.size/1024) + ' KB)';
    dropEl.appendChild(d);
  }
  function clearFilePreview(dropEl){
    const ex = dropEl.querySelector('.file-name');
    if(ex) ex.remove();
  }

})();
