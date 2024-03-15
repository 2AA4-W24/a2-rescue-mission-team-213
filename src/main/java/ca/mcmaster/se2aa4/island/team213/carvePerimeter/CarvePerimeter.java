package ca.mcmaster.se2aa4.island.team213.carvePerimeter;

import java.util.LinkedList;
import java.util.Queue;


import ca.mcmaster.se2aa4.island.team213.areaScan.AreaScanInterlaced;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.Action;
import ca.mcmaster.se2aa4.island.team213.Direction;
import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.EchoResult;
import ca.mcmaster.se2aa4.island.team213.Phase;

public class CarvePerimeter implements Phase {
    private boolean isFinished;
    private int islandX, islandY;
    private Direction droneDirection;
    private int rightTurnsPerformed;

    private int droneX, droneY;                             // store in ADT
    private int horizontalFlyActions, verticalFlyActions;

    private Queue<Action> decisionQueue;
    private boolean[][] mapOfCheckedTiles;

    private final Logger logger = LogManager.getLogger();

    public CarvePerimeter(int islandX, int islandY, Direction droneDirection) {
        logger.info("*");
        logger.info("*");
        logger.info("** new CarvePerimeter created **" + droneDirection);
        logger.info("*");
        logger.info("*");
        
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

    // Move out of this class
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
        decisionQueue.add(Action.ECHO_RIGHT);
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
            decisionQueue.add(Action.ECHO_RIGHT);
        }
    }

    @Override
    public boolean lastPhase() {
        return false;
    }

    @Override
    public boolean isFinished() {
        // if(this.isFinished) {

        //     int groundFound = 0;

        //     for(int i = 0; i < islandY; i++) {
        //         String test = "";
        //         for(int j = 0; j < islandX; j++) {
        //             if(this.droneY == i && this.droneX == j) {
        //                 test += "1 ";
        //             } else {
        //                 if(this.mapOfCheckedTiles[i][j] == true) {
        //                     test += "- ";
        //                 } else {
        //                     test += "0 ";

        //                     groundFound += 1;

        //                 }
        //             }
        //         }
        //         logger.info(test);
        //     }

        //     logger.info("Total tiles: " + (islandY * islandX));
        //     logger.info("Tiles left to scan: " + groundFound);
        //     logger.info("Estimated battery cost: ~" + ((islandY * islandX * 8) - (groundFound * 8)));

        // }
        return this.isFinished;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision;
        Action nextAction = this.decisionQueue.peek();
        
        decision = nextAction.toJSON(drone.getDirection());
        if(nextAction.equals(Action.TURN_RIGHT)) {
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
            updateDroneXYAfterRightTurn();
            this.droneDirection = this.droneDirection.rightTurn();
            loadDecisionQueue(this.droneDirection);
        }
        else if(drone.getPreviousDecision().equals(Action.FLY)) {
            updateDroneXYAfterFly();
        }
        else if(drone.getPreviousDecision().equals(Action.ECHO_RIGHT)) {
            updateMapOfCheckedTiles(drone.getRangeRight(), drone.getEchoRight());
        }

    }

    @Override
    public Phase nextPhase() {
        return new AreaScanInterlaced(islandX, islandY, droneX, droneY, droneDirection, mapOfCheckedTiles);

    }

    // Move out of this class
    private void updateMapOfCheckedTiles(Integer echoRange, EchoResult echoRight) {
        if(this.droneDirection.equals(Direction.N)) {
            if(echoRight.equals(EchoResult.OUT_OF_RANGE)) {
                for(int i = droneX; i < islandX; i++) {
                    this.mapOfCheckedTiles[droneY][i] = true;
                }
            }
            else {
                for(int i = droneX; i < echoRange; i++) {
                    this.mapOfCheckedTiles[droneY][i] = true;
                }
            }
        }
        else if(this.droneDirection.equals(Direction.E)) {
            if(echoRight.equals(EchoResult.OUT_OF_RANGE)) {
                for(int i = droneY; i < islandY; i++) {
                    this.mapOfCheckedTiles[i][droneX] = true;
                }
            }
            else {
                for(int i = droneY; i < echoRange; i++) {
                    this.mapOfCheckedTiles[i][droneX] = true;
                }
            }
        }
        else if(this.droneDirection.equals(Direction.S)) {
            if(echoRight.equals(EchoResult.OUT_OF_RANGE)) {
                for(int i = droneX; i >= 0; i--) {
                    this.mapOfCheckedTiles[droneY][i] = true;
                }
            }
            else {
                for(int i = droneX; i > droneX - echoRange; i--) {
                    this.mapOfCheckedTiles[droneY][i] = true;
                }
            }
        }
        else if(this.droneDirection.equals(Direction.W)) {
            if(echoRight.equals(EchoResult.OUT_OF_RANGE)) {
                for(int i = droneY; i >= 0; i--) {
                    this.mapOfCheckedTiles[i][droneX] = true;
                }
            }
            else {
                for(int i = droneY; i > droneY - echoRange; i--) {
                    this.mapOfCheckedTiles[i][droneX] = true;
                }
            }
        }
    }
    
    // Move out of this class
    private void updateDroneXYAfterFly() {
        if(this.droneDirection.equals(Direction.N)) {
            decreaseDroneY();
        }
        else if(this.droneDirection.equals(Direction.E)) {
            increaseDroneX();
        }
        else if(this.droneDirection.equals(Direction.S)) {
            increaseDroneY();
        }
        else if(this.droneDirection.equals(Direction.W)) {
            decreaseDroneX();
        }
    }

    // Move out of this class
    private void updateDroneXYAfterRightTurn() {
        if(this.droneDirection.equals(Direction.N)) {
            increaseDroneX();
            decreaseDroneY();
        }
        else if(this.droneDirection.equals(Direction.E)) {
            increaseDroneX();
            increaseDroneY();
        }
        else if(this.droneDirection.equals(Direction.S)) {
            decreaseDroneX();
            increaseDroneY();
        }
        else if(this.droneDirection.equals(Direction.W)) {
            decreaseDroneX();
            decreaseDroneY();
        }
    }

    // Move out of this class
    private void increaseDroneX() {
        this.droneX += 1;
    }

    // Move out of this class
    private void decreaseDroneX() {
        this.droneX -= 1;
    }

    // Move out of this class
    private void increaseDroneY() {
        this.droneY += 1;
    }

    // Move out of this class
    private void decreaseDroneY() {
        this.droneY -= 1;
    }

    // Move out of this class
    public int getDroneX() {
        return this.droneX;
    }

    // Move out of this class
    public int getDroneY() {
        return this.droneY;
    }

    public int getHorizontalFlyActions() {
        return this.horizontalFlyActions;
    }

    public int getVerticalFlyActions() {
        return this.verticalFlyActions;
    }

    public Direction getDroneDirection() {
        return this.droneDirection;
    }

}

