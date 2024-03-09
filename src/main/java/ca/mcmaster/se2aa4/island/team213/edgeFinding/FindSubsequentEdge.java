package ca.mcmaster.se2aa4.island.team213.edgeFinding;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team213.Drone;
import ca.mcmaster.se2aa4.island.team213.Phase;

public class FindSubsequentEdge implements Phase {
    private boolean increaseX, lastEdge;
    private int islandX, islandY;

    public FindSubsequentEdge(int islandX, int islandY, boolean increaseX, boolean lastEdge) {
        this.islandX = islandX;
        this.islandY = islandY;
        this.increaseX = increaseX;
        this.lastEdge = lastEdge;
    }
    
    @Override
    public boolean lastPhase() {
        return false;
    }

    @Override
    public boolean isFinished() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isFinished'");
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDecision'");
    }

    @Override
    public void checkDrone(Drone drone) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkDrone'");
    }

    @Override
    public Phase nextPhase() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nextPhase'");
    }
    
}
