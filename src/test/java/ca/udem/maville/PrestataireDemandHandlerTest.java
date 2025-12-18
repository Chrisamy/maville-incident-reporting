package ca.udem.maville;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class PrestataireDemandHandlerTest {

    @Test
    public void changeStartDateTest() {
        PrestataireDemandHandler handler = new PrestataireDemandHandler();
        ArrayList<DemandForm> DemandList = new ArrayList<>();
        DemandForm demand = new DemandForm("titre", EnumWorkType.RoadWork, "quelque part",
                "demande de projet pour problème x", "14 septembre 2025", "12 mars 2026",
                39021.15);
        DemandList.add(demand);
        handler.changeStartDate(DemandList, demand.getId(), "27 mai 2025");
        assertEquals("27 mai 2025", demand.getStartDate());
    }

    @Test
    public void changeEndDateTest() {
        PrestataireDemandHandler handler = new PrestataireDemandHandler();
        ArrayList<DemandForm> DemandList = new ArrayList<>();
        DemandForm demand = new DemandForm("titre", EnumWorkType.RoadWork, "quelque part",
                "demande de projet pour problème x", "14 septembre 2025", "12 mars 2026",
                39021.15);
        DemandList.add(demand);
        handler.changeEndDate(DemandList, demand.getId(), "13 juin 2027");
        assertEquals("13 juin 2027", demand.getEndDate());
    }

    @Test
    public void changeStatusTest() {
        PrestataireDemandHandler handler = new PrestataireDemandHandler();
        ArrayList<DemandForm> DemandList = new ArrayList<>();
        DemandForm demand = new DemandForm("titre", EnumWorkType.RoadWork, "quelque part",
                "demande de projet pour problème x", "14 septembre 2025", "12 mars 2026",
                39021.15);
        DemandList.add(demand);
        handler.changeStatus(DemandList, demand.getId(), EnumStatus.onHold);
        assertEquals(EnumStatus.onHold, demand.getStatus());
    }

    @Test
    public void changeCostEstimateTest() {
        PrestataireDemandHandler handler = new PrestataireDemandHandler();
        ArrayList<DemandForm> DemandList = new ArrayList<>();
        DemandForm demand = new DemandForm("titre", EnumWorkType.RoadWork, "quelque part",
                "demande de projet pour problème x", "14 septembre 2025", "12 mars 2026",
                39021.15);
        DemandList.add(demand);
        handler.changeCostEstimate(DemandList, demand.getId(), 3.50);
        assertEquals(3.50, demand.getCostEstimate());
    }

}
