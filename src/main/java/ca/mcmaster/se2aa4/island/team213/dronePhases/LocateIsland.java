package ca.mcmaster.se2aa4.island.team213.dronePhases;

import ca.mcmaster.se2aa4.island.team213.*;
import ca.mcmaster.se2aa4.island.team213.dronePhases.edgeFinding.FindFirstEdge;

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
    private boolean arrivedAtLand;
    private Direction directionFacing;

    private boolean traversingToLand = false;

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
        // reached the island
        else if (traversingToLand && taskQueue.isEmpty()){
            directionFacing = drone.getDirection();
            arrivedAtLand = true;
//            decision.put("action", "echo");
//            JSONObject parameters = new JSONObject();
//            parameters.put("direction", drone.getDirection().toString());
//            decision.put("parameters", parameters);
//            decision.put("action", "stop");
            decision.put("action", "scan");
        }
        // check if we are on iteration one, where the echo every direction would be null
        else if (Objects.equals(drone.getEchoAhead(), null) &&  Objects.equals(drone.getEchoRight(), null) && Objects.equals(drone.getEchoLeft(), null)){
            decision.put("action", "echo");
            JSONObject parameters = new JSONObject();
            parameters.put("direction", drone.getDirection().toString());
            decision.put("parameters", parameters);

            JSONObject enqueueEcho = new JSONObject();
            enqueueEcho.put("action", "echo");
            parameters = new JSONObject();
            parameters.put("direction", drone.getDirection().rightTurn().toString());
            enqueueEcho.put("parameters", parameters);
            taskQueue.add(enqueueEcho);

            enqueueEcho = new JSONObject();
            enqueueEcho.put("action", "echo");
            parameters = new JSONObject();
            parameters.put("direction", drone.getDirection().leftTurn().toString());
            enqueueEcho.put("parameters", parameters);
            taskQueue.add(enqueueEcho);

        }


        // check if reached the border of the map -> need to turn around
        else if (Objects.equals(drone.getEchoAhead(), EchoResult.OUT_OF_RANGE) && Objects.equals(drone.getRangeAhead(), 1)){
            decision.put("action", "heading");
            JSONObject parameters = new JSONObject();
            // TODO: violates law of demeter
//            logger.info(drone.getDirection());
//            logger.info("!!!! NEED TO TURN: Direction set to {}", drone.getDirection().rightTurn().toString());
            parameters.put("direction", drone.getDirection().rightTurn().toString());
            decision.put("parameters", parameters);

//            logger.info(drone.getDirection());

            // set decision to turn right once and queue another one
            JSONObject secondTurn = new JSONObject();
            secondTurn.put("action", "heading");
            JSONObject secondParameters = new JSONObject();
            // TODO: violates law of demeter
//            logger.info("!!!! NEED TO TURN: Direction set to {}", drone.getDirection().rightTurn().rightTurn().toString());
            secondParameters.put("direction", drone.getDirection().rightTurn().rightTurn().toString());
            secondTurn.put("parameters", secondParameters);
            taskQueue.add(secondTurn);

        }

        // no ground in each direction, need to fly forward and check again
        else if (!(Objects.equals(drone.getEchoAhead(), EchoResult.GROUND) || Objects.equals(drone.getEchoRight(), EchoResult.GROUND) || Objects.equals(drone.getEchoLeft(), EchoResult.GROUND))){
            decision.put("action", "fly");

            JSONObject enqueueEcho = new JSONObject();
            enqueueEcho.put("action", "echo");
            JSONObject parameters = new JSONObject();
            parameters.put("direction", drone.getDirection().toString());
            enqueueEcho.put("parameters", parameters);
            taskQueue.add(enqueueEcho);

            enqueueEcho = new JSONObject();
            enqueueEcho.put("action", "echo");
            parameters = new JSONObject();
            parameters.put("direction", drone.getDirection().rightTurn().toString());
            enqueueEcho.put("parameters", parameters);
            taskQueue.add(enqueueEcho);


            enqueueEcho = new JSONObject();
            enqueueEcho.put("action", "echo");
            parameters = new JSONObject();
            parameters.put("direction", drone.getDirection().leftTurn().toString());
            enqueueEcho.put("parameters", parameters);
            taskQueue.add(enqueueEcho);


        }
        // ground found change heading if need be and queue fly commands
        else if (Objects.equals(drone.getEchoAhead(), EchoResult.GROUND) || Objects.equals(drone.getEchoRight(), EchoResult.GROUND) || Objects.equals(drone.getEchoLeft(), EchoResult.GROUND)){
//           logger.info("!!!!!!!!!!! LAND FOUND LAND FOUND LAND FOUND !!!!!!!!!!!!!!!!!!!!!! ");
//           logger.info(drone.getEchoAhead());
//           logger.info(drone.getEchoRight());
//           logger.info(drone.getEchoLeft());
           traversingToLand = true;
            // Keep heading in direction
            if (Objects.equals(drone.getEchoAhead(), EchoResult.GROUND)){
                decision.put("action", "fly");
                // need to keep heading i - 1 times in that direction
                for (int i = 0; i < drone.getRangeAhead(); i++){
                    taskQueue.add(decision);
                }
            }
            // Ground is right
            else if (Objects.equals(drone.getEchoRight(), EchoResult.GROUND)){
                decision.put("action", "heading");
                JSONObject parameters = new JSONObject();
                parameters.put("direction", drone.getDirection().rightTurn().toString());
                decision.put("parameters", parameters);
                // need to keep heading i - 1 times in that direction
                for (int i = 0; i < drone.getRangeRight(); i++){
                    JSONObject flyCommand = new JSONObject();
                    flyCommand.put("action", "fly");
                    taskQueue.add(flyCommand);
                }
            }
            // Ground is left
            else if (Objects.equals(drone.getEchoLeft(), EchoResult.GROUND)){
                decision.put("action", "heading");
                JSONObject parameters = new JSONObject();
                parameters.put("direction", drone.getDirection().leftTurn().toString());

                decision.put("parameters", parameters);
                // need to keep heading i - 1 times in that direction
                for (int i = 0; i < drone.getRangeLeft(); i++){
                    JSONObject flyCommand = new JSONObject();
                    flyCommand.put("action", "fly");
                    taskQueue.add(flyCommand);
                }
            }
            
        }

        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
            // temporarily empty until final refactor
    }

    @Override
    public Phase nextPhase() {
        return new FindFirstEdge(directionFacing);
    }

    @Override
    public boolean isFinished() {
        return arrivedAtLand;
    }



}
