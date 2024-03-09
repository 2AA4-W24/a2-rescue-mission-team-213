package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team213.edgeFinding.FindFirstEdge;
import ca.mcmaster.se2aa4.island.team213.edgeFinding.FindSubsequentEdge;
import ca.mcmaster.se2aa4.island.team213.edgeFinding.FlyPastDetermined;


public class FindEdgesTest {
    private Drone droneA, droneB;
    private FindFirstEdge phaseA, phaseB;
    private FindSubsequentEdge phaseC, phaseD;
    private FlyPastDetermined phaseE;
    private Phase phaseZ;


    private int flyActionsLeft;
    private JSONObject decision, parameter, response, extras;

    @BeforeEach 
    public void setUp() {
        droneA = new Drone("N", 1000);   
        droneB = new Drone("E", 1000);

        phaseA = new FindFirstEdge(droneA);
        phaseB = new FindFirstEdge(droneB);
        phaseC = new FindSubsequentEdge(0, 0, false, 0);
        phaseD = new FindSubsequentEdge(0, 0, false, 3);
        flyActionsLeft = 10;
        phaseE = new FlyPastDetermined(0, 0, false, 0, flyActionsLeft);
        phaseZ = new FindFirstEdge(droneA);

        decision = new JSONObject();
        parameter = new JSONObject();
        parameter.put("direction", droneA.getDirection().rightTurn());
        decision.put("parameters", parameter);
        decision.put("action", "echo");

        response = new JSONObject();
        extras = new JSONObject();
        extras.put("found", "OUT_OF_RANGE");
        extras.put("range", 3); 
        response.put("extras", extras);
        response.put("cost", 1);
        response.put("status", "OK");
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
    public void testEndOfPhases() {
        for(int i = 0; i < 6; i++){
            phaseZ = phaseZ.nextPhase();
        }
        assertEquals(phaseZ.createDecision(droneA).toString(), "{\"action\":\"stop\"}");
    }

    @Test
    public void testFindSubsequentEdge() {
        droneA.parseDecision(decision);
        droneA.updateStatus(response);
        phaseC.checkDrone(droneA);
        assertEquals(droneA.getPreviousDecision(), Action.echoRight);
        assertEquals(droneA.getEchoRight(), EchoResult.OUT_OF_RANGE);
        assertEquals(phaseC.createDecision(droneA).toString(), "{\"action\":\"heading\",\"parameters\":{\"direction\":\"" + droneA.getDirection().rightTurn() + "\"}}");
        assertTrue(phaseC.isFinished());
    }

    @Test
    public void testFinalFindSubsequentEdge() {
        phaseZ = phaseD.nextPhase();
        assertEquals(phaseZ.createDecision(droneA).toString(), "{\"action\":\"stop\"}");
    }

    @Test
    public void testFlyPastDetermined() {
        for(int i = 0; i < flyActionsLeft; i++) {
            phaseE.checkDrone(droneA);
        }

        assertTrue(phaseE.isFinished());
    }

}
