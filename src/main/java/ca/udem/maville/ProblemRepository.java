package ca.udem.maville;

import java.util.ArrayList;

public class ProblemRepository {

    //Repository pour stocker les formulaires envoyés par les résidents
    //On utilise une instance globale

    private static ProblemRepository instance = null;
    public static ArrayList<ResidentForm> FormList;

    private ProblemRepository() {
        FormList = new ArrayList<>();
    }

    public static synchronized ProblemRepository getInstance() {
        if (instance == null) {
            instance = new ProblemRepository();
        }
        return instance;
    }


    public ArrayList<ResidentForm> getFormList(){
        return FormList;
    }

    public static void addForm(ResidentForm form){
        FormList.add(form);
    }
}
