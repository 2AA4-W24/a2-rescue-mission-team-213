package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public class FindArea implements Phase {
    private int x, y; // stays in AreaFinder
    private boolean increaseX; // stays in AreaFinder
    private int edgesFound; // stays in AreaFinder
    private int flyActionsLeft; // goes in flyPastDetermined

    private boolean firstEdgeFound, subsequentEdgeFound, flyPastDetermined, turnRight; // secondary phases
    private boolean movedForward, scanned, echoedLeft, echoedRight; // tertiary phases

    public FindArea(Drone drone) {
        this.edgesFound = 0;

        parseStartingDirection(drone.getDirection());
        resetSecondaryPhases();
        resetTertiaryPhases();
    }

    private void parseStartingDirection(Direction direction) {
        if(direction.toString().equals("N") || direction.toString().equals("S")) {
            this.increaseX = false;
            this.x = 1;
            this.y = 0;
        } else {
            this.increaseX = true;
            this.x = 0;
            this.y = 1;
        }
    }

    private void resetSecondaryPhases() { // interface will handle
        if(edgesFound == 0) {
            this.firstEdgeFound = false;
        }
        this.subsequentEdgeFound = false;
        this.flyPastDetermined = false;
        this.turnRight = false;
    }

    private void resetTertiaryPhases() { // interface will handle
        this.movedForward = false;
        this.scanned = false;
        this.echoedLeft = false;
        this.echoedRight = false;
    }

    @Override
    public boolean isFinished() {                                           // primary phase interface method
        if(this.edgesFound == 4) {
            return true;
        }
        return false;
    }

    @Override
    public JSONObject createDecision(Drone drone) {                                    // primary phase interface method
        JSONObject decision = new JSONObject();

        if(this.turnRight) {
            this.movedForward = true;
            this.turnRight = false;

            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().rightTurn());
            decision.put("parameters", parameter);
            decision.put("action", "heading");            
        }
        else if(!this.firstEdgeFound) {
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
        } 
        else if(!this.subsequentEdgeFound) {
            if(!this.movedForward) {
                this.movedForward = true;
                decision.put("action", "fly");
            } 
            else if(!this.echoedRight) {
                this.echoedRight = true;
                JSONObject parameter = new JSONObject();
                parameter.put("direction", drone.getDirection().rightTurn());
                decision.put("parameters", parameter);
                decision.put("action", "echo");
            }
        }
        else if(!this.flyPastDetermined) {
            decision.put("action", "fly");
        }

        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {                                // primary phase interface method
        if(this.subsequentEdgeFound && drone.getPreviousDecision().equals("fly")) {
            checkFly();
        }
        else if(drone.getPreviousDecision().equals("echoRight")) {
            checkScanAndEchoes(drone);
        }
    }

    @Override
    public Phase nextPhase() {
        return new TestPhase();
    }

    private void checkScanAndEchoes(Drone drone) { // firstEdgeFound and subsequentEdgeFound method
        if(!this.firstEdgeFound) {
            if(drone.getScanInfo().length() == 1 && drone.getScanInfo().getString(0).equals("BEACH") && drone.getEchoLeft().equals(EchoResult.OUT_OF_RANGE) && drone.getEchoRight().equals(EchoResult.OUT_OF_RANGE)) {
                this.firstEdgeFound = true;
                this.turnRight = true;
                this.edgesFound += 1;
                
                increaseXorY();
                swapXorY();
            } 
            increaseXorY();
            resetTertiaryPhases();
        }
        else {
            if(drone.getEchoRight().equals(EchoResult.OUT_OF_RANGE)) {
                this.subsequentEdgeFound = true;
                this.turnRight = true;
                this.edgesFound += 1;
                
                increaseXorY();
                swapXorY();
                setFlyActionsLeft();
            }
            increaseXorY();
            resetTertiaryPhases();
        }
    }

    private void checkFly() { // flyPastDetermined method
        this.flyActionsLeft -= 1;
        if(this.flyActionsLeft == 0) {
            resetSecondaryPhases();
        }
    }

    private void increaseXorY() { // firstEdgeFound and subsequentEdgeFound method
        if(this.increaseX) {
            this.x += 1;
        } 
        else {
            this.y += 1;
        }
    }

    private void swapXorY() { // AreaFinder method
        this.increaseX = this.increaseX ? false : true;
    }

    private void setFlyActionsLeft() { // subsequentEdgeFound method
        this.flyActionsLeft = this.increaseX ? this.x - 1: this.y - 1;
    }
}
