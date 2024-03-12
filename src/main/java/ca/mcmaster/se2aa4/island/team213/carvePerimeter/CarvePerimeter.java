package ca.mcmaster.se2aa4.island.team213.carvePerimeter;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.Action;
import ca.mcmaster.se2aa4.island.team213.Direction;
import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.EndPhase;
import ca.mcmaster.se2aa4.island.team213.Phase;
import ca.mcmaster.se2aa4.island.team213.edgeFinding.DecisionJSONs;

public class CarvePerimeter implements Phase {
    private boolean isFinished;
    private int islandX, islandY;
    private Direction droneDirection;
    private int rightTurnsPerformed;

    private int droneX, droneY;
    private int horizontalFlyActions, verticalFlyActions;

    private Queue<Action> decisionQueue;
    private boolean[][] mapOfCheckedTiles;

    public CarvePerimeter(int islandX, int islandY, Direction droneDirection) {
        this.isFinished = false;
        this.decisionQueue = new LinkedList<Action>();
        this.islandX = islandX;
        this.islandY = islandY;
        this.droneDirection = droneDirection;

        this.rightTurnsPerformed = 0;
        this.verticalFlyActions = islandY - 3;
        this.horizontalFlyActions = islandX - 3;
        this.mapOfCheckedTiles = new boolean[islandY][islandX];

        placeDroneOnBooleanMap(droneDirection);
        loadDecisionQueue(droneDirection);
    }

    private void placeDroneOnBooleanMap(Direction droneDirection) {
        if(droneDirection.equals(Direction.N)) {
            this.droneX = 0;
            this.droneY = this.islandY - 2;
        }
        else if(droneDirection.equals(Direction.E)) {
            this.droneX = 1;
            this.droneY = 0;
        }
        else if(droneDirection.equals(Direction.S)) {
            this.droneX = this.islandX - 1;
            this.droneY = 1;
        }
        else if(droneDirection.equals(Direction.W)) {
            this.droneX = this.islandX - 2;
            this.droneY = this.islandY - 1;
        }
    }

    private void loadDecisionQueue(Direction droneDirection) {
        // decisionQueue.add(Action.ECHO_RIGHT);
        if(droneDirection.equals(Direction.N) || droneDirection.equals(Direction.S)) {
            loadFlyAndEchoActions(this.verticalFlyActions);
        }
        else {
            loadFlyAndEchoActions(this.horizontalFlyActions);
        }
        decisionQueue.add(Action.TURN_RIGHT);
    }

    private void loadFlyAndEchoActions(int amount) {
        for(int i = 0; i < amount; i++) {
            decisionQueue.add(Action.FLY);
            // decisionQueue.add(Action.ECHO_RIGHT);
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
        
        decision = DecisionJSONs.actionToJSONObject(this.decisionQueue.peek(), drone.getDirection());
        if(this.decisionQueue.peek().equals(Action.TURN_RIGHT)) {
            this.rightTurnsPerformed += 1;
        }
        if(this.rightTurnsPerformed == 4) {
            this.isFinished = true;
        }
        this.decisionQueue.remove();

        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        if(drone.getPreviousDecision().equals(Action.TURN_RIGHT)) {
            this.droneDirection = this.droneDirection.rightTurn();
            loadDecisionQueue(this.droneDirection);
        }
    }

    public void updateDroneOnBooleanMap(Action action) {
        // Will update droneX and droneY relative to mapOfCheckedTiles  indicies if the previous action was FLY or TURN_RIGHT
    }

    @Override
    public Phase nextPhase() {
        return new EndPhase();
    }
    
    public int getDroneX() {
        return this.droneX;
    }

    public int getDroneY() {
        return this.droneY;
    }

    public int getHorizontalFlyActions() {
        return this.horizontalFlyActions;
    }

    public int getVerticalFlyActions() {
        return this.verticalFlyActions;
    }

}
