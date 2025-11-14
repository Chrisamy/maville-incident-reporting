package ca.udem.maville;

import java.util.Date;

public class FormPrestataire {
    Prestataire prestataire;
    String titreProjet;
    EnumWorkType workType;
    String location;
    String description;
    String dateDebut;
    String dateFin;
    String id;

    public FormPrestataire(Prestataire prestataire, String titreProjet, EnumWorkType workType,
                           String location, String description, String dateDebut, String dateFin, String id) {
        this.prestataire = prestataire;
        this.titreProjet = titreProjet;
        this.workType = workType;
        this.location = location;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.id = id;

    }

    //Getters

    public Prestataire getPrestataire() {
        return prestataire;
    }
    public String getTitreProjet() {
        return titreProjet;
    }
    public EnumWorkType getWorkType() {
        return workType;
    }
    public String getLocation() {
        return location;
    }
    public String getDescription() {
        return description;
    }
    public String getDateDebut() {
        return dateDebut;
    }
    public String getDateFin() {
        return dateFin;
    }
    public String getId() {
        return id;
    }

    //Setters

    public void setPrestataire(Prestataire prestataire) {
        this.prestataire = prestataire;
    }
    public void setTitreProjet(String titreProjet) {
        this.titreProjet = titreProjet;
    }
    public void setWorkType(EnumWorkType workType) {
        this.workType = workType;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }
    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }
    public void setId(String id) {
        this.id = id;
    }
}
