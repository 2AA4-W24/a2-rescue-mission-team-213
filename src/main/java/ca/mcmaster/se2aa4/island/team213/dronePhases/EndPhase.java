package ca.mcmaster.se2aa4.island.team213.dronePhases;

import ca.mcmaster.se2aa4.island.team213.Drone;
import org.json.JSONObject;

public class EndPhase implements Phase {
    boolean isFinished = true;


    @Override
    public boolean lastPhase(){
        return true;
    }

    @Override
    public boolean isFinished() {
        return this.isFinished;
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
        this.isFinished = true;
    }

    @Override
    public Phase nextPhase() {
        return new EndPhase();
    }


}
