package ca.udem.maville;

import java.util.ArrayList;
import java.util.List;

public class ProblemRepository {
    ArrayList<FormResident> FormList = new ArrayList<>();

    public ArrayList<FormResident> getFormList(){
        return FormList;
    }

    public void addForm(FormResident form){
        FormList.add(form);
    }
}
