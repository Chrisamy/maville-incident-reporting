package ca.udem.maville;

import java.util.ArrayList;
import java.util.List;

public class ProblemRepository {

    static ArrayList<FormResident> FormList = new ArrayList<>();

    public ArrayList<FormResident> getFormList(){
        return FormList;
    }

    public static void addForm(FormResident form){
        FormList.add(form);
    }
}
