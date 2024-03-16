package ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan;

import ca.mcmaster.se2aa4.island.team213.*;
import ca.mcmaster.se2aa4.island.team213.dronePhases.EndPhase;
import ca.mcmaster.se2aa4.island.team213.dronePhases.Phase;
import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.BooleanMap;
import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.DronePosition;
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
    public int x;
    public int y;
    int islandx;
    int islandy;


    boolean passedFirstEnd;
    int blocksMovedSideways = 0;
    boolean extraColumn;

    boolean lastTurnLeft = true;

    public boolean turnedAround = false;

    boolean[][] mapOfCheckedTiles;
    boolean[][] mapOfEdges;

    int[] edgePos;

    int startx;
    int starty;
    Direction startDirection;
    private final Logger logger = LogManager.getLogger();

    private Queue<JSONObject> taskQueue = new LinkedList<>();
    //int islandx, int islandy, int x, int y, Direction droneDirection, boolean[][] mapOfCheckedTiles

    public AreaScanInterlaced(DronePosition dronePosition, BooleanMap mapOfCheckedTiles, Direction droneDirection){
        logger.info("---------------------------AREASCANINTERLACED CREATED---------------------------------------");
//
//        logger.info(islandx);
//        logger.info(islandy);
//        logger.info(droneDirection);
        shortestPath = new GetShortestPath();
        this.islandx = mapOfCheckedTiles.getIslandX();

        //TODO: GET RID OF ATTRIBUTES AFTER TESTING
        this.startx = dronePosition.getDroneX();
        this.starty = dronePosition.getDroneY();
        this.startDirection = droneDirection;


        this.islandy = mapOfCheckedTiles.getIslandY();
        this.direction = droneDirection;
        this.x = dronePosition.getDroneX();
        this.y = dronePosition.getDroneY();

        this.mapOfCheckedTiles = mapOfCheckedTiles.getMap();

        EdgeMap edgeMap = new EdgeMap(this.mapOfCheckedTiles, this.direction, this.islandx, this.islandy);
        mapOfEdges = edgeMap.getEdgeMap();

        passedFirstEnd = false;

        switch (droneDirection){
            case E, W -> {
                this.stepsBeforeTurn = islandx-3;
//                this.edgePos = new int[]{x, islandx+x};
                //Even length width will result in an extra column that needs to be covered at the end
                if (islandy % 2 == 0){
                    this.turnsBeforeReturn = islandy/2 - 1;
                    extraColumn = true;
                }
                else{
                    this.turnsBeforeReturn = islandy/2;
                    extraColumn = false;
                }
            }
            case S, N -> {
                this.stepsBeforeTurn = islandy-3;
//                this.edgePos = new int[]{y, islandy+y};
                if (islandx % 2 == 0){
                    this.turnsBeforeReturn = islandx/2 - 1;
                    extraColumn = true;
                }
                else{
                    this.turnsBeforeReturn = islandx/2;
                    extraColumn = false;
                }
            }
        }

        switch (droneDirection){
            case N -> {
                this.edgePos = new int[] {y, y-islandy+2};
            }
            case E -> {
                this.edgePos = new int[] {x, x+islandx-2};
            }
            case S -> {
                this.edgePos = new int[] {y, y+islandy-2};
            }
            case W -> {
                this.edgePos = new int[] {x, x-islandx+2};
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
        if (turnedAround && turns == turnsBeforeReturn-1 && reachedEdge()){
            logger.info("ENDED");
            for (int yy=0; yy<mapOfEdges.length; ++yy) {
                String test = "";
                for (int xx = 0; xx < mapOfEdges[0].length; ++xx) {
                    if (mapOfEdges[yy][xx]){
                        test += "0";
                    }
                    else{
                        test += "-";
                    }
                }
                logger.info(test);
            }
            logger.info(mapOfCheckedTiles.length + "   " + mapOfCheckedTiles[0].length);
            logger.info(mapOfEdges.length + "   " + mapOfEdges[0].length);
            logger.info("EDGEPOS----");
            logger.info(Arrays.toString(edgePos));
            logger.info(startx + "   " + starty + "   " + startDirection.toString());

            return true;
        }
        return false;
//        if (!extraColumn){
//            logger.info("ENDED");
//            return (turnedAround && turns == turnsBeforeReturn-1 && movesSinceTurn == stepsBeforeTurn);
//        }
//        return (turnedAround && turns == turnsBeforeReturn && movesSinceTurn == stepsBeforeTurn);

    }

    @Override
    public JSONObject createDecision(Drone drone) {

//        Random random = new Random();
//        int min = 6;
//        int max = 8;
//        int randomNumber = random.nextInt(max - min + 1) + min;

        logger.info("x: " + x + "  y: " + y + "   turnedAround:" + turnedAround);
//        JSONObject enqueueScan = new JSONObject();
        JSONObject decision = new JSONObject();
        JSONObject headingDirection = new JSONObject();
        JSONObject finaldecision = new JSONObject();

        //TODO: ADD SCANNING TO TASKQUEUE AND FIGURE OUT HOW IT ALL TIES TOGETHER

        if (!taskQueue.isEmpty()){
//            logger.info("REMOVING TASK FROM QUEUE - -------");
            decision = taskQueue.remove();
            return decision;
        }

        if (!turnedAround && turns == turnsBeforeReturn && reachedEdge()){
            logger.info("--------------------------TURNING AROUND ----------------------------------");
            turnedAround = true;

            if (lastTurnLeft){
                leftTurnPos();
                direction = direction.leftTurn();
            }
            else{
                rightTurnPos();
                direction = direction.rightTurn();
            }

            decision = new JSONObject();
            decision.put("action", "heading");
            headingDirection = new JSONObject();
            headingDirection.put("direction", direction);
            decision.put("parameters", headingDirection);
            taskQueue.add(decision);

            //Gets back to second column
            while (blocksMovedSideways - 3 > 0){
                JSONObject moveForward = new JSONObject();
                moveForward.put("action", "fly");
                taskQueue.add(moveForward);
                blocksMovedSideways--;
                movePos();
            }

            //final left turn to face towards column
            if (lastTurnLeft){
                leftTurnPos();
                direction = direction.leftTurn();
                lastTurnLeft = false;
            }
            else{
                rightTurnPos();
                direction = direction.rightTurn();
                lastTurnLeft = true;
            }

            decision = new JSONObject();
            headingDirection = new JSONObject();

            decision.put("action", "heading");
            headingDirection.put("direction", direction);
            decision.put("parameters", headingDirection);
            taskQueue.add(decision);

            turns = 0;
            movesSinceTurn = 0;

        }
        else if (reachedEdge()){
            logger.info("------TURNING------------------------------------------------------------\n\n");
            logger.info(movesSinceTurn);
            logger.info(reachedEdge());
            logger.info("turns: " + turns);

//            passedFirstEnd = false;
            logger.info("facing: " + direction);
            if (lastTurnLeft){
                rightTurnPos();
                direction = direction.rightTurn();
                logger.info("RIGHT TURN");
            }
            else{
                leftTurnPos();
                direction = direction.leftTurn();
                logger.info("LEFT TURN");
            }

            //2 right turns
            decision = new JSONObject();
            headingDirection = new JSONObject();
            decision.put("action", "heading");
            headingDirection.put("direction", direction);
            decision.put("parameters", headingDirection);
            taskQueue.add(decision);

            if (lastTurnLeft){
                rightTurnPos();
                direction = direction.rightTurn();
                lastTurnLeft = false;
            }
            else{
                leftTurnPos();
                direction = direction.leftTurn();
                lastTurnLeft = true;
            }
            decision = new JSONObject();
            headingDirection = new JSONObject();
            decision.put("action", "heading");
            headingDirection.put("direction", direction);
            decision.put("parameters", headingDirection);
            taskQueue.add(decision);

            movesSinceTurn = 0;
            blocksMovedSideways += 2;
            turns++;


        }
        else{
            logger.info("------FLYING STRAIGHT------------------------------------------------------------\n\n");
            decision = new JSONObject();
            decision.put("action", "fly");
            taskQueue.add(decision);
            movePos();
            movesSinceTurn++;
        }
        //(mapOfEdges[y][x])

        if (!mapOfCheckedTiles[y][x]){
            JSONObject enqueueScan2 = new JSONObject();
            enqueueScan2.put("action", "scan");
            taskQueue.add(enqueueScan2);
            mapOfCheckedTiles[y][x] = true;
        }
//        JSONObject enqueueScan2 = new JSONObject();
//        enqueueScan2.put("action", "scan");
//        taskQueue.add(enqueueScan2);
//        mapOfCheckedTiles[y][x] = true;




        finaldecision = taskQueue.remove();
        return finaldecision;

    }

    private boolean reachedEdge(){
        if (movesSinceTurn > 0){
            switch (startDirection){
                case N, S -> {
                    for (int posY: edgePos){
                        if (y == posY){
                            return true;
                        }
                    }
                }
                case E, W -> {
                    for (int posX: edgePos){
                        if (x == posX){
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
                logger.info(x + " " + y);
                logger.info(creeksJSON.getString(i));
                shortestPath.addCreek(new PointsOfInterest(x,y, creeksJSON.getString(i)));
            }
        }

        JSONArray sitesJSON = drone.getScanInfoSites();
        if (!sitesJSON.isEmpty()){
            for (int i=0; i<sitesJSON.length(); ++i){
                logger.info("SITE FOUND: " + x + " "+ y + " " + sitesJSON.getString(i));
                shortestPath.addSite(new PointsOfInterest(x,y, sitesJSON.getString(i)));

            }
        }

        shortestPath.computeClosestSite();
        shortestPath.updateCreekID(drone);
    }

    @Override
    public Phase nextPhase() {
        return new EndPhase();
    }

    private void rightTurnPos(){
        switch(direction){
            case N -> {
                y--;
                x++;
            }
            case E -> {
                x++;
                y++;
            }
            case S -> {
                y++;
                x--;
            }
            case W -> {
                x--;
                y--;
            }
        }
    }
    private void movePos(){
        switch(direction){
            case N -> {
                y--;
            }
            case E -> {
                x++;
            }
            case S -> {
                y++;
            }
            case W -> {
                x--;
            }
        }
    }
    private void leftTurnPos(){
        switch(direction){
            case N -> {
                y--;
                x--;
            }
            case E -> {
                x++;
                y--;
            }
            case S -> {
                y++;
                x++;
            }
            case W -> {
                x--;
                y++;
            }
        }
    }
}
