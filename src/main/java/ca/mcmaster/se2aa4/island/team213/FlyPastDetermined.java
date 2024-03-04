package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public class FlyPastDetermined implements Phase {
    private int flyActionsLeft;
    private boolean isFinished;

    public FlyPastDetermined(int flyActionsLeft) {
        this.flyActionsLeft = flyActionsLeft;
        this.isFinished = false;
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
        this.flyActionsLeft -= 1;
        if(this.flyActionsLeft == 0) {
            this.isFinished = true;
        }
    }

    @Override
    public Phase nextPhase() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nextPhase'");
    }
    
    public void setFlyActionsLeft(int flyActionsLeft) {
        this.flyActionsLeft = flyActionsLeft;
    }
}
