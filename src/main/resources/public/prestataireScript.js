function printPDF() {
    let body = document.querySelector("body");

    let nomPres = prompt("Nom de prestataire");
    let titreProj = prompt("C'est quoi le titre de votre projet");
    let problemCibler = prompt("C'est quoi le(s) Problème(s) ciblé(s)");
    let descriptDuProj = prompt("Description du projet?");
    let typeTrav = prompt("c'est quoi le type de travaux?");
    let lieuProj = prompt("Lieu du projet?");
    let dateStart = prompt("Date de début")
    let dateFin = prompt("Date de fin")
    let coutEstimer = prompt("Coût estimé?")

    body.innerHTML = "<h1>" + nomPres + "</h1>" ;
    body.innerHTML = body.innerHTML + "<h2>" + titreProj + "</h2>";
    body.innerHTML = body.innerHTML + "<h3>" + problemCibler + "</h3>";
    body.innerHTML = body.innerHTML + "<p>" + descriptDuProj + "</p>";
    body.innerHTML = body.innerHTML + "<u>" + typeTrav + "</u>";
    body.innerHTML = body.innerHTML + "<h2>" + lieuProj + "</h2>";
    body.innerHTML = body.innerHTML + "<h2>" + dateStart + "</h2>";
    body.innerHTML = body.innerHTML + "<h2>" + dateFin + "</h2>";
    body.innerHTML = body.innerHTML + "<h1>" + coutEstimer + "</h1>";
}