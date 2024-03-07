package ca.mcmaster.se2aa4.island.team213;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class FindSubsequentEdge implements Phase {
    private boolean isFinished;
    private boolean echoedRight, movedForward;
    private boolean turnRight;

    private final Logger logger = LogManager.getLogger();

    public FindSubsequentEdge() {
        this.isFinished = false;

        this.turnRight = false;
        this.movedForward = true;
        this.echoedRight = false;
    }

    @Override
    public boolean isFinished() {
        return this.isFinished;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision = new JSONObject();

        if(this.turnRight) {
            
            this.isFinished = true;
            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().rightTurn());
            decision.put("parameters", parameter);
            decision.put("action", "heading");  
        }
        else if(!this.movedForward) {
            this.movedForward = true;
            decision.put("action", "fly");
        }
        else if(!this.echoedRight) {
            // logger.info("!!!!!! FIND SUB. EDGE REACHED !!!!!!");
            this.echoedRight = true;
            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().rightTurn());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        }
        

        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        if(drone.getPreviousDecision().equals("echoRight")) {
            checkEcho(drone);
        }
    }

    @Override
    public Phase nextPhase() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nextPhase'");
    }

    private void checkEcho(Drone drone) {
        logger.info("** PREVIOUS DECISION: " + drone.getPreviousDecision());
        logger.info("**");
        logger.info("**");
        if(drone.getEchoRight().equals("OUT_OF_RANGE")) {
            // logger.info("turnRight set to true !!!!!!!!!!!!");
            this.turnRight = true;
        }
        resetTertiaryPhases();
    }


    private void resetTertiaryPhases() {
        this.echoedRight = false;
        this.movedForward = false;
    }
}
