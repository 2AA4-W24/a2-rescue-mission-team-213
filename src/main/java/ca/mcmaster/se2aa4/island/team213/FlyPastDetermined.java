package ca.mcmaster.se2aa4.island.team213;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class FlyPastDetermined implements Phase {
    private int flyActionsLeft;
    private boolean isFinished;

    private final Logger logger = LogManager.getLogger();

    public FlyPastDetermined(int flyActionsLeft) {
        this.flyActionsLeft = flyActionsLeft;
        this.isFinished = false;
    }

    @Override
    public boolean lastPhase(){
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
        logger.info("CURRENT FLY ACTIONS LEFT: " + Integer.toString(this.flyActionsLeft));
        this.flyActionsLeft -= 1;
        logger.info("SUBTRACTING FLY ACTIONS TO: " + Integer.toString(this.flyActionsLeft));
        if(this.flyActionsLeft == 0) {
            this.isFinished = true;
        }
    }

    @Override
    public Phase nextPhase() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nextPhase'");
    }
}
