package ca.mcmaster.se2aa4.island.team213;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DecisionMaker {

    private final Logger logger = LogManager.getLogger();

    public String makeDecision(){
        JSONObject decision = new JSONObject();
        decision.put("action", "stop"); // we stop the exploration immediately
        logger.info("** Decision: {}",decision.toString());
        return decision.toString();
    }

}