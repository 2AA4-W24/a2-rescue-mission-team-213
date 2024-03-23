package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team213.dronephases.EndPhase;
import ca.mcmaster.se2aa4.island.team213.dronephases.Phase;
import ca.mcmaster.se2aa4.island.team213.enums.Action;

public class EndPhaseTest {
    private Drone drone;
    private Phase end;
    
    @BeforeEach
    public void setUp() {
        end = new EndPhase();
        drone = new Drone("N", 100);
    }

    @Test
    public void testEndPhase() {
        assertFalse(end.isFinished());
        drone.parseDecision(end.createDecision(drone));
        assertEquals(drone.getPreviousDecision(), Action.STOP);
        end.checkDrone(drone);
        assertTrue(end.isFinished());
    }

    @Test
    public void testEndPhaseLoop() {
        end = end.nextPhase();
        drone.parseDecision(end.createDecision(drone));
        assertEquals(drone.getPreviousDecision(), Action.STOP);
    }
}
