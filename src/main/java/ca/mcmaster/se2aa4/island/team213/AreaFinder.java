package ca.mcmaster.se2aa4.island.team213;

import org.apache.logging.log4j.core.parser.JsonLogEventParser;
import org.json.JSONArray;
import org.json.JSONObject;

public class AreaFinder {
    private int length, width;

    private boolean a, b, c, d; // major phases
    private boolean movedForward, scanned, echoedLeft, echoedRight; // minor phases

    private String previousDecision;
    private JSONObject scanInfo, leftEcho, rightEcho;
    private Direction direction;

    // Assume drone is facing west
    public AreaFinder() {
        this.length = 0;
        this.width = 0;
        this.a = true;
        this.b = true;
        this.c = true;
        this.d = true;
    }

    private JSONObject createDecision() {
        JSONObject decision = new JSONObject();

        if(a) {
            if(!movedForward) {
                this.previousDecision = "fly";
                this.movedForward = true;
            } else if(!scanned) {
                this.previousDecision = "scan";
                this.scanned = true;
            } else if(!echoedLeft) {
                this.previousDecision = "echo";
                this.echoedLeft = true;

                JSONObject parameter = new JSONObject();
                parameter.put("direction", this.direction.leftTurn());
                decision.put("parameters", parameter);
            } else if(!echoedRight) {
                this.previousDecision = "echo";
                this.echoedRight = true;

                JSONObject parameter = new JSONObject();
                parameter.put("direction", this.direction.rightTurn());
                decision.put("parameters", parameter);                
            }
            decision.put("action", this.previousDecision);
        }

        return decision;
    }

    private void receiveResult(JSONObject response) {
        if(previousDecision.equals("scan")) {
            JSONArray biomes = scanInfo.getJSONObject("extras").getJSONArray("biomes");
        }
        if(previousDecision.equals("echo")) {
            
        }
    }
}
