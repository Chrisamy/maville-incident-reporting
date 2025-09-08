document.getElementById("hello-world-btn").addEventListener("click", async () => {
    try {
        let response = await fetch("/api/hello_world");
        let data = await response.json();
        document.getElementById("message").innerText = data.message;
    } catch (err) {
        document.getElementById("message").innerText = "Error loading message.";
    }
});