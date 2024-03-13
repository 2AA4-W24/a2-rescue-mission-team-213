package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public class EndPhase implements Phase {


    @Override
    public boolean lastPhase(){
        return true;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision = new JSONObject();
        decision.put("action", "stop");
        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        // Left empty, as this is a placeholder phase to avoid null phases in phase queues
    }

    @Override
    public Phase nextPhase() {
        return new EndPhase();
    }
    
}
