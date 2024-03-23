package ca.mcmaster.se2aa4.island.team213.dronephases.flytoisland;

import ca.mcmaster.se2aa4.island.team213.dronephases.Phase;

import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import ca.mcmaster.se2aa4.island.team213.enums.EchoResult;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class LocateIsland implements Phase {

    JSONObject decision;

    private boolean landFound = false;

    // Queue object used to enqueue set of tasks to completed over number of different iterations
    private Queue<JSONObject> taskQueue = new LinkedList<>();

    @Override
    public JSONObject createDecision(Drone drone) {

        decision = new JSONObject();

        // check if tasks to be exectued
        if (!taskQueue.isEmpty()){
            decision = taskQueue.remove();
        }
        else  {
            echoAllDirections(drone.getDirection());
            decision = taskQueue.remove();
        }

        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        // Based on the attributes of drone, consider which current status of phase

        // check if we don't have echo data for current location, and if so collect it
        if (Objects.equals(drone.getEchoAhead(), null) &&  Objects.equals(drone.getEchoRight(), null) && Objects.equals(drone.getEchoLeft(), null)){
            echoAllDirections(drone.getDirection());
        }
        // check if reached the border of the map -> need to turn around
        else if (Objects.equals(drone.getEchoAhead(), EchoResult.OUT_OF_RANGE) && Objects.equals(drone.getRangeAhead(), 1)) {
            taskQueue.add(Action.TURN_RIGHT.toJSON(drone.getDirection()));
            taskQueue.add(Action.TURN_RIGHT.toJSON(drone.getDirection().rightTurn()));
        }
        // no land in each direction -> Need to fly forward and echo again
        else if (!(Objects.equals(drone.getEchoAhead(), EchoResult.GROUND) || Objects.equals(drone.getEchoRight(), EchoResult.GROUND) || Objects.equals(drone.getEchoLeft(), EchoResult.GROUND))){
            taskQueue.add(Action.FLY.toJSON(drone.getDirection()));
            queueEchoLeftRight(drone.getDirection());
        }

        else if (Objects.equals(drone.getEchoAhead(), EchoResult.GROUND) || Objects.equals(drone.getEchoRight(), EchoResult.GROUND) || Objects.equals(drone.getEchoLeft(), EchoResult.GROUND)){
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

    private void echoAllDirections(Direction droneDirection){
        taskQueue.add(Action.ECHO_AHEAD.toJSON(droneDirection));
        taskQueue.add(Action.ECHO_RIGHT.toJSON(droneDirection));
        taskQueue.add(Action.ECHO_LEFT.toJSON(droneDirection));

    }

    private void queueEchoLeftRight(Direction droneDirection){
        taskQueue.add(Action.ECHO_RIGHT.toJSON(droneDirection));
        taskQueue.add(Action.ECHO_LEFT.toJSON(droneDirection));
    }

    public JSONObject getDecision(){
        return decision;
    }

    public Queue<JSONObject> getTaskQueue(){
        return taskQueue;
    }



}
