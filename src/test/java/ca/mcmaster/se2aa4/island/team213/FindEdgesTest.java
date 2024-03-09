package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team213.edgeFinding.FindFirstEdge;

import org.junit.jupiter.api.BeforeEach;

public class FindEdgesTest {
    private Drone droneA, droneB;
    private FindFirstEdge phaseA, phaseB;
    private JSONObject decision, parameter, response, extras;

    @BeforeEach 
    public void setUp() {
        droneA = new Drone("N", 1000);
        droneB = new Drone("E", 1000);
        phaseA = new FindFirstEdge(droneA);
        phaseB = new FindFirstEdge(droneB);

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

    // @Test
    // public void testDecisionToDroneUpdate() {
    //     droneA.parseDecision(decision);
    //     droneA.updateStatus(response);

    //     assertEquals(droneA.getPreviousDecision(), "echoRight");
    //     assertEquals(droneA.getEchoRight(), EchoResult.OUT_OF_RANGE);
    // }

}
