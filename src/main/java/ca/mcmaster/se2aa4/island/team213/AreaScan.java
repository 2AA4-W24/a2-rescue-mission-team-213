package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AreaScan implements DecisionMakerInterface{
    private Perimeter perimeter;
    private Direction direction;
    private int distanceTillHorizontal;
    private int distanceTillVertical;

    private int maxHorizontalDistance;
    private int maxVerticalDistance;

    int x;
    int y;

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


    }
    @Override
    public String makeDecision(Drone drone) {
        JSONObject decision = new JSONObject();
        JSONObject headingDirection = new JSONObject();

        if (distanceTillVertical <= 0){
            direction = direction.rightTurn();
            headingDirection.put("direction", direction);
            decision.put("action", "heading");
            decision.put("parameters", headingDirection);
            maxVerticalDistance -= 5;

            distanceTillVertical = maxVerticalDistance;

        }
        else if (distanceTillHorizontal <= 0){
            direction = direction.rightTurn();
            headingDirection.put("direction", direction);
            decision.put("action", "heading");
            decision.put("parameters", headingDirection);
            maxHorizontalDistance -= 5;

            distanceTillHorizontal = maxHorizontalDistance;
        }
        else{
            if (direction.equals(Direction.N) || direction.equals(Direction.S)){
                distanceTillHorizontal -= 1;
            }
            else{
                distanceTillVertical -= 1;
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
        return decision.toString();
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
        return maxVerticalDistance < 0 && maxHorizontalDistance < 0;
    }

}
