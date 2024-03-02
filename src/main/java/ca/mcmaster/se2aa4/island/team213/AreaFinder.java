package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONArray;
import org.json.JSONObject;

public class AreaFinder {
    private int length, width;

    private int edgesFound;
    private boolean firstEdgeFound, subsequentEdgeFound, flyPastDetermined, turnRight; // secondary phases
    private boolean movedForward, scanned, echoedLeft, echoedRight; // tertiary phases

    private String previousDecision;
    private JSONArray scanInfo; 
    private JSONObject leftEcho, rightEcho;
    private Direction direction;

    // Assume drone starting direction is west
    public AreaFinder() {
        this.edgesFound = 0;
        this.length = 0;
        this.width = 1;

        resetSecondaryPhases();
        resetTertiaryPhases();
    }

    private void resetSecondaryPhases() {
        this.firstEdgeFound = false;
        this.subsequentEdgeFound = false;
        this.flyPastDetermined = false;
        this.turnRight = false;
    }

    private void resetTertiaryPhases() {
        this.movedForward = false;
        this.scanned = false;
        this.echoedLeft = false;
        this.echoedRight = false;
    }

    public boolean isFinished() { // primary phase interface method
        if(this.edgesFound == 4) {
            return true;
        }
        return false;
    }

    public JSONObject createDecision() { // primary phase interface method
        JSONObject decision = new JSONObject();

        if(this.turnRight) {
            this.previousDecision = "turnRight";
            this.movedForward = true;
            this.direction = this.direction.rightTurn();

            JSONObject parameter = new JSONObject();
            parameter.put("direction", this.direction);
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
                parameter.put("direction", this.direction.leftTurn());
                decision.put("parameters", parameter);
                decision.put("action", "echo");
            } else if(!this.echoedRight) {
                this.previousDecision = "echoRight";
                this.echoedRight = true;

                JSONObject parameter = new JSONObject();
                parameter.put("direction", this.direction.rightTurn());
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
                parameter.put("direction", this.direction.rightTurn());
                decision.put("parameters", parameter);
                decision.put("action", "echo");
            }
        }
        else if(!this.flyPastDetermined) {
            decision.put("action", "fly");
        }

        return decision;
    }

    public void receiveResult(JSONObject response) { // primary phase interface method
        if(this.previousDecision.equals("fly")) {
            
        }
        else if (this.previousDecision.equals("scan")) 
        {
            this.scanInfo = response.getJSONObject("extras").getJSONArray("biomes");
        }
        else if(this.previousDecision.equals("echoLeft"))
        {
            this.leftEcho = response.getJSONObject("extras");
        }
        else if(this.previousDecision.equals("echoRight"))
        {
            this.rightEcho = response.getJSONObject("extras");
            checkScanAndEchoes();
        }
    }

    private void checkScanAndEchoes() {
        if(!this.firstEdgeFound) {
            if(this.scanInfo.length() == 1 && this.scanInfo.getString(0).equals("BEACH") && this.leftEcho.getString("found").equals("OUT_OF_RANGE") && this.rightEcho.getString("found").equals("OUT_OF_RANGE")) {
                this.firstEdgeFound = true;
                this.edgesFound += 1;

                this.turnRight = true;
                this.length += 1;
            } 
            this.length += 1;
            resetTertiaryPhases();
        }
        else {
            if(this.rightEcho.getString("found").equals("OUT_OF_RANGE")) {
                this.subsequentEdgeFound = true;
                this.edgesFound += 1;

                this.turnRight = true;
                this.width += 1;
            }
            this.width += 1;
            resetTertiaryPhases();
        }
    }
}
