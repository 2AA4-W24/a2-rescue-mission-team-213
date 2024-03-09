package ca.mcmaster.se2aa4.island.team213.areaScan;

import ca.mcmaster.se2aa4.island.team213.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

public class AreaScanNew implements Phase {
    public int maxX; //fix
    public int maxY;
    public int x, y;
    public int xSteps, ySteps;
    public Direction direction;
    private GetShortestPath shortestPath;


    private Queue<JSONObject> taskQueue = new LinkedList<>();

    @Override
    public boolean lastPhase(){
        return true;
    }
    public AreaScanNew(int islandx, int islandy, Drone drone){
        maxX = islandx;
        maxY = islandy;
        x = 0;
        y = 0;
        switch (drone.getDirection()){
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

    }
    @Override
    public JSONObject createDecision(Drone drone){
        JSONObject decision = new JSONObject();
        JSONObject headingDirection = new JSONObject();
        // check if tasks are queued
        if (!taskQueue.isEmpty()){
            decision = taskQueue.remove();
            return decision;
        }

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
        return (maxX - 2 <= 0 || maxY - 2 <= 0);
    }
    @Override
    public void checkDrone(Drone drone){
        JSONArray creeksJSON = drone.getScanInfoCreeks();
        if (!creeksJSON.isEmpty()){
            for (int i=0; i<creeksJSON.length(); ++i){
                shortestPath.addCreek(new PointsOfInterest(x,y, creeksJSON.getString(i)));
            }
        }

        JSONArray sitesJSON = drone.getScanInfoSites();
        if (!sitesJSON.isEmpty()){
            for (int i=0; i<sitesJSON.length(); ++i){
                shortestPath.addSite(new PointsOfInterest(x,y, sitesJSON.getString(i)));
            }
        }



    }
    @Override
    public Phase nextPhase(){
        return new AreaScanNew(0, 0, null);
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
