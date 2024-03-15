package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public enum Action {
    TURN_RIGHT, TURN_LEFT, ECHO_RIGHT, ECHO_LEFT, ECHO_AHEAD, SCAN, FLY, STOP;

    public JSONObject toJSON(Direction direction) {
        JSONObject decision = new JSONObject();
        JSONObject parameter = new JSONObject();

        if(this.equals(Action.FLY)) {
            decision.put("action", "fly");
        }
        else if(this.equals(Action.SCAN)) {
            decision.put("action", "scan");
        }
        else if(this.equals(Action.ECHO_LEFT)) {
            parameter.put("direction", direction.leftTurn().toString());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        }
        else if(this.equals(Action.ECHO_RIGHT)) {
            parameter.put("direction", direction.rightTurn().toString());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        }
        else if(this.equals(Action.ECHO_AHEAD)) {
            parameter.put("direction", direction.toString());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        }
        else if(this.equals(Action.TURN_LEFT)) {
            parameter.put("direction", direction.leftTurn().toString());
            decision.put("parameters", parameter);
            decision.put("action", "heading");  
        }
        else if(this.equals(Action.TURN_RIGHT)) {
            parameter.put("direction", direction.rightTurn().toString());
            decision.put("parameters", parameter);
            decision.put("action", "heading");  
        }
        return decision;
    }
}
