package ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter;

import java.util.LinkedList;
import java.util.Queue;

import ca.mcmaster.se2aa4.island.team213.*;
import ca.mcmaster.se2aa4.island.team213.dronePhases.Phase;
import ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan.AreaScanInterlaced;
import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class CarvePerimeter implements Phase {
    private boolean isFinished;
    private Queue<Action> decisionQueue;
    private BooleanMap checkedTiles;
    private DronePosition dronePosition;
    private int horizontalFlyActions, verticalFlyActions, rightTurnsPerformed;
    private Direction droneDirection;

    private final Logger logger = LogManager.getLogger();

    public CarvePerimeter(int islandX, int islandY, Direction droneDirection) {
        this.isFinished = false;
        this.decisionQueue = new LinkedList<Action>();
        this.dronePosition = new DronePosition(islandX, islandY, droneDirection);
        this.checkedTiles = new BooleanMap(islandX, islandY);
        this.droneDirection = droneDirection;

        this.rightTurnsPerformed = 0;
        this.verticalFlyActions = islandY - 3;
        this.horizontalFlyActions = islandX - 3;
        
        loadDecisionQueue(droneDirection);
    }

    private void loadDecisionQueue(Direction droneDirection) {
        this.decisionQueue.add(Action.ECHO_RIGHT);
        loadFlyAndEchoActions((droneDirection.equals(Direction.N) || droneDirection.equals(Direction.S)) ? this.verticalFlyActions : this.horizontalFlyActions);
        this.decisionQueue.add(Action.TURN_RIGHT);
    }

    private void loadFlyAndEchoActions(int amount) {
        for(int i = 0; i < amount; i++) {
            this.decisionQueue.add(Action.FLY);
            this.decisionQueue.add(Action.ECHO_RIGHT);
        }
    }

    @Override
    public boolean lastPhase() {
        return false;
    }

    @Override
    public boolean isFinished() {
        if(this.isFinished) {

            int groundFound = 0;

            for(int i = 0; i < checkedTiles.islandY; i++) {
                String test = "";
                for(int j = 0; j < checkedTiles.islandX; j++) {
                    if(this.dronePosition.getDroneY() == i && this.dronePosition.getDroneX() == j) {
                        test += "1 ";
                    } else {
                        if(this.checkedTiles.map[i][j] == true) {
                            test += "- ";
                        } else {
                            test += "0 ";

                            groundFound += 1;

                        }
                    }
                }
                logger.info(test);
            }

            logger.info("Total tiles: " + (checkedTiles.islandY * checkedTiles.islandX));
            logger.info("Tiles left to scan: " + groundFound);
            logger.info("Estimated battery cost: ~" + ((checkedTiles.islandY * checkedTiles.islandX * 8) - (groundFound * 8)));

        }
        return this.isFinished;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision;
        Action nextAction = this.decisionQueue.peek();
        
        decision = nextAction.toJSON(drone.getDirection());
        if(nextAction.equals(Action.TURN_RIGHT)) {
            this.rightTurnsPerformed += 1;
        }
        if(this.rightTurnsPerformed == 4) {
            this.isFinished = true;
        }
        this.decisionQueue.remove();

        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        this.dronePosition.updatePositionAfterDecision(drone.getPreviousDecision(), drone.getDirection());
        logger.info("Drone X: " + dronePosition.getDroneX());
        logger.info("Drone Y: " + dronePosition.getDroneY());
        if(drone.getPreviousDecision().equals(Action.TURN_RIGHT)) {
            loadDecisionQueue(drone.getDirection());
        }
        else if(drone.getPreviousDecision().equals(Action.ECHO_RIGHT)) {
            checkedTiles.updateMapFromEchoRight(drone.getDirection(), drone.getRangeRight(), drone.getEchoRight(), this.dronePosition);
        }
    }

    @Override
    public Phase nextPhase() {
//        return new EndPhase();
        // return area scan interlaced with dronePosition, checkedTiles, droneDirection
         return new AreaScanInterlaced(dronePosition, checkedTiles, droneDirection);
    }
    
    public int getHorizontalFlyActions() {
        return this.horizontalFlyActions;
    }

    public int getVerticalFlyActions() {
        return this.verticalFlyActions;
    }

    public int getDroneX() {
        return this.dronePosition.getDroneX();
    }

    public int getDroneY() {
        return this.dronePosition.getDroneY();
    }
}
