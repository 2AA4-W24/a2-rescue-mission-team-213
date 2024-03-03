package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONArray;
import org.json.JSONObject;

public class AreaFinder implements Phase {
    private int x, y; // stays in AreaFinder
    private int edgesFound; // stays in AreaFinder

    private boolean firstEdgeFound, subsequentEdgeFound, flyPastDetermined, turnRight; // secondary phases
    private boolean movedForward, scanned, echoedLeft, echoedRight; // tertiary phases

    private String previousDecision;
    private boolean increaseX; // stays in AreaFinder
    private int flyActionsLeft;

    private Drone drone;

    public AreaFinder(Drone drone) {
        this.drone = drone;
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
            this.previousDecision = "turnRight";
            this.movedForward = true;

            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().rightTurn());
            decision.put("parameters", parameter);
            decision.put("action", "heading");

            this.turnRight = false;
        }
        else if(!this.firstEdgeFound) {
            if(!this.movedForward) {
                this.previousDecision = "fly";
                this.movedForward = true;

                decision.put("action", previousDecision);
            } else if(!this.scanned) {
                this.previousDecision = "scan";
                this.scanned = true;

                decision.put("action", this.previousDecision);
            } else if(!this.echoedLeft) {
                this.previousDecision = "echoLeft";
                this.echoedLeft = true;

                JSONObject parameter = new JSONObject();
                parameter.put("direction", drone.getDirection().leftTurn());
                decision.put("parameters", parameter);
                decision.put("action", "echo");
            } else if(!this.echoedRight) {
                this.previousDecision = "echoRight";
                this.echoedRight = true;

                JSONObject parameter = new JSONObject();
                parameter.put("direction", drone.getDirection().rightTurn());
                decision.put("parameters", parameter);
                decision.put("action", "echo");
            }
        } 
        else if(!this.subsequentEdgeFound) {
            if(!this.movedForward) {
                this.previousDecision = "fly";
                this.movedForward = true;

                decision.put("action", this.previousDecision);
            } else if(!this.echoedRight) {
                this.previousDecision = "echoRight";
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
    public void receiveResult(Drone drone) {                        // primary phase interface method
        if(this.subsequentEdgeFound && this.previousDecision.equals("fly")) {
            checkFly();
        }
        else if(this.previousDecision.equals("echoRight"))
        {
            checkScanAndEchoes(drone);
        }
    }

    @Override
    public Phase nextPhase() {
        return new TestPhase();
    }

    private void checkScanAndEchoes(Drone drone) { // firstEdgeFound and subsequentEdgeFound method
        if(!this.firstEdgeFound) {
            if(this.scanInfo.length() == 1 && this.scanInfo.getString(0).equals("BEACH") && this.leftEcho.getString("found").equals("OUT_OF_RANGE") && this.rightEcho.getString("found").equals("OUT_OF_RANGE")) {
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
            if(this.rightEcho.getString("found").equals("OUT_OF_RANGE")) {
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
