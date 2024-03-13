package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team213.carvePerimeter.CarvePerimeter;

public class CarvePerimeterTest {
    CarvePerimeter cpN, cpE, cpS, cpW;
    Drone drone;
    JSONObject echoGResponse;

    @BeforeEach
    public void setUp() {
        cpN = new CarvePerimeter(10, 10, Direction.N);
        cpE = new CarvePerimeter(5, 10, Direction.E);
        cpS = new CarvePerimeter(10, 5, Direction.S);
        cpW = new CarvePerimeter(18, 31, Direction.W);
        drone = new Drone("N", 500);

        JSONObject decision = new JSONObject();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", "E");
        decision.put("parameters", parameter);
        decision.put("action", "echo");
        drone.parseDecision(decision);

        createEchoGResponse();
        drone.updateStatus(echoGResponse);
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

    @Test
    public void testDroneStartXY() {
        assertEquals(cpN.getDroneX(), 0);
        assertEquals(cpN.getDroneY(), 10 - 2);

        assertEquals(cpE.getDroneX(), 1);
        assertEquals(cpE.getDroneY(), 0);

        assertEquals(cpS.getDroneX(), 10 - 1);
        assertEquals(cpS.getDroneY(), 1);

        assertEquals(cpW.getDroneX(), 18 - 2);
        assertEquals(cpW.getDroneY(), 31 - 1);
    }

    @Test
    public void testFirstDroneTurn() {
        for(int i = 0; i <= cpN.getVerticalFlyActions() * 2 + 1; i++) {
            drone.parseDecision(cpN.createDecision(drone));
        }
        cpN.checkDrone(drone);
        assertEquals(drone.getPreviousDecision(), Action.TURN_RIGHT);
        assertEquals(cpN.getDroneDirection(), Direction.E);

        for(int i = 0; i <= cpW.getHorizontalFlyActions() * 2 + 1; i++) {
            drone.parseDecision(cpW.createDecision(drone));
        }
        cpW.checkDrone(drone);
        assertEquals(drone.getPreviousDecision(), Action.TURN_RIGHT);
        assertEquals(cpW.getDroneDirection(), Direction.N);
    }

    @Test
    public void testFirstTwoDroneTurns() {
        for(int i = 0; i <= cpW.getHorizontalFlyActions() * 2 + 1; i++) {
            drone.parseDecision(cpW.createDecision(drone));
        }
        cpW.checkDrone(drone);
        for(int i = 0; i <= cpW.getVerticalFlyActions() * 2 + 1; i++) {
            drone.parseDecision(cpW.createDecision(drone));
        }
        cpW.checkDrone(drone);
        assertEquals(drone.getPreviousDecision(), Action.TURN_RIGHT);
        assertEquals(cpW.getDroneDirection(), Direction.E);
    }

    @Test
    public void testCarvePerimeterEnd() {
        for(int j = 0; j < 2; j++) {
            for(int i = 0; i <= cpW.getHorizontalFlyActions() * 2 + 1; i++) {
                drone.parseDecision(cpW.createDecision(drone));
            }
            cpW.checkDrone(drone);
            for(int i = 0; i <= cpW.getVerticalFlyActions() * 2 + 1; i++) {
                drone.parseDecision(cpW.createDecision(drone));
            }
            cpW.checkDrone(drone);
        }
        assertTrue(cpW.isFinished());
        assertEquals(cpW.getDroneX(), 18 - 2);
        assertEquals(cpW.getDroneY(), 31 - 1);
    }

    @Test
    public void testDroneXYUpdate() {
        drone.parseDecision(cpW.createDecision(drone));
        cpW.checkDrone(drone);
        assertEquals(drone.getPreviousDecision(), Action.ECHO_RIGHT);
        drone.parseDecision(cpW.createDecision(drone));
        cpW.checkDrone(drone);
        assertEquals(drone.getPreviousDecision(), Action.FLY);
        assertNotEquals(cpW.getDroneX(), 18 - 2);
        assertEquals(cpW.getDroneY(), 31 - 1);

        drone.parseDecision(cpN.createDecision(drone));
        cpN.checkDrone(drone);
        assertEquals(drone.getPreviousDecision(), Action.ECHO_RIGHT);
        drone.parseDecision(cpN.createDecision(drone));
        cpN.checkDrone(drone);
        assertEquals(drone.getPreviousDecision(), Action.FLY);
        assertEquals(cpN.getDroneX(), 0);
        assertNotEquals(cpN.getDroneY(), 10 - 2);
    }

}
