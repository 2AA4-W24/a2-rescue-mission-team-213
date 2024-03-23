package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team213.dronephases.carveperimeter.CarvePerimeter;

public class CarvePerimeterTest {
    JSONObject echoGResponse, echoOOBResponse;
    CarvePerimeter cpN, cpE, cpS, cpW;
    Drone dN, dE, dS, dW;

    @BeforeEach
    public void setUp() {
        cpN = new CarvePerimeter(10, 10, Direction.N);
        cpE = new CarvePerimeter(10, 10, Direction.E);
        cpS = new CarvePerimeter(10, 10, Direction.S);
        cpW = new CarvePerimeter(10, 10, Direction.W);
        dN = new Drone("N", 50000);
        dE = new Drone("E", 50000);
        dS = new Drone("S", 50000);
        dW = new Drone("W", 50000);
        createEchoGResponse();
        createEchoOOBResponse();
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

    private void createEchoOOBResponse() {
        echoOOBResponse = new JSONObject();
        JSONObject extras = new JSONObject();
        extras.put("found", "OUT_OF_RANGE");
        extras.put("range", 30); 

        echoOOBResponse.put("extras", extras);
        echoOOBResponse.put("cost", 1);
        echoOOBResponse.put("status", "OK");
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
    public void testGroundEcho() {
        dN.parseDecision(cpN.createDecision(dN));
        dN.updateStatus(echoGResponse);
        cpN.checkDrone(dN);

        dE.parseDecision(cpE.createDecision(dE));
        dE.updateStatus(echoGResponse);
        cpE.checkDrone(dE);

        dS.parseDecision(cpS.createDecision(dS));
        dS.updateStatus(echoGResponse);
        cpS.checkDrone(dS);

        dW.parseDecision(cpW.createDecision(dW));
        dW.updateStatus(echoGResponse);
        cpW.checkDrone(dW);
    }

    @Test
    public void testOOBEcho() {
        dN.parseDecision(cpN.createDecision(dN));
        dN.updateStatus(echoOOBResponse);
        cpN.checkDrone(dN);

        dE.parseDecision(cpE.createDecision(dE));
        dE.updateStatus(echoOOBResponse);
        cpE.checkDrone(dE);

        dS.parseDecision(cpS.createDecision(dS));
        dS.updateStatus(echoOOBResponse);
        cpS.checkDrone(dS);

        dW.parseDecision(cpW.createDecision(dW));
        dW.updateStatus(echoOOBResponse);
        cpW.checkDrone(dW);
    }

    @Test
    public void testFly() {
        cpN.createDecision(dN);
        dN.parseDecision(cpN.createDecision(dN));
        dN.updateStatus(echoOOBResponse);
        cpN.checkDrone(dN);

        cpE.createDecision(dE);
        dE.parseDecision(cpE.createDecision(dE));
        dE.updateStatus(echoOOBResponse);
        cpE.checkDrone(dE);

        cpS.createDecision(dS);
        dS.parseDecision(cpS.createDecision(dS));
        dS.updateStatus(echoOOBResponse);
        cpS.checkDrone(dS);

        cpW.createDecision(dW);
        dW.parseDecision(cpW.createDecision(dW));
        dW.updateStatus(echoOOBResponse);
        cpW.checkDrone(dW);
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
