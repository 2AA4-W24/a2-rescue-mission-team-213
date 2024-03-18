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
import java.util.LinkedList;
import java.util.Queue;

public class AreaScanInterlaced implements Phase {
    private GetShortestPath shortestPath;
    public int stepsBeforeTurn;
    public int turnsBeforeReturn;

    //Moves since turn is initialized to 1, as start position is one block ahead
    public int movesSinceTurn = 0;
    public int turns = 0;

    Direction direction;



    int islandx;
    int islandy;


    boolean passedFirstEnd;
    int blocksMovedSideways = 0;

    boolean lastTurnLeft = true;

    public boolean turnedAround = false;

    boolean[][] mapOfCheckedTiles;
    boolean[][] mapOfEdges;

    int[] edgePos;

    int startx;
    int starty;
    Direction startDirection;

    BooleanMap booleanMap;

    DronePosition dronePosition;

    private final Logger logger = LogManager.getLogger();

    private Queue<JSONObject> taskQueue = new LinkedList<>();
    //int islandx, int islandy, int x, int y, Direction droneDirection, boolean[][] mapOfCheckedTiles

    public AreaScanInterlaced(DronePosition dronePosition, BooleanMap mapOfCheckedTiles, Direction droneDirection){
        logger.info("---------------------------AREASCANINTERLACED CREATED---------------------------------------");


        this.dronePosition = dronePosition;

        this.booleanMap = mapOfCheckedTiles;

        shortestPath = new GetShortestPath();
        this.islandx = booleanMap.getIslandX();

        //TODO: GET RID OF ATTRIBUTES AFTER TESTING
//        this.startx = dronePosition.getDroneX();
//        this.starty = dronePosition.getDroneY();
        this.startDirection = droneDirection;


        this.islandy = booleanMap.getIslandY();
        this.direction = droneDirection;




        this.mapOfCheckedTiles = mapOfCheckedTiles.getMap();

        EdgeMap edgeMap = new EdgeMap(this.mapOfCheckedTiles, this.direction, this.islandx, this.islandy);
        mapOfEdges = edgeMap.getEdgeMap();

        passedFirstEnd = false;

        switch (droneDirection){
            case E, W -> {
                this.stepsBeforeTurn = islandx-3;
                //Even length width will result in an extra column that needs to be covered at the end
                if (islandy % 2 == 0){
                    this.turnsBeforeReturn = islandy/2 - 1;
                }
                else{
                    this.turnsBeforeReturn = islandy/2;
                }
            }
            case S, N -> {
                this.stepsBeforeTurn = islandy-3;
                if (islandx % 2 == 0){
                    this.turnsBeforeReturn = islandx/2 - 1;
                }
                else{
                    this.turnsBeforeReturn = islandx/2;
                }
            }
        }
        switch (droneDirection){
            case N -> {
                this.edgePos = new int[] {dronePosition.getDroneY(), dronePosition.getDroneY()-islandy+2};
            }
            case E -> {
                this.edgePos = new int[] {dronePosition.getDroneX(), dronePosition.getDroneX()+islandx-2};
            }
            case S -> {
                this.edgePos = new int[] {dronePosition.getDroneY(), dronePosition.getDroneY()+islandy-2};
            }
            case W -> {
                this.edgePos = new int[] {dronePosition.getDroneX(), dronePosition.getDroneX()-islandx+2};
            }
        }
        logger.info(Arrays.toString(edgePos));

    }

    @Override
    public boolean lastPhase() {
        return true;
    }

    @Override
    public boolean isFinished() {
        return turnedAround && turns == turnsBeforeReturn-1 && reachedEdge();
    }

    @Override
    public JSONObject createDecision(Drone drone) {

        if (!taskQueue.isEmpty()){
            return taskQueue.remove();
        }

        if (!turnedAround && turns == turnsBeforeReturn && reachedEdge()){
            logger.info("--------------------------TURNING AROUND ----------------------------------");
            turnedAround = true;

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
        else if (reachedEdge()){
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
            logger.info("------FLYING STRAIGHT------------------------------------------------------------\n\n");
            taskQueue.add(Action.FLY.toJSON(direction));
            dronePosition.updatePositionAfterDecision(Action.FLY, direction);
            movesSinceTurn++;
        }

        if (!booleanMap.getMap()[dronePosition.getDroneY()][dronePosition.getDroneX()]){
            taskQueue.add(Action.SCAN.toJSON(direction));
            booleanMap.getMap()[dronePosition.getDroneY()][dronePosition.getDroneX()] = true;
        }

        return taskQueue.remove();

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
    @Override
    public void checkDrone(Drone drone) {
        JSONArray creeksJSON = drone.getScanInfoCreeks();
        if (!creeksJSON.isEmpty()){
            for (int i=0; i<creeksJSON.length(); ++i){
                shortestPath.addCreek(new PointsOfInterest(dronePosition.getDroneX(), dronePosition.getDroneY(), creeksJSON.getString(i)));
            }
        }

        JSONArray sitesJSON = drone.getScanInfoSites();
        if (!sitesJSON.isEmpty()){
            for (int i=0; i<sitesJSON.length(); ++i){
                shortestPath.addSite(new PointsOfInterest(dronePosition.getDroneX(), dronePosition.getDroneY(), sitesJSON.getString(i)));
            }
        }

        shortestPath.computeClosestSite();
        shortestPath.updateCreekID(drone);

        if (shortestPath.checkIfPair()){
            booleanMap.determineImpossibleTiles(shortestPath.getSite(), shortestPath.closestCreekPOI);
        }
    }

    @Override
    public Phase nextPhase() {
        return new EndPhase();
    }
}