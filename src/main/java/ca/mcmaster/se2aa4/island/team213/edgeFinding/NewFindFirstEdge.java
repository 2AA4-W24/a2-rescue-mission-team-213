package ca.mcmaster.se2aa4.island.team213.edgeFinding;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.Action;
import ca.mcmaster.se2aa4.island.team213.Biome;
import ca.mcmaster.se2aa4.island.team213.Direction;
import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.EchoResult;
import ca.mcmaster.se2aa4.island.team213.Phase;

public class NewFindFirstEdge implements Phase {
    private boolean isFinished;
    private boolean increaseX;
    private int islandX, islandY;

    private Queue<Action> decisionQueue;

    public NewFindFirstEdge(Direction direction) {
        this.isFinished = false;
        this.decisionQueue = new LinkedList<Action>();

        parseStartingDirection(direction);
        loadDecisionQueue();
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
        
        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        if(drone.getPreviousDecision().equals(Action.ECHO_RIGHT)) {
            increaseXorY();
            checkScanAndEchoes(drone);
        }
    }

    @Override
    public Phase nextPhase() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nextPhase'");
    }

    private void checkScanAndEchoes(Drone drone) {
        JSONArray biomes = drone.getScanInfoBiome();
        Biome firstBiome = Biome.valueOf(biomes.getString(0));
        if(biomes.length() == 1 && firstBiome.equals(Biome.OCEAN) && drone.getEchoLeft().equals(EchoResult.OUT_OF_RANGE) && drone.getEchoRight().equals(EchoResult.OUT_OF_RANGE)) {
            increaseXorY();
            this.decisionQueue.add(Action.TURN_RIGHT);
        }
        loadDecisionQueue();
    }
    
    private void loadDecisionQueue() {
        this.decisionQueue.add(Action.FLY);
        this.decisionQueue.add(Action.SCAN);
        this.decisionQueue.add(Action.ECHO_LEFT);
        this.decisionQueue.add(Action.ECHO_RIGHT);
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
