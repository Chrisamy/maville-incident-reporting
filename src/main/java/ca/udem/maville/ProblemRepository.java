package ca.udem.maville;

import java.util.ArrayList;

public class ProblemRepository {

    private static ProblemRepository instance = null;
    public static ArrayList<ProblemForm> FormList;

    private ProblemRepository() {
        FormList = new ArrayList<>();
    }

    public static synchronized ProblemRepository getInstance() {
        if (instance == null) {
            instance = new ProblemRepository();
        }
        return instance;
    }


    public ArrayList<ProblemForm> getFormList(){
        return FormList;
    }

    public static void addForm(ProblemForm form){
        FormList.add(form);
    }
}
