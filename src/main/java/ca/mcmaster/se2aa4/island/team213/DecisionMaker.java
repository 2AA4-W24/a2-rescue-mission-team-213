package ca.mcmaster.se2aa4.island.team213;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DecisionMaker {
    private final Logger logger = LogManager.getLogger();
    private final Phase findLand = new LocateIsland();

    private Phase findEdges;
    private boolean findEdgesInitialized;

    public DecisionMaker() {
        findEdgesInitialized = false;
    }

    public JSONObject decideDecision(Drone drone){
        JSONObject decision = new JSONObject();
        


        // Need to check if end condition is met before calling
        if(!findLand.isFinished()) {
            decision = findLand.createDecision(drone);
            logger.info(findLand.isFinished());
        }
//        else if(!findEdgesInitialized) {
//            logger.info("!!!!!!!!!!!!!!!!!! FINISHED PHASE 1 !!!!!!!!!!!!!!!!!!");
//            findEdges = new FindEdges(drone);
//            findEdgesInitialized = true;
//            decision = findEdges.createDecision(drone);
//        } else if(findEdgesInitialized) {
//            if(!findEdges.isFinished()) {
//                decision = findEdges.createDecision(drone);
//            } else {
//                decision.put("action", "stop");
//            }
//        }

        logger.info("** DECISION: " + decision.toString());
        return decision;
    }

    public void sendDroneToPhase(Drone drone) {
        if(findEdgesInitialized) {
            findEdges.checkDrone(drone);
        }
    }

}