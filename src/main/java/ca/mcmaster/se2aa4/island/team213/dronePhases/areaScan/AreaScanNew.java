package ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan;

import ca.mcmaster.se2aa4.island.team213.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

public class AreaScanNew implements Phase {
    public int maxX; //fix
    public int maxY;
    public int x, y;

//    boolean lowBattery = false;
    public int xSteps, ySteps;
    public Direction direction;
    private GetShortestPath shortestPath;

    private final Logger logger = LogManager.getLogger();

    private Queue<JSONObject> taskQueue = new LinkedList<>();


    public AreaScanNew(int islandx, int islandy, Direction droneDirection){
        logger.info("** AreaScanNew Created *****");
        shortestPath = new GetShortestPath();
        logger.info(islandx);
        logger.info(islandy);
        maxX = islandx;
        maxY = islandy;
        x = 0;
        y = 0;
        switch (droneDirection){
            case E -> {
                direction = Direction.E;
            }
            case S -> {
                direction = Direction.S;
            }
            case W -> {
                direction = Direction.W;
            }
            case N -> {
                direction = Direction.N;
            }
        }
//        logger.info("---------fdsfsdjfsadlfjsadlkfasdfs--------------------");

    }
    @Override
    public JSONObject createDecision(Drone drone){
        JSONObject decision = new JSONObject();
        JSONObject headingDirection = new JSONObject();
        // check if tasks are queued
        if (!taskQueue.isEmpty()){
//            logger.info("REMOVING TASK FROM QUEUE - -------");
            decision = taskQueue.remove();
            return decision;
        }
//        logger.warn("OTHER TASK ---------------------------------------------------------------");

        /*
         * Checks if steps taken in certain direction has reached threshold. The row/column occupied
         * from previous pass through as well as drone turning radius is taken in account, giving a
         * buffer requirement of 3 blocks
         */
        if ((direction == Direction.E || direction == Direction.W) && xSteps == maxX - 3){
            rightTurnPos();
            direction = direction.rightTurn();
            maxX--;
            xSteps = 0;

            headingDirection.put("direction", direction);
            decision.put("action", "heading");
            decision.put("parameters", headingDirection);

        }
        else if ((direction == Direction.N || direction == Direction.S) && ySteps == maxY - 3){
            rightTurnPos();
            direction = direction.rightTurn();
            maxY--;
            ySteps = 0;

            headingDirection.put("direction", direction);
            decision.put("action", "heading");
            decision.put("parameters", headingDirection);
        }
        //Move forward if drone doesn't have to turn
        else if (direction == Direction.N){
            ySteps++;
            y++;

            decision.put("action", "fly");
        }
        else if (direction == Direction.S){
            ySteps++;
            y--;

            decision.put("action", "fly");
        }
        else if (direction == Direction.E){
            xSteps++;
            x++;

            decision.put("action", "fly");
        }
        else if (direction == Direction.W){
            xSteps++;
            x--;

            decision.put("action", "fly");
        }

        // Enqueue scan for next iteration
        JSONObject enqueueScan = new JSONObject();
        enqueueScan.put("action", "scan");
        taskQueue.add(enqueueScan);

        return decision;
    }
    @Override
    public boolean isFinished(){
        if (maxX - 2 <= 0 || maxY - 2 <= 0) {
//            logger.info("hellllllllllloooooooooooooooooooooooo");
            logger.info(shortestPath.closestCreek.toString());
            return true;
        }
        return false;

//        return (maxX - 2 <= 0 || maxY - 2 <= 0);
    }

    @Override
    public boolean lastPhase(){
        return true;
    }
    @Override
    public void checkDrone(Drone drone){

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
//            logger.info("------------------BEGIN CHECKDRONE -----------------------------------------++++++++++++++++++++++");
            for (int i=0; i<sitesJSON.length(); ++i){
//                logger.info("-----------------------------------------SITE FOUND: " + x + " "+ y + " " + sitesJSON.getString(i));
                shortestPath.addSite(new PointsOfInterest(x,y, sitesJSON.getString(i)));

            }
        }
//        logger.info("------------------MIDDLE CHECKDRONE --------------------------------------");
        shortestPath.computeClosestSite();
        shortestPath.updateCreekID(drone);
//        logger.info("------------------END CHECKDRONE --------------------------------------");
    }
    @Override
    public Phase nextPhase(){
        //Never reaches this method
        return null;
    }

    private void rightTurnPos(){
        switch(direction){
            case N -> {
                y++;
                x++;
            }
            case E -> {
                x++;
                y--;
            }
            case S -> {
                y--;
                x--;
            }
            case W -> {
                x--;
                y++;
            }
        }
    }
}
