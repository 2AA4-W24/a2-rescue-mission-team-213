package ca.mcmaster.se2aa4.island.team213;

import ca.mcmaster.se2aa4.island.team213.dronephases.edgefinding.FindFirstEdge;
import ca.mcmaster.se2aa4.island.team213.dronephases.flytoisland.LocateIsland;
import ca.mcmaster.se2aa4.island.team213.dronephases.flytoisland.TravelToIsland;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import ca.mcmaster.se2aa4.island.team213.enums.EchoResult;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class FlyToIslandTest {
    private Drone droneA;

    private LocateIsland phaseA;

    private TravelToIsland phaseB;
    private int distanceToLand = 3;

    private JSONObject echoOOBResponse;
    private JSONObject echoGResponse;
    private JSONObject headingResponse;
    private JSONObject flyResponse;

    @BeforeEach
    public void setUp() {
        droneA = new Drone("N", 1000);
        phaseA = new LocateIsland();
        phaseB = new TravelToIsland();

        createEchoOOBResponse();
        createEchoGResponse();
        createHeadingResponse();
        createFlyResponse();
    }

    @Test
    public void testStartStates() {
        assertEquals(droneA.getDirection(), Direction.N);

        assertFalse(phaseA.isFinished());

        assertFalse(phaseB.isFinished());
    }

    @Test
    public void testNullDecision(){
        phaseA.createDecision(droneA);
        assertEquals(phaseA.getDecision().getString("action"), "echo");
        assertEquals(phaseA.getTaskQueue().size(), 2);
        phaseA.createDecision(droneA);
        phaseA.createDecision(droneA);
        assertEquals(phaseA.getTaskQueue().size(), 0);
    }

    @Test
    public void testGoNextIteration(){
        for (int i = 0; i < 3; i++){
            droneA.parseDecision(phaseA.createDecision(droneA));
            droneA.updateStatus(echoOOBResponse);
            phaseA.checkDrone(droneA);
        }
        phaseA.createDecision(droneA);
        assertEquals(phaseA.getDecision().getString("action"), "fly" );


    }

    @Test
    public void testTurnAround(){
        for (int i = 0; i < 3; i++){
            droneA.parseDecision(phaseA.createDecision(droneA));
            droneA.updateStatus(createEchoOOBCloseResponse());
            phaseA.checkDrone(droneA);
        }

        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(headingResponse);
        phaseA.checkDrone(droneA);
        // Ensure we are turning right, and we have another turn right queued after
        assertEquals(phaseA.getDecision().getString("action"), "heading");

        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(headingResponse);
        phaseA.checkDrone(droneA);
        // Ensured we are now 180 degrees, which is facing south since we started by facing north
        assertEquals(droneA.getDirection(), Direction.S);
    }

    @Test
    public void testFinishLocateIsland(){
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoGResponse);
        phaseA.checkDrone(droneA);
        assertTrue(phaseA.isFinished());
    }

    @Test
    public void travelLandForward(){
        // forward - found ground
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoGResponse);
        // right - no ground
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoOOBResponse);
        phaseA.checkDrone(droneA);
        // left - no ground
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoOOBResponse);
        phaseA.checkDrone(droneA);

        droneA.parseDecision(phaseB.createDecision(droneA));
        droneA.updateStatus(flyResponse);
        phaseB.checkDrone(droneA);

        while (!phaseB.getTaskQueue().isEmpty()){
            droneA.parseDecision(phaseB.createDecision(droneA));
            droneA.updateStatus(flyResponse);
            phaseA.checkDrone(droneA);
        }

        Optional<Integer> rangeAheadOptional = Optional.ofNullable(droneA.getRangeAhead());
        assertTrue(rangeAheadOptional.isPresent());
        assertEquals(0, rangeAheadOptional.get().intValue());

        Optional<EchoResult> echoAheadOptional = Optional.ofNullable(droneA.getEchoAhead());
        assertTrue(echoAheadOptional.isPresent());
        assertEquals(EchoResult.GROUND, echoAheadOptional.get());


        assertTrue(phaseB.isFinished());
    }
    @Test
    public void travelLandRight(){
        // forward - no ground
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoOOBResponse);
        // right - found ground
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoGResponse);
        phaseA.checkDrone(droneA);
        // left - no ground
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoOOBResponse);
        phaseA.checkDrone(droneA);

        droneA.parseDecision(phaseB.createDecision(droneA));
        droneA.updateStatus(headingResponse);
        phaseB.checkDrone(droneA);

        while (!phaseB.getTaskQueue().isEmpty()){
            droneA.parseDecision(phaseB.createDecision(droneA));

            droneA.updateStatus(flyResponse);
            phaseB.checkDrone(droneA);
        }

        assertTrue(phaseB.isFinished());


    }
    @Test
    public void travelLandLeft(){
        // forward - no ground
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoOOBResponse);
        // right - no ground
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoOOBResponse);
        phaseA.checkDrone(droneA);
        // left - found ground
        droneA.parseDecision(phaseA.createDecision(droneA));
        droneA.updateStatus(echoGResponse);
        phaseA.checkDrone(droneA);

        droneA.parseDecision(phaseB.createDecision(droneA));
        droneA.updateStatus(headingResponse);
        phaseB.checkDrone(droneA);

        while (!phaseB.getTaskQueue().isEmpty()){
            droneA.parseDecision(phaseB.createDecision(droneA));

            droneA.updateStatus(flyResponse);
            phaseB.checkDrone(droneA);
        }

        assertTrue(phaseB.isFinished());
    }

    @Test
    public void ensureNextPhase(){
        assertTrue(phaseA.nextPhase() instanceof TravelToIsland);
        phaseB.checkDrone(droneA);
        assertTrue(phaseB.nextPhase() instanceof FindFirstEdge);
    }



    private void createHeadingResponse(){
        headingResponse = new JSONObject();
        JSONObject extras = new JSONObject();
        headingResponse.put("extras", extras);
        headingResponse.put("cost", 1);
        headingResponse.put("status", "OK");
    }

    private void createFlyResponse(){
        flyResponse = new JSONObject();
        JSONObject extras = new JSONObject();
        flyResponse.put("extras", extras);
        flyResponse.put("cost", 1);
        flyResponse.put("status", "OK");
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
    private JSONObject createEchoOOBCloseResponse(){
        JSONObject response = new JSONObject();
        JSONObject extras = new JSONObject();
        extras.put("found", "OUT_OF_RANGE");
        extras.put("range", 1);

        response.put("extras", extras);
        response.put("cost", 1);
        response.put("status", "OK");
        return response;
    }

    private void createEchoGResponse() {
        echoGResponse = new JSONObject();
        JSONObject extras = new JSONObject();
        extras.put("found", "GROUND");
        extras.put("range", distanceToLand);

        echoGResponse.put("extras", extras);
        echoGResponse.put("cost", 1);
        echoGResponse.put("status", "OK");
    }

}
