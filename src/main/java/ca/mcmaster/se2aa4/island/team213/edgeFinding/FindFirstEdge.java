package ca.mcmaster.se2aa4.island.team213.edgeFinding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.Action;
import ca.mcmaster.se2aa4.island.team213.Biome;
import ca.mcmaster.se2aa4.island.team213.Direction;
import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.EchoResult;
import ca.mcmaster.se2aa4.island.team213.Phase;

public class FindFirstEdge implements Phase {
    private boolean isFinished;
    private boolean increaseX;
    private int islandX, islandY;

    private boolean movedForward, scanned, echoedLeft, echoedRight;
    private boolean turnRight;

    private final Logger logger = LogManager.getLogger();

    public FindFirstEdge(Direction direction) {
        this.isFinished = false;
        this.turnRight = false;

        resetTertiaryPhases();
        parseStartingDirection(direction);
    }

    private void parseStartingDirection(Direction direction) {
        if(direction.equals(Direction.N) || direction.equals(Direction.S)) {
            this.increaseX = false;
            this.islandX = 1;
            this.islandY = 0;
        } else {
            this.increaseX = true;
            this.islandX = 0;
            this.islandY = 1;
        }
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
            logger.info("** MOVING FORWARD IN FIND FIRST EDGE **");
            this.movedForward = true;
            decision.put("action", "fly");
        } 
        else if(!this.scanned) {
            this.scanned = true;
            decision.put("action", "scan");
        } 
        else if(!this.echoedLeft) {
            this.echoedLeft = true;
            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().leftTurn().toString());
            decision.put("parameters", parameter);
            decision.put("action", "echo");
        } 
        else if(!this.echoedRight) {
            this.echoedRight = true;
            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().rightTurn().toString());
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
            checkScanAndEchoes(drone);
        }
    }

    @Override
    public Phase nextPhase() {
        return new FindSubsequentEdge(this.islandX, this.islandY, !this.increaseX, 1);
    }

    private void checkScanAndEchoes(Drone drone) {
        JSONArray biomes = drone.getScanInfoBiome();
        Biome firstBiome = Biome.valueOf(biomes.getString(0));
        if(biomes.length() == 1 && firstBiome.equals(Biome.OCEAN) && drone.getEchoLeft().equals(EchoResult.OUT_OF_RANGE) && drone.getEchoRight().equals(EchoResult.OUT_OF_RANGE)) {
            increaseXorY();
            this.turnRight = true;
        }
        resetTertiaryPhases();
    }

    private void resetTertiaryPhases() {
        this.movedForward = false;
        this.scanned = false;
        this.echoedLeft = false;
        this.echoedRight = false;
    }

    private void increaseXorY() { 
        if(this.increaseX) {
            this.islandX += 1;
        } 
        else {
            this.islandY += 1;
        }
    }

    public boolean getIncreaseX() {
        return this.increaseX;
    }
}
