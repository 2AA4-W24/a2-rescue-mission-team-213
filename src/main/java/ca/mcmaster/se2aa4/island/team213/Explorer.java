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

    private Phase phase = new LocateIsland();

    @Override
    public void initialize(String s) {
        config = new Configuration(s);
        decisionMaker = new DecisionMaker();
        drone = new Drone(config.getDirection(), config.getBatteryLevel());
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();

        /*
         * Stops if it is in last phase and phase is finished
         */

        if(phase.lastPhase() && phase.isFinished()) {
            decision.put("action", "stop");
        }
        /*
         * If current phase is finished initialize next phase
         */
        else if(phase.isFinished()) {
            phase = phase.nextPhase();
            decision = phase.createDecision(drone);
        }
        else {
            decision = phase.createDecision(drone);
        }

        // decision = decisionMaker.decideDecision(drone);
        // logger.info("reached before");
        drone.parseDecision(decision);
        // logger.info("reached after");
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** RESPONSE: " + response.toString());
        drone.updateStatus(response);
        phase.checkDrone(drone);
    }

    @Override
    public String deliverFinalReport() {
        return drone.getSiteID();
    }

}
