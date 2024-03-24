package ca.mcmaster.se2aa4.island.team213.dronephases.areascan;

import ca.mcmaster.se2aa4.island.team213.*;
import ca.mcmaster.se2aa4.island.team213.dronephases.EndPhase;
import ca.mcmaster.se2aa4.island.team213.dronephases.Phase;
import ca.mcmaster.se2aa4.island.team213.dronephases.carveperimeter.BooleanMap;
import ca.mcmaster.se2aa4.island.team213.dronephases.carveperimeter.DronePosition;
import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class InterlacedAreaScan implements Phase {
    private boolean goingUpOrRight;
    private final PointsOfInterests pointsOfInterest;
    private int turnsBeforeReturn;
    private int movesSinceTurn = 0;
    private int turns = 0;
    private Direction direction;
    private int blocksMovedSideways = 0;
    private boolean lastTurnLeft = true;
    private boolean turnedAround = false;
    private int[] edgePos;
    private final Direction startDirection;
    private final BooleanMap booleanMap;
    private final DronePosition dronePosition;
    private final Queue<JSONObject> taskQueue = new LinkedList<>();
    private final Map<Integer, int[]> edgePosMap;

    public InterlacedAreaScan(DronePosition dronePosition, BooleanMap mapOfCheckedTiles, Direction droneDirection){
        this.dronePosition = dronePosition;
        this.booleanMap = mapOfCheckedTiles;
        this.pointsOfInterest = new PointsOfInterests();
        this.startDirection = droneDirection;
        int islandX = booleanMap.getIslandX();
        int islandY = booleanMap.getIslandY();
        this.direction = droneDirection;

        EdgeMap edgeMap = new EdgeMap(droneDirection, mapOfCheckedTiles.getMap());
        edgePosMap = edgeMap.getEdgeMap();

        switch (droneDirection){
            case E, W -> {
                /*
                 * Even length width will result in an extra column
                 * that needs to be covered at the second iteration
                 * of interlaced scan
                 */
                if (islandY % 2 == 0){
                    this.turnsBeforeReturn = islandY /2 - 1;
                }
                else{
                    this.turnsBeforeReturn = islandY /2;
                }
            }
            case S, N -> {
                if (islandX % 2 == 0){
                    this.turnsBeforeReturn = islandX /2 - 1;
                }
                else{
                    this.turnsBeforeReturn = islandX /2;
                }
            }
        }
        /*
         * Finds coordinates of the edge positions for the
         * drone to reference when determining when to turn
         */
        switch (droneDirection){
            case N -> {
                this.goingUpOrRight = true;
                this.edgePos = new int[] {dronePosition.getDroneY(), dronePosition.getDroneY()- islandY +2};
            }
            case E -> {
                this.goingUpOrRight = true;
                this.edgePos = new int[] {dronePosition.getDroneX(), dronePosition.getDroneX()+ islandX -2};
            }
            case S -> {
                this.goingUpOrRight = false;
                this.edgePos = new int[] {dronePosition.getDroneY(), dronePosition.getDroneY()+ islandY -2};
            }
            case W -> {
                this.goingUpOrRight = false;
                this.edgePos = new int[] {dronePosition.getDroneX(), dronePosition.getDroneX()- islandX +2};
            }
        }
    }

    @Override
    public boolean isFinished() {
        return turnedAround && reachedEdge() && (turns == turnsBeforeReturn-1 || earlyReturnInterlaced());
    }
    @Override
    public JSONObject createDecision(Drone drone) {

        if (!taskQueue.isEmpty()){
            return taskQueue.remove();
        }

        /*
         * If in the first iteration of the interlaced scan,
         * checks if drone can return back to 2nd row/ column
         * to start second iteration
         */
        if (!turnedAround && (turns == turnsBeforeReturn || earlyReturnInterlaced()) && reachedEdge()){
            turnedAround = true;
            goingUpOrRight = !goingUpOrRight;

            if (lastTurnLeft){

                taskQueue.add(Action.TURN_LEFT.toJSON(direction));
                direction = direction.leftTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_LEFT, direction);
            }
            else{

                taskQueue.add(Action.TURN_RIGHT.toJSON(direction));
                direction = direction.rightTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_RIGHT, direction);
            }

            //Gets back to second column
            while (blocksMovedSideways - 3 > 0){
                taskQueue.add(Action.FLY.toJSON(direction));
                blocksMovedSideways--;
                dronePosition.updatePositionAfterDecision(Action.FLY, direction);
            }

            //final left turn to face towards column
            if (lastTurnLeft){

                taskQueue.add(Action.TURN_LEFT.toJSON(direction));
                direction = direction.leftTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_LEFT, direction);
                lastTurnLeft = false;
            }
            else{

                taskQueue.add(Action.TURN_RIGHT.toJSON(direction));
                direction = direction.rightTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_RIGHT, direction);
                lastTurnLeft = true;
            }

            turns = 0;
            movesSinceTurn = 0;

        }

        /*
         * Checks if the drone reached the edge of the perimeter
         * or can do an early turn doing a 180-degree turn if so
         */
        else if (reachedEdge() || earlyTurn()){
            goingUpOrRight = !goingUpOrRight;
            /*
             * Performs 2 left or right turns
             */
            if (lastTurnLeft){
                taskQueue.add(Action.TURN_RIGHT.toJSON(direction));
                direction = direction.rightTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_RIGHT, direction);

                taskQueue.add(Action.TURN_RIGHT.toJSON(direction));
                direction = direction.rightTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_RIGHT, direction);
                lastTurnLeft = false;
            }
            else{
                taskQueue.add(Action.TURN_LEFT.toJSON(direction));
                direction = direction.leftTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_LEFT, direction);

                taskQueue.add(Action.TURN_LEFT.toJSON(direction));
                direction = direction.leftTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_LEFT, direction);
                lastTurnLeft = true;
            }

            movesSinceTurn = 0;
            blocksMovedSideways += 2;
            turns++;
        }
        /*
         * If drone is neither doing 180-turn nor flying to the beginning
         * to start second iteration of area scan, drone performs a fly forward
         */
        else{
            taskQueue.add(Action.FLY.toJSON(direction));
            dronePosition.updatePositionAfterDecision(Action.FLY, direction);
            movesSinceTurn++;
        }

        /*
         * Checks if the current position has to be scanned
         */
        if (!booleanMap.getMap()[dronePosition.getDroneY()][dronePosition.getDroneX()]){
            taskQueue.add(Action.SCAN.toJSON(direction));
            booleanMap.getMap()[dronePosition.getDroneY()][dronePosition.getDroneX()] = true;
        }

        return taskQueue.remove();

    }

    /*
     * Adds all creeks and sites found, and computes and updates the closest creekID
     */
    @Override
    public void checkDrone(Drone drone) {
        JSONArray creeksJSON = drone.getScanInfoCreeks();
        JSONArray sitesJSON = drone.getScanInfoSites();

        pointsOfInterest.addCreeks(creeksJSON, dronePosition);
        pointsOfInterest.addSites(sitesJSON, dronePosition);
        pointsOfInterest.computeClosestSite();
        pointsOfInterest.updateCreekID(drone);

        /*
         * Once a site and creek pair is identified, will mark all new blocks that do not need to be scanned
         */
        if (pointsOfInterest.checkIfPair()){
            booleanMap.determineImpossibleTiles(pointsOfInterest.getSite(), pointsOfInterest.getClosestCreek());
        }
    }
    @Override
    public Phase nextPhase() {
        return new EndPhase();
    }

    /*
     * Determines if a premature start of the second interlaced iteration
     * or premature isFinished condition can be made
     */
    private boolean earlyReturnInterlaced(){
        if (pointsOfInterest.checkIfPair()){
            switch (startDirection){
                case N -> {
                    return dronePosition.getDroneX() > pointsOfInterest.maxCoord(startDirection);
                }
                case E -> {
                    return dronePosition.getDroneY() > pointsOfInterest.maxCoord(startDirection);
                }
                case S -> {
                    return dronePosition.getDroneX() < pointsOfInterest.maxCoord(startDirection);
                }
                case W -> {
                    return dronePosition.getDroneY() < pointsOfInterest.maxCoord(startDirection);
                }
            }
        }
        return false;
    }

    private boolean reachedEdge(){
        if (movesSinceTurn > 0){
            switch (startDirection){
                case N, S -> {
                    for (int posY: edgePos){
                        if (dronePosition.getDroneY() == posY){
                            return true;
                        }
                    }
                }
                case E, W -> {
                    for (int posX: edgePos){
                        if (dronePosition.getDroneX() == posX){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /*
     * Determines if an early 180 turn of the drone can be made
     * (i.e. the drone is at the edge of the island)
     * The drone must not only consider the current row/column to determine
     * if it can do a 180 turn, but also the row/column that it will turn to,
     * ensuring that no blocks are missed
     */
    private boolean earlyTurn(){
        switch (startDirection){
            case N -> {
                if (edgePosMap.get(dronePosition.getDroneX()) != null && edgePosMap.get(dronePosition.getDroneX()+2) != null){
                    if (goingUpOrRight){
                        //If passed both edge points
                        if (dronePosition.getDroneY() <= edgePosMap.get(dronePosition.getDroneX())[0] && dronePosition.getDroneY() <= edgePosMap.get(dronePosition.getDroneX())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            return dronePosition.getDroneY() <= edgePosMap.get(dronePosition.getDroneX() + 2)[0] && dronePosition.getDroneY() <= edgePosMap.get(dronePosition.getDroneX() + 2)[1];
                        }
                    }
                    else{
                        if (dronePosition.getDroneY() >= edgePosMap.get(dronePosition.getDroneX())[0] && dronePosition.getDroneY() >= edgePosMap.get(dronePosition.getDroneX())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            return dronePosition.getDroneY() >= edgePosMap.get(dronePosition.getDroneX() + 2)[0] && dronePosition.getDroneY() >= edgePosMap.get(dronePosition.getDroneX() + 2)[1];
                        }
                    }
                }
                return false;

            }
            case E -> {
                if (edgePosMap.get(dronePosition.getDroneY()) != null && edgePosMap.get(dronePosition.getDroneY()+2) != null){
                    if (goingUpOrRight){
                        //If passed both edge points
                        if (dronePosition.getDroneX() >= edgePosMap.get(dronePosition.getDroneY())[0] && dronePosition.getDroneX() >= edgePosMap.get(dronePosition.getDroneY())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            return dronePosition.getDroneX() >= edgePosMap.get(dronePosition.getDroneY() + 2)[0] && dronePosition.getDroneX() >= edgePosMap.get(dronePosition.getDroneY() + 2)[1];
                        }
                    }
                    else{
                        if (dronePosition.getDroneX() <= edgePosMap.get(dronePosition.getDroneY())[0] && dronePosition.getDroneX() <= edgePosMap.get(dronePosition.getDroneY())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            return dronePosition.getDroneX() <= edgePosMap.get(dronePosition.getDroneY() + 2)[0] && dronePosition.getDroneX() <= edgePosMap.get(dronePosition.getDroneY() + 2)[1];
                        }
                    }
                }
                return false;

            }
            case S -> {
                if (edgePosMap.get(dronePosition.getDroneX()) != null && edgePosMap.get(dronePosition.getDroneX()-2) != null){
                    if (goingUpOrRight){
                        //If passed both edge points
                        if (dronePosition.getDroneY() <= edgePosMap.get(dronePosition.getDroneX())[0] && dronePosition.getDroneY() <= edgePosMap.get(dronePosition.getDroneX())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            return dronePosition.getDroneY() <= edgePosMap.get(dronePosition.getDroneX() - 2)[0] && dronePosition.getDroneY() <= edgePosMap.get(dronePosition.getDroneX() - 2)[1];
                        }
                    }
                    else{
                        if (dronePosition.getDroneY() >= edgePosMap.get(dronePosition.getDroneX())[0] && dronePosition.getDroneY() >= edgePosMap.get(dronePosition.getDroneX())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            return dronePosition.getDroneY() >= edgePosMap.get(dronePosition.getDroneX() - 2)[0] && dronePosition.getDroneY() >= edgePosMap.get(dronePosition.getDroneX() - 2)[1];
                        }
                    }
                }

                return false;

            }
            case W -> {
                if (edgePosMap.get(dronePosition.getDroneY()) != null && edgePosMap.get(dronePosition.getDroneY()-2) != null){
                    if (goingUpOrRight){
                        //If passed both edge points
                        if (dronePosition.getDroneX() >= edgePosMap.get(dronePosition.getDroneY())[0] && dronePosition.getDroneX() >= edgePosMap.get(dronePosition.getDroneY())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            return dronePosition.getDroneX() >= edgePosMap.get(dronePosition.getDroneY() - 2)[0] && dronePosition.getDroneX() >= edgePosMap.get(dronePosition.getDroneY() - 2)[1];
                        }
                    }
                    else{
                        if (dronePosition.getDroneX() <= edgePosMap.get(dronePosition.getDroneY())[0] && dronePosition.getDroneX() <= edgePosMap.get(dronePosition.getDroneY())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            return dronePosition.getDroneX() <= edgePosMap.get(dronePosition.getDroneY() - 2)[0] && dronePosition.getDroneX() <= edgePosMap.get(dronePosition.getDroneY() - 2)[1];
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }
}