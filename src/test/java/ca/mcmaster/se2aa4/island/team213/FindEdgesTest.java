package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team213.edgeFinding.FlyPastDetermined;
import ca.mcmaster.se2aa4.island.team213.edgeFinding.NewFindFirstEdge;
import ca.mcmaster.se2aa4.island.team213.edgeFinding.NewFindSubsequentEdge;


public class FindEdgesTest {
    private Drone droneA, droneB;
    private NewFindFirstEdge phaseA, phaseB;
    private NewFindSubsequentEdge phaseC;
    private FlyPastDetermined phaseD;


    private int flyActionsLeft;
    private JSONObject echoOOBResponse, echoGResponse, scanResponse;

    @BeforeEach 
    public void setUp() {
        flyActionsLeft = 10;

        droneA = new Drone("N", 1000);   
        droneB = new Drone("E", 1000);

        phaseA = new NewFindFirstEdge(droneA.getDirection());
        phaseB = new NewFindFirstEdge(droneB.getDirection());
        phaseC = new NewFindSubsequentEdge(0, 0, false, 0);
        phaseD = new FlyPastDetermined(0, 0, false, 0, flyActionsLeft);

        createEchoOOBResponse();
        createEchoGResponse();
        createScanResponse();
    }

    private void createEchoOOBResponse() {
        echoOOBResponse = new JSONObject();
        JSONObject extras = new JSONObject();
        extras.put("found", "OUT_OF_RANGE");
        extras.put("range", 3); 

        echoOOBResponse.put("extras", extras);
        echoOOBResponse.put("cost", 1);
        echoOOBResponse.put("status", "OK");
    }

    private void createEchoGResponse() {
        echoGResponse = new JSONObject();
        JSONObject extras = new JSONObject();
        extras.put("found", "GROUND");
        extras.put("range", 3); 

        echoGResponse.put("extras", extras);
        echoGResponse.put("cost", 1);
        echoGResponse.put("status", "OK");
    }

    private void createScanResponse() {
        scanResponse = new JSONObject();
        JSONObject extras = new JSONObject();
        JSONArray biomes = new JSONArray();
        extras.put("creeks", new JSONArray());
        extras.put("sites", new JSONArray());
        biomes.put("OCEAN");
        extras.put("biomes", biomes);

        scanResponse.put("extras", extras);
        scanResponse.put("cost", 2);
        scanResponse.put("status", "OK");
    }

    @Test
    public void testStartStates() {
        assertEquals(droneA.getDirection(), Direction.N);
        assertFalse(phaseA.isFinished());
        assertFalse(phaseA.getIncreaseX());

        assertEquals(droneB.getDirection(), Direction.E);
        assertFalse(phaseB.isFinished());
        assertTrue(phaseB.getIncreaseX());
    }

    @Test
    public void testFindFirstEdgeFinish() {
        assertEquals(phaseA.getNextDecision(), Action.FLY);
        droneA.parseDecision(phaseA.createDecision(droneA));
        phaseA.checkDrone(droneA);

        assertEquals(phaseA.getNextDecision(), Action.SCAN);
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(scanResponse);
        phaseA.checkDrone(droneA);

        assertEquals(phaseA.getNextDecision(), Action.ECHO_LEFT);
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoOOBResponse);
        phaseA.checkDrone(droneA);

        assertEquals(phaseA.getNextDecision(), Action.ECHO_RIGHT);
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoOOBResponse);
        phaseA.checkDrone(droneA);

        assertEquals(phaseA.getNextDecision(), Action.TURN_RIGHT);
        phaseA.createDecision(droneA);
        assertTrue(phaseA.isFinished());
    }

    @Test
    public void testFindFirstEdgeLoop() {
        assertEquals(phaseA.getNextDecision(), Action.FLY);
        droneA.parseDecision(phaseA.createDecision(droneA));
        phaseA.checkDrone(droneA);

        assertEquals(phaseA.getNextDecision(), Action.SCAN);
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(scanResponse);
        phaseA.checkDrone(droneA);

        assertEquals(phaseA.getNextDecision(), Action.ECHO_LEFT);
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoOOBResponse);
        phaseA.checkDrone(droneA);

        assertEquals(phaseA.getNextDecision(), Action.ECHO_RIGHT);
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoGResponse);
        phaseA.checkDrone(droneA);

        assertEquals(phaseA.getNextDecision(), Action.FLY);
        phaseA.createDecision(droneA);
        assertFalse(phaseA.isFinished());
    }

    @Test
    public void testFindSubsequentEdgeFinish() {
        assertEquals(phaseC.getNextDecision(), Action.ECHO_RIGHT);
        droneA.parseDecision(phaseC.createDecision(droneA));
        droneA.updateStatus(echoOOBResponse);
        phaseC.checkDrone(droneA);

        assertEquals(phaseC.getNextDecision(), Action.TURN_RIGHT);
        phaseC.createDecision(droneA);
        assertTrue(phaseC.isFinished());
    }

    @Test
    public void testFindSubsequentEdgeLoop() {
        assertEquals(phaseC.getNextDecision(), Action.ECHO_RIGHT);
        droneA.parseDecision(phaseC.createDecision(droneA));
        droneA.updateStatus(echoGResponse);
        phaseC.checkDrone(droneA);

        assertEquals(phaseC.getNextDecision(), Action.FLY);
        phaseC.createDecision(droneA);
        assertFalse(phaseC.isFinished());
    }

    @Test
    public void testFlyPastDetermined() {
        for(int i = 0; i < flyActionsLeft; i++) {
            phaseD.checkDrone(droneA);
        }

        assertTrue(phaseD.isFinished());
    }

}
