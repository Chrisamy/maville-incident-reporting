package ca.udem.maville;

import java.util.ArrayList;
import java.util.List;

public class ProblemRepository {

    private static ProblemRepository instance = null;
    public ArrayList<FormResident> FormList;

    private ProblemRepository() {
        FormList = new ArrayList<>();
    }

    public static synchronized ProblemRepository getInstance() {
        if (instance == null) {
            instance = new ProblemRepository();
        }
        return instance;
    }


    public ArrayList<FormResident> getFormList(){
        return FormList;
    }

    public static void addForm(FormResident form){
        FormList.add(form);
    }
}
