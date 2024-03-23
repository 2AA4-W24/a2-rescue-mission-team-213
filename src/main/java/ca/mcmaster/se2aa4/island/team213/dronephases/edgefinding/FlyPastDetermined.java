package ca.mcmaster.se2aa4.island.team213.dronephases.edgefinding;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.dronephases.Phase;

public class FlyPastDetermined implements Phase {
    private boolean isFinished;
    private boolean increaseX;
    private int islandX;
    private int islandY;
    private int edgesFound;
    private int flyActionsLeft;


    public FlyPastDetermined(int islandX, int islandY, boolean increaseX, int edgesFound, int flyActionsLeft) {
        this.isFinished = false;

        this.islandX = islandX;
        this.islandY = islandY;
        this.increaseX = increaseX;
        this.edgesFound = edgesFound;
        this.flyActionsLeft = flyActionsLeft;
    }

    @Override
    public boolean isFinished() {
        return this.isFinished;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision;
        decision = Action.FLY.toJSON(drone.getDirection());
        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        this.flyActionsLeft -= 1;
        if(this.flyActionsLeft == 0) {
            this.isFinished = true;
        }
    }

    @Override
    public Phase nextPhase() {
        return new FindSubsequentEdge(this.islandX, this.islandY, this.increaseX, this.edgesFound);
    }

}