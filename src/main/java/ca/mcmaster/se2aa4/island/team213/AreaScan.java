package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AreaScan{
    private Perimeter perimeter;
    private Direction direction;
    public int distanceTillHorizontal; //MAKE PRIVATE AFTER TEST
    public int distanceTillVertical; //MAKE PRIVATE

    public int maxHorizontalDistance;
    public int maxVerticalDistance;

    public int x;
    public int y;

    private ArrayList<PointsOfInterest> creeks;
    private ArrayList<PointsOfInterest> sites;


    public AreaScan(Perimeter perimeter) {
        this.perimeter = perimeter;
        this.x = 0;
        this.y = 0;
        switch (perimeter.cornerPosition) {
            case TOPRIGHT -> {
                direction = Direction.E;
                distanceTillVertical = 0;
                distanceTillHorizontal = perimeter.height;
            }
            case TOPLEFT -> {
                direction = Direction.N;
                distanceTillHorizontal = 0;
                distanceTillVertical = perimeter.width;
            }
            case BOTTOMLEFT -> {
                direction = Direction.W;
                distanceTillVertical = 0;
                distanceTillHorizontal = perimeter.height;
            }
            case BOTTOMRIGHT -> {
                direction = Direction.S;
                distanceTillHorizontal = 0;
                distanceTillVertical = perimeter.width;
            }
        }
        maxHorizontalDistance = perimeter.height;
        maxVerticalDistance = perimeter.width;
        this.x = 0;
        this.y = 0;


    }
//    @Override
    public JSONObject makeDecision(Drone drone) {
        JSONObject decision = new JSONObject();
        JSONObject headingDirection = new JSONObject();

        if (distanceTillVertical <= 1 && maxVerticalDistance > 0){
            rightTurnCoord();
            direction = direction.rightTurn();

            headingDirection.put("direction", direction);
            decision.put("action", "heading");
            decision.put("parameters", headingDirection);
            maxVerticalDistance -= 1;

            distanceTillVertical = Math.max(0,maxVerticalDistance);

        }
        else if (distanceTillHorizontal <= 1 && maxHorizontalDistance > 0){
            rightTurnCoord();
            direction = direction.rightTurn();

            headingDirection.put("direction", direction);
            decision.put("action", "heading");
            decision.put("parameters", headingDirection);
            maxHorizontalDistance -= 1;

            distanceTillHorizontal = Math.max(0,maxHorizontalDistance);
        }
        else{
            if (direction.equals(Direction.N) || direction.equals(Direction.S)){
                distanceTillHorizontal = Math.max(--distanceTillHorizontal, 0);
            }
            else{
                distanceTillVertical = Math.max(--distanceTillVertical,0);
            }
            decision.put("action", "fly");

            //Keep track of relative position
            switch(direction) {
                case N -> {
                    y++;
                }
                case E -> {
                    x++;
                }
                case S -> {
                    y--;
                }
                case W -> {
                    x--;
                }
            }
        }
        decision.put("action", "scan");
        return decision;
    }

    public void receiveResult(JSONObject response){
        JSONObject extraInfo = response.getJSONObject("extras");
        JSONArray creeksJSON = extraInfo.getJSONArray("creeks");
        if (!creeksJSON.isEmpty()){
            for (int i=0; i<creeksJSON.length(); ++i){
                creeks.add(new PointsOfInterest(x,y, creeksJSON.getString(i)));
            }
        }

        JSONArray sitesJSON = extraInfo.getJSONArray("sites");
        if (!sitesJSON.isEmpty()){
            for (int i=0; i<sitesJSON.length(); ++i){
                sites.add(new PointsOfInterest(x,y, sitesJSON.getString(i)));
            }
        }

    }

    boolean isFinished(){
        return maxVerticalDistance <= 0 || maxHorizontalDistance <= 0;
    }

    void rightTurnCoord(){
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
