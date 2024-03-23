package ca.mcmaster.se2aa4.island.team213;

import java.io.StringReader;

import ca.mcmaster.se2aa4.island.team213.dronephases.flytoisland.LocateIsland;
import ca.mcmaster.se2aa4.island.team213.dronephases.Phase;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {
    private Drone drone;


    private Phase phase = new LocateIsland();

    @Override
    public void initialize(String s) {
        Configuration config = new Configuration(s);
        drone = new Drone(config.getDirection(), config.getBatteryLevel());
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();

        if(drone.getBattery() <= 50) {
            decision.put("action", "stop");
        } else {
            if(phase.isFinished()) {
                phase = phase.nextPhase();
            }
            decision = phase.createDecision(drone);
        }

        drone.parseDecision(decision);
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));

        drone.updateStatus(response);
        phase.checkDrone(drone);
    }

    @Override
    public String deliverFinalReport() {
        return drone.getCreekID();
    }

}
