package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public interface Phase {

    boolean isFinished();

    // WILL NOT ACCEPT DRONE IN END PRODUCT
    JSONObject createDecision(Drone drone);

    void checkDrone(Drone drone);

    Phase nextPhase();

}
