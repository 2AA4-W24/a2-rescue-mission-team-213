package ca.mcmaster.se2aa4.island.team213.enums;

import org.json.JSONObject;

public enum Action {
    TURN_RIGHT, TURN_LEFT, ECHO_RIGHT, ECHO_LEFT, ECHO_AHEAD, SCAN, FLY, STOP;
    public JSONObject toJSON(Direction direction) {
        JSONObject decision = new JSONObject();
        JSONObject parameter = new JSONObject();

        switch (this) {
            case FLY -> {
                decision.put("action", "fly");
            }
            case SCAN -> {
                decision.put("action", "scan");
            }
            case ECHO_LEFT -> {
                parameter.put("direction", direction.leftTurn().toString());
                decision.put("parameters", parameter);
                decision.put("action", "echo");
            }
            case ECHO_RIGHT -> {
                parameter.put("direction", direction.rightTurn().toString());
                decision.put("parameters", parameter);
                decision.put("action", "echo");
            }
            case ECHO_AHEAD -> {
                parameter.put("direction", direction.toString());
                decision.put("parameters", parameter);
                decision.put("action", "echo");
            }
            case TURN_LEFT -> {
                parameter.put("direction", direction.leftTurn().toString());
                decision.put("parameters", parameter);
                decision.put("action", "heading");
            }
            case TURN_RIGHT -> {
                parameter.put("direction", direction.rightTurn().toString());
                decision.put("parameters", parameter);
                decision.put("action", "heading");
            }
            case STOP -> {
                decision.put("action", "stop");
            }
        }
        return decision;
    }
}
