package ca.mcmaster.se2aa4.island.team213.dronephases.edgefinding;

import java.util.LinkedList;
import java.util.Queue;

import ca.mcmaster.se2aa4.island.team213.*;
import ca.mcmaster.se2aa4.island.team213.dronephases.Phase;
import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import ca.mcmaster.se2aa4.island.team213.enums.EchoResult;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.dronephases.carveperimeter.CarvePerimeter;

public class FindSubsequentEdge implements Phase {
    private boolean isFinished;
    private boolean increaseX;
    private int islandX;
    private int islandY;
    private int edgesFound;
    private Direction droneDirection;
    private Queue<Action> decisionQueue;


    public FindSubsequentEdge(int islandX, int islandY, boolean increaseX, int edgesFound) {
        this.isFinished = false;
        this.decisionQueue = new LinkedList<Action>();
        this.decisionQueue.add(Action.ECHO_RIGHT);
        this.islandX = islandX;
        this.islandY = islandY;
        this.increaseX = increaseX;
        this.edgesFound = edgesFound;
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
            this.droneDirection = drone.getDirection().rightTurn();
        }
        this.decisionQueue.remove();

        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        if(drone.getPreviousDecision().equals(Action.ECHO_RIGHT)) {
            increaseXorY();
            checkEcho(drone);
        }
    }

    @Override
    public Phase nextPhase() {
        if(this.edgesFound == 3) {

            return new CarvePerimeter(this.islandX, this.islandY, this.droneDirection);
        }

        int flyActionsLeft = !this.increaseX ? this.islandX : this.islandY;
        return new FlyPastDetermined(this.islandX, this.islandY, !this.increaseX, this.edgesFound + 1, flyActionsLeft);
    }

    private void checkEcho(Drone drone) {
        if(drone.getEchoRight().equals(EchoResult.OUT_OF_RANGE)) {
            increaseXorY();
            this.decisionQueue.add(Action.TURN_RIGHT);
        }
        loadDecisionQueue();
    }

    private void loadDecisionQueue() {
        this.decisionQueue.add(Action.FLY);
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

    public Action getNextDecision() {
        return this.decisionQueue.peek();
    }

}