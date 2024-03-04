package ca.mcmaster.se2aa4.island.team213;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {
    private DecisionMaker decisionMaker;
    private Drone drone;
    
    private final Logger logger = LogManager.getLogger();
    private Configuration config;

    @Override
    public void initialize(String s) {
        config = new Configuration(s);
        decisionMaker = new DecisionMaker();
        drone = new Drone(config.getDirection(), config.getBatteryLevel());
    }

    @Override
    public String takeDecision() {
        JSONObject decision = decisionMaker.decideDecision(drone);
//        drone.updatePosition(decision);
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        drone.updateStatus(response);
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }

}
