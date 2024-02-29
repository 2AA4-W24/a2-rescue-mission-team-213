package ca.mcmaster.se2aa4.island.team213;

import org.apache.logging.log4j.core.parser.JsonLogEventParser;
import org.json.JSONArray;
import org.json.JSONObject;

public class AreaFinder {
    private int length, width;
    private boolean a, b, c, d; // major phases
    private boolean movedForward, scanned, echoedLeft, echoedRight; // minor phases
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
                decision.put("action", "fly");
                movedForward = true;
            } else if(!scanned) {
                decision.put("action", "scan");
                scanned = true;
            } else if(!echoedLeft) {
                decision.put("action", "echo");
                JSONObject parameter = new JSONObject();
                parameter.put("direction", this.direction.leftTurn());
                decision.put("parameters", parameter);
                echoedLeft = true;
            } else if(!echoedRight) {
                decision.put("action", "echo");
                JSONObject parameter = new JSONObject();
                parameter.put("direction", this.direction.rightTurn());
                decision.put("parameters", parameter);
                echoedRight = true;
            }
        }

        return decision;
    }

    private void receiveResult() {
        JSONArray biomes = scanInfo.getJSONObject("extras").getJSONArray("biomes");
    }
}
