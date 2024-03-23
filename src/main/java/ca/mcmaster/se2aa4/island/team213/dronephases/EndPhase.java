package ca.mcmaster.se2aa4.island.team213.dronephases;

import ca.mcmaster.se2aa4.island.team213.Drone;
import org.json.JSONObject;

public class EndPhase implements Phase {
    boolean isFinished = false;

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
        this.isFinished = true;
    }

    @Override
    public Phase nextPhase() {
        return new EndPhase();
    }


}
