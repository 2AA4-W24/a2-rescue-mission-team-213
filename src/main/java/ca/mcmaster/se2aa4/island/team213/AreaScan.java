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
    public String makeDecision() {
        JSONObject decision = new JSONObject();
        JSONObject headingDirection = new JSONObject();
//        while (maxVerticalDistance > 0 || maxHorizontalDistance > 0){
            if (distanceTillVertical <= 0){
                headingDirection.put("direction", turnRight());
                decision.put("action", "heading");
                decision.put("parameters", headingDirection);
                maxVerticalDistance -= 5;
                direction = rightTurn();
                distanceTillVertical = maxVerticalDistance;

            }
            else if (distanceTillHorizontal <= 0){
                headingDirection.put("direction", turnRight());
                decision.put("action", "heading");
                decision.put("parameters", headingDirection);
                maxHorizontalDistance -= 5;
                direction = rightTurn();
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
        return direction.toString();
    }
    public String turnRight(){
        switch (direction) {
            case N -> {
                return "E";
            }
            case E -> {
                return "S";
            }
            case S -> {
                return "W";
            }
            case W -> {
                return "N";
            }
            default -> {
                return null;
            }
        }

    }
    public Direction rightTurn(){
        return switch (direction) {
            case N -> Direction.E;
            case E -> Direction.S;
            case S -> Direction.W;
            case W -> Direction.N;
        };
    }
}
