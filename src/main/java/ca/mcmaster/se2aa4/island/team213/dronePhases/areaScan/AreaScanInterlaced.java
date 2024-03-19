package ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan;

import ca.mcmaster.se2aa4.island.team213.*;
import ca.mcmaster.se2aa4.island.team213.dronePhases.EndPhase;
import ca.mcmaster.se2aa4.island.team213.dronePhases.Phase;
import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.BooleanMap;
import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.DronePosition;
import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class AreaScanInterlaced implements Phase {
    int earlyturn = 0;
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
    private final Logger logger = LogManager.getLogger();
    private final Queue<JSONObject> taskQueue = new LinkedList<>();

    HashMap<Integer, int[]> hashMap;

    public AreaScanInterlaced(DronePosition dronePosition, BooleanMap mapOfCheckedTiles, Direction droneDirection){
        logger.info("---------------------------AREA SCAN INTERLACED CREATED---------------------------------------");

        this.dronePosition = dronePosition;
        this.booleanMap = mapOfCheckedTiles;
        this.pointsOfInterest = new PointsOfInterests();
        this.startDirection = droneDirection;
        int islandX = booleanMap.getIslandX();
        int islandY = booleanMap.getIslandY();
        this.direction = droneDirection;

//        EdgeMap edgeMap = new EdgeMap(mapOfCheckedTiles.getMap(), droneDirection, islandX, islandY);
        EdgeMapNew edgeMap = new EdgeMapNew(dronePosition, droneDirection, islandX, islandY, mapOfCheckedTiles.getMap());
        hashMap = edgeMap.getEdgeMap();

        switch (droneDirection){
            case E, W -> {
                //Even length width will result in an extra column that needs to be covered at the end
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
    public boolean lastPhase() {
        return true;
    }

    @Override
    public boolean isFinished() {
        if (turnedAround && turns == turnsBeforeReturn-1 && reachedEdge()){
            logger.info("early turns: " + earlyturn);
            logger.info("start direction: " + startDirection);
            logger.info(Arrays.toString(edgePos));
            for (HashMap.Entry<Integer, int[]> entry : hashMap.entrySet()) {
                Integer key = entry.getKey();
                int[] value = entry.getValue();
                String logMessage = String.format("Key: %d, Value: [%d, %d]", key, value[0], value[1]);
                logger.info(logMessage);
            }
            return true;
        }

        return false;
    }
    @Override
    public JSONObject createDecision(Drone drone) {

        if (!taskQueue.isEmpty()){
            return taskQueue.remove();
        }

        if (!turnedAround && turns == turnsBeforeReturn && reachedEdge()){
            logger.info("--------------------------TURNING AROUND ----------------------------------");
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
        else if (reachedEdge() || earlyTurn()){
            goingUpOrRight = !goingUpOrRight;
            /*
             * Performs 2 left or right turns
             */
            if (lastTurnLeft){

                taskQueue.add(Action.TURN_RIGHT.toJSON(direction));
                direction = direction.rightTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_RIGHT, direction);
                logger.info("RIGHT TURN");

                taskQueue.add(Action.TURN_RIGHT.toJSON(direction));
                direction = direction.rightTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_RIGHT, direction);
                lastTurnLeft = false;
            }
            else{
                taskQueue.add(Action.TURN_LEFT.toJSON(direction));
                direction = direction.leftTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_LEFT, direction);
                logger.info("LEFT TURN");

                taskQueue.add(Action.TURN_LEFT.toJSON(direction));
                direction = direction.leftTurn();
                dronePosition.updatePositionAfterDecision(Action.TURN_LEFT, direction);
                lastTurnLeft = true;
            }

            movesSinceTurn = 0;
            blocksMovedSideways += 2;
            turns++;
        }
        else{
            taskQueue.add(Action.FLY.toJSON(direction));
            dronePosition.updatePositionAfterDecision(Action.FLY, direction);
            movesSinceTurn++;
        }
//        taskQueue.add(Action.SCAN.toJSON(direction));
        if (!booleanMap.getMap()[dronePosition.getDroneY()][dronePosition.getDroneX()]){
            taskQueue.add(Action.SCAN.toJSON(direction));
            booleanMap.getMap()[dronePosition.getDroneY()][dronePosition.getDroneX()] = true;
        }

        return taskQueue.remove();

    }

    /*
     * Adds all creeks and sites found, and computs and updates the closest creekID
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

    private boolean earlyTurn(){
        switch (startDirection){
            case N -> {
                if (hashMap.get(dronePosition.getDroneX()) != null && hashMap.get(dronePosition.getDroneX()+2) != null){
                    if (goingUpOrRight){
                        //If passed both edge points
                        if (dronePosition.getDroneY() <= hashMap.get(dronePosition.getDroneX())[0] && dronePosition.getDroneY() <= hashMap.get(dronePosition.getDroneX())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            if (dronePosition.getDroneY() <= hashMap.get(dronePosition.getDroneX()+2)[0] && dronePosition.getDroneY() <= hashMap.get(dronePosition.getDroneX()+2)[1]){
                                return true;
                            }
                        }
                    }
                    else{
                        if (dronePosition.getDroneY() >= hashMap.get(dronePosition.getDroneX())[0] && dronePosition.getDroneY() >= hashMap.get(dronePosition.getDroneX())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            if (dronePosition.getDroneY() >= hashMap.get(dronePosition.getDroneX()+2)[0] && dronePosition.getDroneY() >= hashMap.get(dronePosition.getDroneX()+2)[1]){
                                return true;
                            }
                        }
                    }
                }
                return false;

            }
            case E -> {
                if (hashMap.get(dronePosition.getDroneY()) != null && hashMap.get(dronePosition.getDroneY()+2) != null){
                    if (goingUpOrRight){
                        //If passed both edge points
                        if (dronePosition.getDroneX() >= hashMap.get(dronePosition.getDroneY())[0] && dronePosition.getDroneX() >= hashMap.get(dronePosition.getDroneY())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            if (dronePosition.getDroneX() >= hashMap.get(dronePosition.getDroneY()+2)[0] && dronePosition.getDroneX() >= hashMap.get(dronePosition.getDroneY()+2)[1]){
                                return true;
                            }
                        }
                    }
                    else{
                        if (dronePosition.getDroneX() <= hashMap.get(dronePosition.getDroneY())[0] && dronePosition.getDroneX() <= hashMap.get(dronePosition.getDroneY())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            if (dronePosition.getDroneX() <= hashMap.get(dronePosition.getDroneY()+2)[0] && dronePosition.getDroneX() <= hashMap.get(dronePosition.getDroneY()+2)[1]){
                                return true;
                            }
                        }
                    }
                }
                return false;

            }
            case S -> {
                if (hashMap.get(dronePosition.getDroneX()) != null && hashMap.get(dronePosition.getDroneX()-2) != null){
                    if (goingUpOrRight){
                        //If passed both edge points
                        if (dronePosition.getDroneY() <= hashMap.get(dronePosition.getDroneX())[0] && dronePosition.getDroneY() <= hashMap.get(dronePosition.getDroneX())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            if (dronePosition.getDroneY() <= hashMap.get(dronePosition.getDroneX()-2)[0] && dronePosition.getDroneY() <= hashMap.get(dronePosition.getDroneX()-2)[1]){
                                earlyturn++;
                                return true;
                            }
                        }
                    }
                    else{
                        if (dronePosition.getDroneY() >= hashMap.get(dronePosition.getDroneX())[0] && dronePosition.getDroneY() >= hashMap.get(dronePosition.getDroneX())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            if (dronePosition.getDroneY() >= hashMap.get(dronePosition.getDroneX()-2)[0] && dronePosition.getDroneY() >= hashMap.get(dronePosition.getDroneX()-2)[1]){
                                earlyturn++;
                                return true;
                            }
                        }
                    }
                }

                return false;

            }
            case W -> {
                if (hashMap.get(dronePosition.getDroneY()) != null && hashMap.get(dronePosition.getDroneY()-2) != null){
                    if (goingUpOrRight){
                        //If passed both edge points
                        if (dronePosition.getDroneX() >= hashMap.get(dronePosition.getDroneY())[0] && dronePosition.getDroneX() >= hashMap.get(dronePosition.getDroneY())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            if (dronePosition.getDroneX() >= hashMap.get(dronePosition.getDroneY()-2)[0] && dronePosition.getDroneX() >= hashMap.get(dronePosition.getDroneY()-2)[1]){
                                return true;
                            }
                        }
                    }
                    else{
                        if (dronePosition.getDroneX() <= hashMap.get(dronePosition.getDroneY())[0] && dronePosition.getDroneX() <= hashMap.get(dronePosition.getDroneY())[1]){
                            //Checks if a column 2 blocks to the right exists, then checks that current y position is above both checkpoints of that column
                            if (dronePosition.getDroneX() <= hashMap.get(dronePosition.getDroneY()-2)[0] && dronePosition.getDroneX() <= hashMap.get(dronePosition.getDroneY()-2)[1]){
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }
}