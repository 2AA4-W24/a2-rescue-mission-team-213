package ca.mcmaster.se2aa4.island.team213;

import ca.mcmaster.se2aa4.island.team213.dronephases.areascan.InterlacedAreaScan;
import ca.mcmaster.se2aa4.island.team213.dronephases.carveperimeter.BooleanMap;
import ca.mcmaster.se2aa4.island.team213.dronephases.carveperimeter.DronePosition;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AreaScanTest {
    private JSONObject response;
    private JSONObject scanResponse;
    private Drone dE;
    private Drone dS;
    private Drone dN;
    private Drone dW;
    private InterlacedAreaScan areaScanEast;
    private InterlacedAreaScan areaScanSouth;
    private InterlacedAreaScan areaScanNorth;
    private InterlacedAreaScan areaScanWest;
    private DronePosition dronePositionEast;
    private DronePosition dronePositionSouth;
    private DronePosition dronePositionNorth;
    private DronePosition dronePositionWest;

    @BeforeEach
    public void setUp() {
        dE = new Drone("E", 15000);
        dronePositionEast = new DronePosition(20,20,Direction.E);
        areaScanEast = new InterlacedAreaScan(dronePositionEast, new BooleanMap(20,20), Direction.E);
        dS = new Drone("S", 15000);
        dronePositionSouth = new DronePosition(20,20,Direction.S);
        areaScanSouth = new InterlacedAreaScan(dronePositionSouth, new BooleanMap(20,20), Direction.S);
        dN = new Drone("N", 15000);
        dronePositionNorth = new DronePosition(20,20,Direction.N);
        areaScanNorth = new InterlacedAreaScan(dronePositionNorth, new BooleanMap(20,20), Direction.N);
        dW = new Drone("W", 15000);
        dronePositionWest = new DronePosition(20,20,Direction.W);
        areaScanWest = new InterlacedAreaScan(dronePositionWest, new BooleanMap(20,20), Direction.W);
    }

    @Test
    public void finalPositionTestEast(){
        while(!areaScanEast.isFinished()){
            areaScanEast.createDecision(dE);
        }
        //Expected final position of drone for 20x20 map starting facing East
        assertEquals(dronePositionEast.getDroneX(), 19);
        assertEquals(dronePositionEast.getDroneY(), 17);
    }

    @Test
    public void finalPositionTestSouth(){
        while(!areaScanSouth.isFinished()){
            areaScanSouth.createDecision(dS);
        }
        //Expected final position of drone for 20x20 map starting facing East
        assertEquals(dronePositionSouth.getDroneX(), 2);
        assertEquals(dronePositionSouth.getDroneY(), 19);
    }

    @Test
    public void finalPositionTestNorth(){
        while(!areaScanNorth.isFinished()){
            areaScanNorth.createDecision(dN);
        }
        //Expected final position of drone for 20x20 map starting facing East
        assertEquals(dronePositionNorth.getDroneX(), 17);
        assertEquals(dronePositionNorth.getDroneY(), 0);
    }

    @Test
    public void finalPositionTestWest(){
        while(!areaScanWest.isFinished()){
            areaScanWest.createDecision(dW);
        }
        //Expected final position of drone for 20x20 map starting facing East
        assertEquals(dronePositionWest.getDroneX(), 0);
        assertEquals(dronePositionWest.getDroneY(), 2);
    }

    @Test
    public void checkDroneTest(){
        dE = new Drone("E", 15000);
        dronePositionEast = new DronePosition(20,20,Direction.E);
        areaScanEast = new InterlacedAreaScan(dronePositionEast, new BooleanMap(20,20), Direction.E);
        areaScanEast.createDecision(dE);
        areaScanEast.createDecision(dE);

        createScanSiteResponse();
        createAction();
        dE.parseDecision(response);
        dE.updateStatus(scanResponse);
        areaScanEast.checkDrone(dE);

        areaScanEast.createDecision(dE);
        createAction();
        createScanCreekResponse("creek1");
        dE.parseDecision(response);
        dE.updateStatus(scanResponse);
        areaScanEast.checkDrone(dE);

        areaScanEast.createDecision(dE);
        areaScanEast.createDecision(dE);
        createAction();
        createScanCreekResponse("creek2");
        dE.parseDecision(response);
        dE.updateStatus(scanResponse);
        areaScanEast.checkDrone(dE);

        assertEquals(dE.getCreekID(), "creek1");
    }

    private void createAction(){
        response = new JSONObject();
        response.put("action", "scan");
    }
    private void createScanSiteResponse() {
        scanResponse = new JSONObject();
        JSONArray sites = new JSONArray();
        sites.put("site1");
        JSONArray creeks = new JSONArray();
        JSONObject extras = new JSONObject();
        extras.put("creeks", creeks);
        extras.put("sites", sites);
        extras.put("biomes", new JSONArray());

        scanResponse.put("extras", extras);
        scanResponse.put("cost", 2);
        scanResponse.put("status", "OK");
    }

    private void createScanCreekResponse(String id) {
        scanResponse = new JSONObject();
        JSONArray sites = new JSONArray();
        JSONArray creeks = new JSONArray();
        creeks.put(id);
        JSONObject extras = new JSONObject();
        extras.put("creeks", creeks);
        extras.put("sites", sites);
        extras.put("biomes", new JSONArray());

        scanResponse.put("extras", extras);
        scanResponse.put("cost", 2);
        scanResponse.put("status", "OK");
    }
}
