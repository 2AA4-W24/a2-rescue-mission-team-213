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
    private final DecisionMakerInterface findLand  = new LocateIsland();

    private boolean movedForwardOnce = false;

    public DecisionMaker(String direction, Integer batteryLevel) {
        drone = new Drone(direction, batteryLevel);
    }

    public JSONObject decideDecision(Drone drone){
        JSONObject decision = new JSONObject();


        // Need to check if end condition is met before calling
        // return findLand.makeDecision(drone);


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


//        logger.info("** Decision: {}",decision.toString());
        return decision;
    }

}