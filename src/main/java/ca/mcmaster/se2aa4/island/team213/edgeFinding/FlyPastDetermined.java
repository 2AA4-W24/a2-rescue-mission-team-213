package ca.mcmaster.se2aa4.island.team213.edgeFinding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.Phase;

public class FlyPastDetermined implements Phase{
    private boolean isFinished;
    private boolean increaseX;
    private int islandX, islandY;
    private int edgesFound;
    private int flyActionsLeft;

    private final Logger logger = LogManager.getLogger();

    public FlyPastDetermined(int islandX, int islandY, boolean increaseX, int edgesFound, int flyActionsLeft) {
        logger.info("** FlyPastDetermined created with " + flyActionsLeft + " fly actions **");
        this.isFinished = false;

        this.islandX = islandX;
        this.islandY = islandY;
        this.increaseX = increaseX;
        this.edgesFound = edgesFound;
        this.flyActionsLeft = flyActionsLeft;
    }

    @Override
    public boolean lastPhase() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return this.isFinished;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision = new JSONObject();
        decision.put("action", "fly");
        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        // logger.info("CURRENT FLY ACTIONS LEFT: " + Integer.toString(this.flyActionsLeft));
        this.flyActionsLeft -= 1;
        // logger.info("SUBTRACTING FLY ACTIONS TO: " + Integer.toString(this.flyActionsLeft));
        if(this.flyActionsLeft == 0) {
            this.isFinished = true;
        }
    }

    @Override
    public Phase nextPhase() {
        return new FindSubsequentEdge(this.islandX, this.islandY, this.increaseX, this.edgesFound);
    }
    
}
