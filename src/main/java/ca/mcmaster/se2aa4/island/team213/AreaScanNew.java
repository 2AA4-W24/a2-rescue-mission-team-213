package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public class AreaScanNew implements Phase{
    public int maxX; //fix
    public int maxY;
    public int x, y;
    public int xSteps, ySteps;
    Direction direction;
    public AreaScanNew(Perimeter perimeter){
        maxX = perimeter.width;
        maxY = perimeter.height;
        x = 0;
        y = 0;
        switch (perimeter.cornerPosition){
            case TOPLEFT -> {
                direction = Direction.E;
            }
            case TOPRIGHT -> {
                direction = Direction.S;
            }
            case BOTTOMRIGHT -> {
                direction = Direction.W;
            }
            case BOTTOMLEFT -> {
                direction = Direction.N;
            }
        }

    }
    @Override
    public JSONObject createDecision(Drone drone){
        JSONObject decision = new JSONObject();
        JSONObject headingDirection = new JSONObject();
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
        else if (direction == Direction.N){
            ySteps++;
            y++;
        }
        else if (direction == Direction.S){
            ySteps++;
            y--;
        }
        else if (direction == Direction.E){
            xSteps++;
            x++;
        }
        else if (direction == Direction.W){
            xSteps++;
            x--;
        }


        decision.put("action", "scan");
        return decision;
    }
    @Override
    public boolean isFinished(){
        return (maxX - 2 <= 0 || maxY - 2 <= 0);
    }
    @Override
    public void checkDrone(Drone drone){
        System.out.println("hi");
    }
    @Override
    public Phase nextPhase(){
        System.out.println("hello");
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
