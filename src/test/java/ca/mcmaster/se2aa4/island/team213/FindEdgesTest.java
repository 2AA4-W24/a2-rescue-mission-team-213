package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class FindEdgesTest {
    private Drone droneA, droneB;
    private FindEdges feA, feB;

    @BeforeEach 
    public void setUp() {
        droneA = new Drone("N", 1000);
        feA = new FindEdges(droneA);

        droneB = new Drone("E", 1000);
        feB = new FindEdges(droneB);
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
    public void testRequeue() {
        feA.removeFromPhases();
        droneA.setPreviousDecision("echoRight");
        droneA.setEchoRight("OUT_OF_RANGE");

        feA.checkDrone(droneA);
        assertFalse(feA.isFinished());
        feA.createDecision(droneA);
        assertFalse(feA.isFinished());

        assertEquals(feA.getPhases().peek(), feA.getFlyPastDeterminedA());
        feA.removeFromPhases();
        assertNotEquals(feA.getPhases().peek(), null);
    }

    @Test
    public void testEndQueue() {
        feA.removeFromPhases();
        feA.removeFromPhases();
        feA.removeFromPhases();

        assertTrue(feA.isFinished());
        
    }

}
