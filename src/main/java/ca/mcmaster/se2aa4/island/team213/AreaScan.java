package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public class AreaScan implements DecisionMakerInterface{
    private Perimeter perimeter;
    private Direction direction;
    private int distanceTillHorizontal;
    private int distanceTillVertical;

    private int maxHorizontalDistance;
    private int maxVerticalDistance;
    public AreaScan(Perimeter perimeter) {
        this.perimeter = perimeter;
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
    public JSONObject makeDecision(Drone drone) {
        JSONObject decision = new JSONObject();
        JSONObject headingDirection = new JSONObject();
//        while (maxVerticalDistance > 0 || maxHorizontalDistance > 0){
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
            }
//        }
        return decision;
    }

}
