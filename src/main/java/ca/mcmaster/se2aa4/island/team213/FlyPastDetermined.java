package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public class FlyPastDetermined implements Phase {
    private int flyActionsLeft;
    private boolean isFinished;
    private boolean turnedRight;

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

        if(!this.turnedRight) {
            this.turnedRight = true;
            JSONObject parameter = new JSONObject();
            parameter.put("direction", drone.getDirection().rightTurn());
            decision.put("parameters", parameter);
            decision.put("action", "heading");  
        }
        else {
            decision.put("action", "fly");
        }
        
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
