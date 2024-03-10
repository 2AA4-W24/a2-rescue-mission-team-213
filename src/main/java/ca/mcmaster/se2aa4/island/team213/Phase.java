package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public interface Phase {

    boolean lastPhase();

    boolean isFinished();

    JSONObject createDecision(Drone drone);

    void checkDrone(Drone drone);

    Phase nextPhase();

}
