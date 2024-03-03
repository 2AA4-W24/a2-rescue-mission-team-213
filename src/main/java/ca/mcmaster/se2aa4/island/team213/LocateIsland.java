package ca.mcmaster.se2aa4.island.team213;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class LocateIsland implements Phase {
    private final Logger logger = LogManager.getLogger();
    private boolean landFound;

    private Direction landDirection;

    // Queue object used to enqueue set of tasks to completed over number of different iterations
    private Queue<JSONObject> taskQueue = new LinkedList<>();

    @Override
    public JSONObject createDecision(Drone drone) {
//        logger.info("Items in Queue {}",taskQueue);
        JSONObject decision = new JSONObject();
        // first check if there are tasks to be executed
        if (!taskQueue.isEmpty()){
            decision = taskQueue.remove();
            if (Objects.equals(decision.get("action"), "echo") ){
                // get which direction is echoing and set drone.echoing accordingly
                JSONObject parameters = decision.getJSONObject("parameters");
                String direction = parameters.getString("direction");
                switch (direction){
                    case "N" ->{
                        drone.setEcho(Direction.N);
                    }
                    case "E" ->{
                        drone.setEcho(Direction.E);
                    }
                    case "S" ->{
                        drone.setEcho(Direction.S);
                    }
                    case "W" ->{
                        drone.setEcho(Direction.W);
                    }
                }

            }
            else if(Objects.equals(decision.get("action"), "fly") ){
                drone.subtractRangeHeading();
            }
        }

        // check if we are on iteration one, where the echo every direction would be null
        else if (Objects.equals(drone.getEchoNorth(), null) &&  Objects.equals(drone.getEchoEast(), null) && Objects.equals(drone.getEchoSouth(), null) && Objects.equals(drone.getEchoWest(), null)){
            decision.put("action", "echo");
            JSONObject parameters = new JSONObject();
            parameters.put("direction", "E");
            decision.put("parameters", parameters);
            drone.setEcho(Direction.E);


            JSONObject enqueueEcho = new JSONObject();
            enqueueEcho.put("action", "echo");
            parameters = new JSONObject();
            parameters.put("direction", "S");
            enqueueEcho.put("parameters", parameters);
            taskQueue.add(enqueueEcho);

            enqueueEcho = new JSONObject();
            enqueueEcho.put("action", "echo");
            parameters = new JSONObject();
            parameters.put("direction", "N");
            enqueueEcho.put("parameters", parameters);
            taskQueue.add(enqueueEcho);

        }


        // check if reached the border of the map -> need to turn around
        else if (Objects.equals(drone.getEchoHeading(), EchoResult.OUT_OF_RANGE) && Objects.equals(drone.getRangeHeading(), 0)){
            decision.put("action", "heading");
            JSONObject parameters = new JSONObject();
            parameters.put("direction", "S");
            decision.put("parameters", parameters);


            // set decision to turn right once and queue another one
            JSONObject secondTurn = new JSONObject();
            secondTurn.put("action", "heading");
            JSONObject secondParameters = new JSONObject();
            secondParameters.put("direction", "W");
            decision.put("parameters", secondParameters);
            taskQueue.add(secondTurn);
        }

        // reached the island
        else if (drone.getRangeHeading() == 0 && Objects.equals(drone.getEchoHeading(), EchoResult.GROUND)){
            landFound = true;
            decision.put("action", "stop");
        }

        // Technical Debt: Assumes heading east
        // no ground in each direction, need to fly forward and check again
        else if (!(Objects.equals(drone.getEchoEast(), EchoResult.GROUND) || Objects.equals(drone.getEchoSouth(), EchoResult.GROUND) || Objects.equals(drone.getEchoWest(), EchoResult.GROUND) || Objects.equals(drone.getEchoNorth(), EchoResult.GROUND))){
            decision.put("action", "fly");

            JSONObject enqueueEcho = new JSONObject();
            enqueueEcho.put("action", "echo");
            JSONObject parameters = new JSONObject();
            parameters.put("direction", "E");
            enqueueEcho.put("parameters", parameters);
            taskQueue.add(enqueueEcho);

            enqueueEcho = new JSONObject();
            enqueueEcho.put("action", "echo");
            parameters = new JSONObject();
            parameters.put("direction", "S");
            enqueueEcho.put("parameters", parameters);
            taskQueue.add(enqueueEcho);


            enqueueEcho = new JSONObject();
            enqueueEcho.put("action", "echo");
            parameters = new JSONObject();
            parameters.put("direction", "N");
            enqueueEcho.put("parameters", parameters);
            taskQueue.add(enqueueEcho);


        }

        // ground found change heading if need be and queue fly commands
        else if (Objects.equals(drone.getEchoEast(), EchoResult.GROUND) || Objects.equals(drone.getEchoSouth(), EchoResult.GROUND) || Objects.equals(drone.getEchoWest(), EchoResult.GROUND) || Objects.equals(drone.getEchoNorth(), EchoResult.GROUND)){
//            logger.info("!!!!!!!!!!! LAND FOUND LAND FOUND LAND FOUND !!!!!!!!!!!!!!!!!!!!!! ");
            // Keep heading in direction
            if (Objects.equals(drone.getEchoHeading(), EchoResult.GROUND)){
                decision.put("action", "fly");
                // need to keep heading i - 1 times in that direction
                for (int i = 0; i < drone.getRangeHeading(); i++){
                    taskQueue.add(decision);
                }
            }
            // Ground is north
            else if (Objects.equals(drone.getEchoNorth(), EchoResult.GROUND)){
                decision.put("action", "heading");
                JSONObject parameters = new JSONObject();
                parameters.put("direction", "N");
                drone.setDirectionHeading(Direction.N);
                decision.put("parameters", parameters);
                // need to keep heading i - 1 times in that direction
                for (int i = 0; i < drone.getRangeHeading(); i++){
                    JSONObject flyCommand = new JSONObject();
                    flyCommand.put("action", "fly");
                    taskQueue.add((flyCommand));
                }
            }
            // Ground is east
            else if (Objects.equals(drone.getEchoEast(), EchoResult.GROUND)){
                decision.put("action", "heading");
                JSONObject parameters = new JSONObject();
                parameters.put("direction", "E");
                drone.setDirectionHeading(Direction.E);
                decision.put("parameters", parameters);
                // need to keep heading i - 1 times in that direction
                for (int i = 0; i < drone.getRangeHeading(); i++){
                    JSONObject flyCommand = new JSONObject();
                    flyCommand.put("action", "fly");
                    taskQueue.add((flyCommand));
                }
            }
            // Ground is south
            else if (Objects.equals(drone.getEchoSouth(), EchoResult.GROUND)){
                decision.put("action", "heading");
                JSONObject parameters = new JSONObject();
                parameters.put("direction", "S");
                drone.setDirectionHeading(Direction.S);
                decision.put("parameters", parameters);
                // need to keep heading i - 1 times in that direction
                for (int i = 0; i < drone.getRangeHeading(); i++){
                    JSONObject flyCommand = new JSONObject();
                    flyCommand.put("action", "fly");
                    taskQueue.add((flyCommand));
                }
            }
            // Ground is West
            else if (Objects.equals(drone.getEchoSouth(), EchoResult.GROUND)){
                decision.put("action", "heading");
                JSONObject parameters = new JSONObject();
                parameters.put("direction", "W");
                drone.setDirectionHeading(Direction.W);
                decision.put("parameters", parameters);
                // need to keep heading i - 1 times in that direction
                for (int i = 0; i < drone.getRangeHeading(); i++){
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
        return new TestPhase();
    }

    @Override
    public boolean isFinished() {
        return landFound;
    }



}
