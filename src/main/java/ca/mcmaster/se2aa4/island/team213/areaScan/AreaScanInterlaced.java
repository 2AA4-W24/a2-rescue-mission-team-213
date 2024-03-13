package ca.mcmaster.se2aa4.island.team213.areaScan;

import ca.mcmaster.se2aa4.island.team213.Direction;
import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

public class AreaScanInterlaced implements Phase {

    public int stepsBeforeTurn;
    public int turnsBeforeReturn;

    //Moves since turn is initialized to 1, as start position is one block ahead
    public int movesSinceTurn = 0;
    public int turns = 0;

    Direction direction;
    public int x;
    public int y;

    int blocksMovedSideways = 0;
    boolean extraColumn;

    boolean lastTurnLeft = true;

    public boolean turnedAround = false;

    private final Logger logger = LogManager.getLogger();

    private Queue<JSONObject> taskQueue = new LinkedList<>();

    public AreaScanInterlaced(int islandx, int islandy, Direction droneDirection){
        logger.info("---------------------------AREASCANINTERLACED CREATED---------------------------------------");
        logger.info(islandx);
        logger.info(islandy);
        logger.info(droneDirection);
        direction = droneDirection;
        this.x = 0;
        this.y = 0;

        switch (droneDirection){
            case E, W -> {
                this.stepsBeforeTurn = islandx-3;

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


    }

    @Override
    public boolean lastPhase() {
        return true;
    }

    @Override
    public boolean isFinished() {
        if (turnedAround && turns == turnsBeforeReturn-1 && movesSinceTurn == stepsBeforeTurn){
            logger.info("ENDED");
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
        logger.info("x: " + x + "  y: " + y);
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

        if (!turnedAround && turns == turnsBeforeReturn && movesSinceTurn == stepsBeforeTurn){
            System.out.printf("hello-------------------------\n");
            System.out.printf("%b\n\n", lastTurnLeft);
            if (lastTurnLeft){
                leftTurnPos();
                direction = direction.leftTurn();
            }
            else{
                rightTurnPos();
                direction = direction.rightTurn();
            }

//            leftTurnPos();
//            direction = direction.leftTurn();
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
            turnedAround = true;

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
//            leftTurnPos();
//            direction = direction.leftTurn();
            decision.put("action", "heading");
            headingDirection.put("direction", direction);
            decision.put("parameters", headingDirection);
            taskQueue.add(decision);

            turns = 0;
            movesSinceTurn = 0;
//            lastTurnLeft = false;

        }
        else if (movesSinceTurn == stepsBeforeTurn){
            if (lastTurnLeft){
                rightTurnPos();
                direction = direction.rightTurn();
            }
            else{
                leftTurnPos();
                direction = direction.leftTurn();
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
            decision = new JSONObject();
            decision.put("action", "fly");
            taskQueue.add(decision);
            movePos();
            movesSinceTurn++;
        }
        JSONObject enqueueScan2 = new JSONObject();
        enqueueScan2.put("action", "scan");
        taskQueue.add(enqueueScan2);

        finaldecision = taskQueue.remove();
        return finaldecision;

    }

    @Override
    public void checkDrone(Drone drone) {

    }

    @Override
    public Phase nextPhase() {
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
    private void movePos(){
        switch(direction){
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
    private void leftTurnPos(){
        switch(direction){
            case N -> {
                y++;
                x--;
            }
            case E -> {
                x++;
                y++;
            }
            case S -> {
                y--;
                x++;
            }
            case W -> {
                x--;
                y--;
            }
        }
    }
}
