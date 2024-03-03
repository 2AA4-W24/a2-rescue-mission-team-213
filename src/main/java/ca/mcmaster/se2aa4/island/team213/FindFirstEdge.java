package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public class FindFirstEdge implements Phase {
    private boolean isFinished;
    private Phase nextPhase;
    private boolean movedForward, scanned, echoedLeft, echoedRight;
    
    public FindFirstEdge(Phase nextPhase) {
        this.isFinished = false;
        this.nextPhase = nextPhase;
    }

    @Override
    public boolean isFinished() {
        return this.isFinished;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision = new JSONObject();

        if(!this.movedForward) {
            this.movedForward = true;
            decision.put("action", "fly");
        } 
        else if(!this.scanned) {
            this.scanned = true;
            decision.put("action", "scan");
        } 
        else if(!this.echoedLeft) {
            this.echoedLeft = true;
            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().leftTurn());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        } 
        else if(!this.echoedRight) {
            this.echoedRight = true;
            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().rightTurn());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        }

        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        if(drone.getPreviousDecision().equals("echoRight")) {
            checkScanAndEchoes(drone);
        }
    }

    @Override
    public Phase nextPhase() {
        return this.nextPhase;
    }
    
    private void checkScanAndEchoes(Drone drone) {
        if(drone.getScanInfo().length() == 1 && drone.getScanInfo().getString(0).equals("BEACH") && drone.getEchoLeft().equals(EchoResult.OUT_OF_RANGE) && drone.getEchoRight().equals(EchoResult.OUT_OF_RANGE)) {
            this.isFinished = true;
        }
    }
}
