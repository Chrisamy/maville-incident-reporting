// Resident login button
const residentLoginBtn = document.getElementById('resident-Login');
if (residentLoginBtn) {
  residentLoginBtn.addEventListener('click', async () => {
    try {
      const username = document.getElementById('email-resident').value;
      const password = document.getElementById('password-resident').value;

      // The server expects JSON
      const response = await fetch('/api/resident-log-in', {
        method: 'POST',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username }),
      });

      // If the server returned JSON we parse it, otherwise we ignore it.
      const data = await response.json().catch(() => null);
      console.log('Login response', data);

      // In the real app we'd check success and redirect
    } catch (err) {
      // Friendly error :)
      alert('Login failed: ' + err);
    }
  });
}
