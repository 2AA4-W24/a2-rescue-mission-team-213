package ca.mcmaster.se2aa4.island.team213.edgeFinding;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.Action;
import ca.mcmaster.se2aa4.island.team213.Direction;


public class DecisionJSONs {
    
    public static JSONObject actionToJSONObject(Action action, Direction direction) {
        JSONObject decision = new JSONObject();
        JSONObject parameter = new JSONObject();

        if(action.equals(Action.FLY)) {
            decision.put("action", "fly");
        }
        else if(action.equals(Action.SCAN)) {
            decision.put("action", "scan");
        }
        else if(action.equals(Action.ECHO_LEFT)) {
            parameter.put("direction", direction.leftTurn().toString());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        }
        else if(action.equals(Action.ECHO_RIGHT)) {
            parameter.put("direction", direction.rightTurn().toString());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        }
        else if(action.equals(Action.ECHO_AHEAD)) {
            parameter.put("direction", direction.toString());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        }
        else if(action.equals(Action.TURN_LEFT)) {
            parameter.put("direction", direction.leftTurn().toString());
            decision.put("parameters", parameter);
            decision.put("action", "heading");  
        }
        else if(action.equals(Action.TURN_RIGHT)) {
            parameter.put("direction", direction.rightTurn().toString());
            decision.put("parameters", parameter);
            decision.put("action", "heading");  
        }
        
        return decision;
    }

}
