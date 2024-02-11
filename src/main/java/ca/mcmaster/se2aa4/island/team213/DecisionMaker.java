package ca.mcmaster.se2aa4.island.team213;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DecisionMaker {
    private final Logger logger = LogManager.getLogger();
    private String direction;
    private Integer batteryLevel;
    private boolean landFound;
    private boolean movedForwardOnce = false;

    public DecisionMaker(String direction, Integer batteryLevel) {
        this.direction = direction;
        this.batteryLevel = batteryLevel;
    }

    public String makeDecision(){
        JSONObject decision = new JSONObject();

        if(!movedForwardOnce) {
            decision.put("action", "fly");
            movedForwardOnce = true;
        } else {
            decision.put("action", "stop");
        }

        logger.info("** Decision: {}",decision.toString());
        return decision.toString();
    }

}