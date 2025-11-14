package ca.udem.maville;

public class DemandFormHandler {


    public void submitDemand(Prestataire prestataire, String titreProjet, EnumWorkType workType,
                             String location, String description, String dateDebut, String dateFin, String id) {
        DemandForm form = new DemandForm(prestataire, titreProjet, workType, location,
                description, dateDebut, dateFin, id);
        DemandRepository.addForm(form);
    }

}
