package ca.mcmaster.se2aa4.island.team213.edgeFinding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.Action;
import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.EchoResult;
import ca.mcmaster.se2aa4.island.team213.EndPhase;
import ca.mcmaster.se2aa4.island.team213.Phase;

public class FindSubsequentEdge implements Phase {
    private boolean isFinished;
    private boolean increaseX;
    private int islandX, islandY;
    private int edgesFound;

    private boolean echoedRight, movedForward;
    private boolean turnRight;

    private final Logger logger = LogManager.getLogger();

    public FindSubsequentEdge(int islandX, int islandY, boolean increaseX, int edgesFound) {
        this.isFinished = false;
        this.turnRight = false;

        this.islandX = islandX;
        this.islandY = islandY;
        this.increaseX = increaseX;
        this.edgesFound = edgesFound;

        this.movedForward = true;
        this.echoedRight = false;
    }
    
    @Override
    public boolean lastPhase() {
        return false;
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
        logger.info("** PREVIOUS DECISION: " + drone.getPreviousDecision());
        logger.info("**");
        logger.info("**");
        if(drone.getPreviousDecision().equals(Action.echoRight)) {
            increaseXorY();
            checkEcho(drone);
        }
    }

    @Override
    public Phase nextPhase() {
        if(this.edgesFound == 3) {
            return new EndPhase();
        }

        int flyActionsLeft = !this.increaseX ? this.islandX : this.islandY;
        return new FlyPastDetermined(this.islandX, this.islandY, !this.increaseX, this.edgesFound + 1, flyActionsLeft);
    }

    private void checkEcho(Drone drone) {
        if(drone.getEchoRight().equals(EchoResult.OUT_OF_RANGE)) {
            increaseXorY();
            this.turnRight = true;
        }
        resetTertiaryPhases();
    }
 
    private void resetTertiaryPhases() {
        this.echoedRight = false;
        this.movedForward = false;
    }

    private void increaseXorY() { 
        if(this.increaseX) {
            this.islandX += 1;
        } 
        else {
            this.islandY += 1;
        }
    }

}
