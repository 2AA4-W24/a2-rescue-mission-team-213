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
    private final Phase findLand  = new LocateIsland();

    private boolean movedForwardOnce = false;

    public DecisionMaker() {
        
    }

    public JSONObject decideDecision(Drone drone){
        JSONObject decision = new JSONObject();


        // Need to check if end condition is met before calling
        JSONObject hi =  findLand.createDecision(drone);
        logger.info(hi.toString());

        return hi;


//        if(!movedForwardOnce) {
//            drone.setEcho(Direction.E);
//            decision.put("action", "echo");
//            JSONObject parameters = new JSONObject();
//            parameters.put("direction", "E");
//            decision.put("parameters", parameters);
//            movedForwardOnce = true;
//        } else {
//            decision.put("action", "scan");
//        }


//        logger.info("** Decision: {}",decision.toString());
//        return decision;
    }

}