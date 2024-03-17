package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.CarvePerimeter;

public class CarvePerimeterTest {
    CarvePerimeter cpN, cpE;
    Drone dN, dE;

    @BeforeEach
    public void setUp() {
        cpN = new CarvePerimeter(10, 10, Direction.N);
        cpE = new CarvePerimeter(10, 10, Direction.E);
        dN = new Drone("N", 50000);
        dE = new Drone("E", 50000);
    }
    
    @Test
    public void testTurn() {
        for(int i = 0; i <= cpN.getVerticalFlyActions() * 2 + 1; i++) {
            dN.parseDecision(cpN.createDecision(dN));
        }
        cpN.checkDrone(dN);
        assertEquals(dN.getPreviousDecision(), Action.TURN_RIGHT);
        assertEquals(dN.getDirection(), Direction.E);


        for(int i = 0; i <= cpE.getHorizontalFlyActions() * 2 + 1; i++) {
            dE.parseDecision(cpE.createDecision(dE));  
        }
        cpE.checkDrone(dE);
        assertEquals(dE.getPreviousDecision(), Action.TURN_RIGHT);
        assertEquals(dE.getDirection(), Direction.S);
    }

    @Test
    public void testFinish() {
        for(int j = 0; j < 2; j++) {
            for(int i = 0; i <= cpN.getHorizontalFlyActions() * 2 + 1; i++) {
                dN.parseDecision(cpN.createDecision(dN));
            }
            cpN.checkDrone(dN);
            for(int i = 0; i <= cpN.getVerticalFlyActions() * 2 + 1; i++) {
                dN.parseDecision(cpN.createDecision(dN));
            }
            cpN.checkDrone(dN);
        }
        assertTrue(cpN.isFinished());
        assertEquals(cpN.getDroneX(), 0);
        assertEquals(cpN.getDroneY(), 8);
    }
    
}
