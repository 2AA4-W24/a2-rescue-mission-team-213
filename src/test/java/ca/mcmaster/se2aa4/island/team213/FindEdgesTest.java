package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class FindEdgesTest {
    private Drone droneA, droneB;
    private FindEdges feA, feB;
    private JSONObject decision, parameter, response, extras;

    @BeforeEach 
    public void setUp() {
        droneA = new Drone("N", 1000);
        feA = new FindEdges(droneA);
        droneB = new Drone("E", 1000);
        feB = new FindEdges(droneB);

        decision = new JSONObject();
        parameter = new JSONObject();
        parameter.put("direction", droneA.getDirection().rightTurn());
        decision.put("parameters", parameter);
        decision.put("action", "echo");

        response = new JSONObject();
        extras = new JSONObject();
        extras.put("found", "OUT_OF_RANGE");
        response.put("extras", extras);
        response.put("cost", 1);
        response.put("status", "OK");
    }

    @Test
    public void testStartStates() {
        assertEquals(droneA.getDirection(), Direction.N);
        assertFalse(feA.isFinished());
        assertFalse(feA.getIncreaseX());

        assertEquals(droneB.getDirection(), Direction.E);
        assertFalse(feB.isFinished());
        assertTrue(feB.getIncreaseX());
    }

    @Test
    public void testQueue() {
        assertEquals(feA.getPhases().peek(), feA.getFindFirstEdge());
        feA.removeFromPhases();
        assertNotEquals(feA.getPhases().peek(), feA.getFindFirstEdge());
        assertEquals(feA.getPhases().peek(), feA.getFindSecondEdge());
    }

    @Test
    public void testDecisionToDroneUpdate() {
        droneA.parseDecision(decision);
        droneA.updateStatus(response);

        assertEquals(droneA.getPreviousDecision(), "echoRight");
        assertEquals(droneA.getEchoRight(), "OUT_OF_RANGE");
    }

    @Test
    public void testRequeue() {
        feA.removeFromPhases();

        droneA.parseDecision(decision);
        droneA.updateStatus(response);

        feA.checkDrone(droneA);
        assertFalse(feA.isFinished()); // feA.requeue() called in this method
        feA.createDecision(droneA);
        assertFalse(feA.isFinished());

        assertEquals(feA.getPhases().peek(), feA.getFlyPastDeterminedA());
        feA.removeFromPhases();
        assertEquals(feA.getPhases().peek(), feA.getFindThirdEdge());
    }

    @Test
    public void testEndQueue() {
        feA.removeFromPhases();
        feA.removeFromPhases();
        feA.removeFromPhases();

        assertTrue(feA.isFinished());
        
    }

}
