package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public interface Phase {

    boolean isFinished();

    JSONObject createDecision();

    void receiveResult(Drone drone);

    Phase nextPhase();

}
