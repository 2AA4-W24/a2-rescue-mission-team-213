package ca.mcmaster.se2aa4.island.team213.dronephases;

import ca.mcmaster.se2aa4.island.team213.Drone;
import org.json.JSONObject;

public interface Phase {

    boolean isFinished();

    JSONObject createDecision(Drone drone);

    void checkDrone(Drone drone);

    Phase nextPhase();

}
