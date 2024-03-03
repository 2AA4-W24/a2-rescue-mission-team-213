package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public class FindSecondEdge implements Phase {
    private boolean isFinished;
    private boolean turnedRight, echoedRight, movedForward;

    public FindSecondEdge() {
        this.isFinished = false;
        this.turnedRight = false;
        resetTertiaryPhases();
    }

    @Override
    public boolean isFinished() {
        return this.isFinished;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision = new JSONObject();

        if(!this.turnedRight) {
            this.turnedRight = true;
            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().rightTurn());
            decision.put("parameters", parameter);
            decision.put("action", "heading");  
        } 
        else if(!this.echoedRight) {
            this.echoedRight = true;
            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().rightTurn());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        } 
        else if(!this.movedForward) {
            this.movedForward = true;
            decision.put("action", "fly");
        }

        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        if(drone.getPreviousDecision().equals("echoRight")) {
            checkEcho(drone);
        }
    }

    @Override
    public Phase nextPhase() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nextPhase'");
    }
    
    private void checkEcho(Drone drone) {
        if(drone.getEchoRight().equals(EchoResult.OUT_OF_RANGE)) {
            this.isFinished = true;
            // increase x or y
            // swap x or y
        }
        resetTertiaryPhases();
        // increase x or y
    }

    private void resetTertiaryPhases() {
        this.echoedRight = false;
        this.movedForward = false;
    }
}
