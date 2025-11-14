package ca.udem.maville;

public class PresFormHandler {


    public void submitDemand(Prestataire prestataire, String titreProjet, EnumWorkType workType,
                             String location, String description, String dateDebut, String dateFin, String id) {
        FormPrestataire form = new FormPrestataire(prestataire, titreProjet, workType, location,
                description, dateDebut, dateFin, id);
        DemandRepository.addForm(form);
    }

}
