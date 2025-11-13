let colorSwap = 1;
let requestNum = 1;

document.getElementById("hello-world-btn").addEventListener("click", async () => {
    try {
        let response = await fetch("/api/hello_world");
        let data = await response.json();
        document.getElementById("message").innerText = data.message;
    } catch (err) {
        document.getElementById("message").innerText = "Error loading message.";
    }
});

function addRequestButton() {
    var table = document.getElementById("table-request");
    let typeProb = document.getElementById("type-probleme").value;
    let addreseProb = document.getElementById("Address").value;
    let Status = "Non traité"

    var row = table.insertRow(-1);
    var cellRequestNum = row.insertCell(0);

    var cellAddProb = row.insertCell(1);
    var cellTypeProb = row.insertCell(2);
    var cellStatus = row.insertCell(3);


    cellRequestNum.innerHTML = requestNum;

    cellAddProb.innerHTML = addreseProb;
    cellTypeProb.innerHTML = typeProb;
    cellStatus.innerHTML = Status;

    requestNum += 1;

    if (colorSwap == 1) {
        row.setAttribute('id', 'request-row-blue');
        colorSwap *= -1;
    } else {
        row.setAttribute('id', 'request-row-green')
        colorSwap *= -1;
    }
}

if (!window.eventSource) {
    window.eventSource = new EventSource("/events");
    window.eventSource.onmessage = (event) => {
        alert(event.data);
        };
}