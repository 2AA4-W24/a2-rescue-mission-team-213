package ca.mcmaster.se2aa4.island.team213.dronephases.edgefinding;

import java.util.LinkedList;
import java.util.Queue;

import ca.mcmaster.se2aa4.island.team213.*;
import ca.mcmaster.se2aa4.island.team213.dronephases.Phase;
import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Biome;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import ca.mcmaster.se2aa4.island.team213.enums.EchoResult;

import org.json.JSONArray;
import org.json.JSONObject;

public class FindFirstEdge implements Phase {
    private boolean isFinished;
    private boolean increaseX;
    private int islandX, islandY;
    private Queue<Action> decisionQueue = new LinkedList<Action>();



    public FindFirstEdge(Direction droneDirection) {
        this.isFinished = false;

        parseStartingDirection(droneDirection);
        loadDecisionQueue();
    }

    private void parseStartingDirection(Direction droneDirection) {
        if(droneDirection.equals(Direction.N) || droneDirection.equals(Direction.S)) {
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
    public boolean isFinished() {
        return this.isFinished;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision;
        Action nextAction = this.decisionQueue.peek();

        decision = nextAction.toJSON(drone.getDirection());
        if(nextAction.equals(Action.TURN_RIGHT)) {
            this.isFinished = true;
        }
        this.decisionQueue.remove();

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
        return new FindSubsequentEdge(this.islandX, this.islandY, !this.increaseX, 1);
    }

    private void checkScanAndEchoes(Drone drone) {
        JSONArray biomes = drone.getScanInfoBiomes();
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

    public Action getNextDecision() {
        return this.decisionQueue.peek();
    }
}