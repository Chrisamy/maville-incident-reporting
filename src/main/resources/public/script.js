let colorSwap = 1;

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
    let reqProposee = prompt("Probleme?");
    let addreseProb = prompt("C'est ou le probleme?")
    let typeProb = prompt("C'est quelle type de probleme?")
    let Status = prompt("mettre le status (pour raison de teste seulement)")

    var row = table.insertRow(-1);
    var cellReqProp = row.insertCell(0);
    var cellAddProb = row.insertCell(1);
    var cellTypeProb = row.insertCell(2);
    var cellStatus = row.insertCell(3);

    cellReqProp.innerHTML = reqProposee;
    cellAddProb.innerHTML = addreseProb;
    cellTypeProb.innerHTML = typeProb;
    cellStatus.innerHTML = Status;

    console.log(colorSwap)

    if (colorSwap == 1) {
        row.setAttribute('id', 'request-row-blue');
        colorSwap *= -1;
    } else {
        row.setAttribute('id', 'request-row-green')
        colorSwap *= -1;
    }


}