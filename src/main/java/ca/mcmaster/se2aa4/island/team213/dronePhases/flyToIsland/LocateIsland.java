package ca.mcmaster.se2aa4.island.team213.dronePhases.flyToIsland;

import ca.mcmaster.se2aa4.island.team213.dronePhases.Phase;

import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import ca.mcmaster.se2aa4.island.team213.enums.EchoResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class LocateIsland implements Phase {
    private final Logger logger = LogManager.getLogger();

    private boolean noDataCollected = true;
    private boolean reachedBorder = false;
    private boolean needNextIteration = false;
    private boolean landFound = false;

    // Queue object used to enqueue set of tasks to completed over number of different iterations
    private Queue<JSONObject> taskQueue = new LinkedList<>();

    @Override
    public boolean lastPhase(){
        return false;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
//        logger.info("Items in Queue {}",taskQueue);

        JSONObject decision = new JSONObject();

        // first check if there are tasks to be executed
        if (!taskQueue.isEmpty()){
            decision = taskQueue.remove();
            return decision;
        }
        // check if we don't have echo data for current location, and if so collect it
        else if (noDataCollected) {
            decision = echoAllDirections(drone.getDirection());
            noDataCollected = false;
        }
        // check if reached the border of the map -> need to turn around
        else if (reachedBorder){
            decision = Action.TURN_RIGHT.toJSON(drone.getDirection());
            taskQueue.add(Action.TURN_RIGHT.toJSON(drone.getDirection().rightTurn()));
            reachedBorder = false;
        }
        // no ground in each direction, need to fly forward and check again
        else if (needNextIteration){
            decision = Action.FLY.toJSON(drone.getDirection());
            queueEchoLeftRight(drone.getDirection());
            needNextIteration = false;
        }

        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        // Based on the attributes of drone, consider which current status of phase
        if (Objects.equals(drone.getEchoAhead(), null) &&  Objects.equals(drone.getEchoRight(), null) && Objects.equals(drone.getEchoLeft(), null)){
            noDataCollected = true;
        }else if (Objects.equals(drone.getEchoAhead(), EchoResult.OUT_OF_RANGE) && Objects.equals(drone.getRangeAhead(), 1)) {
            reachedBorder = true;
        } else if (!(Objects.equals(drone.getEchoAhead(), EchoResult.GROUND) || Objects.equals(drone.getEchoRight(), EchoResult.GROUND) || Objects.equals(drone.getEchoLeft(), EchoResult.GROUND))){
            needNextIteration = true;
        } else if (Objects.equals(drone.getEchoAhead(), EchoResult.GROUND) || Objects.equals(drone.getEchoRight(), EchoResult.GROUND) || Objects.equals(drone.getEchoLeft(), EchoResult.GROUND)){
            landFound = true;
        }
    }

    @Override
    public Phase nextPhase() {
        return new TravelToIsland();
    }

    @Override
    public boolean isFinished() {
        return landFound;
    }

    private JSONObject echoAllDirections(Direction droneDirection){

        JSONObject decision = Action.ECHO_AHEAD.toJSON(droneDirection);

        taskQueue.add(Action.ECHO_RIGHT.toJSON(droneDirection));
        taskQueue.add(Action.ECHO_LEFT.toJSON(droneDirection));

        return decision;
    }

    private void queueEchoLeftRight(Direction droneDirection){
        taskQueue.add(Action.ECHO_RIGHT.toJSON(droneDirection));
        taskQueue.add(Action.ECHO_LEFT.toJSON(droneDirection));
    }



}
