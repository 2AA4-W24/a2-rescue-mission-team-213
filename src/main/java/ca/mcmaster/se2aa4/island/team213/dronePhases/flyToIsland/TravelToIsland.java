package ca.mcmaster.se2aa4.island.team213.dronePhases.flyToIsland;

import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.dronePhases.Phase;
import ca.mcmaster.se2aa4.island.team213.dronePhases.edgeFinding.FindFirstEdge;
import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import ca.mcmaster.se2aa4.island.team213.enums.EchoResult;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class TravelToIsland implements Phase {
    private Direction directionFacing;
    private Queue<JSONObject> taskQueue = new LinkedList<>();

    @Override
    public boolean lastPhase(){
        return false;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision = new JSONObject();
        JSONObject flyCommand = Action.FLY.toJSON(drone.getDirection());
        // Keep heading in direction
        if(!taskQueue.isEmpty()){
            decision = taskQueue.remove();
        }
        else if (Objects.equals(drone.getEchoAhead(), EchoResult.GROUND)){
            decision = flyCommand;
            // need to keep heading i - 1 times in that direction
            for (int i = 0; i < drone.getRangeAhead(); i++){
                taskQueue.add(flyCommand);
            }
        }
        // Ground is right
        else if (Objects.equals(drone.getEchoRight(), EchoResult.GROUND)){
            decision = Action.TURN_RIGHT.toJSON(drone.getDirection());
            // need to keep heading i - 1 times in that direction
            for (int i = 0; i < drone.getRangeRight(); i++){
                taskQueue.add(flyCommand);
            }
        }
        // Ground is left
        else if (Objects.equals(drone.getEchoLeft(), EchoResult.GROUND)){
            decision = Action.TURN_LEFT.toJSON(drone.getDirection());
            // need to keep heading i - 1 times in that direction
            for (int i = 0; i < drone.getRangeLeft(); i++){
                taskQueue.add(flyCommand);
            }
        }
        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        directionFacing = drone.getDirection();
    }

    @Override
    public Phase nextPhase() {
        return new FindFirstEdge(directionFacing);
    }

    @Override
    public boolean isFinished() {
        return taskQueue.isEmpty();
    }


}
