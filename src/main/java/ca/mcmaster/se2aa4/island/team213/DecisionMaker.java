package ca.mcmaster.se2aa4.island.team213;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DecisionMaker {
    private final Logger logger = LogManager.getLogger();
    private Drone drone;
    private boolean landFound;
    private boolean movedForwardOnce = false;

    public DecisionMaker(String direction, Integer batteryLevel) {
        drone = new Drone(direction, batteryLevel);
    }

    public String makeDecision(Drone drone){
        JSONObject decision = new JSONObject();

        if(!movedForwardOnce) {
            drone.setEcho(Direction.E);
            decision.put("action", "echo");
            JSONObject parameters = new JSONObject();
            parameters.put("direction", "E");
            decision.put("parameters", parameters);
            movedForwardOnce = true;
        } else {
            decision.put("action", "scan");
        }


        logger.info("** Decision: {}",decision.toString());
        return decision.toString();
    }

}