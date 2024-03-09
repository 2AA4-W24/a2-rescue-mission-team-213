package ca.mcmaster.se2aa4.island.team213;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class FindEdges implements Phase {
    private int x, y;
    private boolean increaseX;
    private Phase findFirstEdge, findSecondEdge, flyPastDeterminedA, findThirdEdge, flyPastDeterminedB, findFourthEdge, endPhase;
    private Queue<Phase> phases;

    private final Logger logger = LogManager.getLogger();

    public FindEdges(Drone drone) {
        this.phases = new LinkedList<Phase>();
        parseStartingDirection(drone.getDirection());
        setupQueue(); 
    }

    private void setupQueue() {
        this.findFirstEdge = new FindFirstEdge();
        this.findSecondEdge = new FindSubsequentEdge();
        this.flyPastDeterminedA = new FlyPastDetermined(0);
        this.findThirdEdge = new FindSubsequentEdge();
        this.flyPastDeterminedB = new FlyPastDetermined(0);
        this.findFourthEdge = new FindSubsequentEdge();
        this.endPhase = new EndPhase();

        this.phases.add(this.findFirstEdge);
        this.phases.add(this.findSecondEdge);
        this.phases.add(this.flyPastDeterminedA);
        this.phases.add(this.endPhase);
    }

    private void parseStartingDirection(Direction direction) {
        if(direction.toString().equals("N") || direction.toString().equals("S")) {
            this.increaseX = false;
            this.x = 1;
            this.y = 0;
        } else {
            this.increaseX = true;
            this.x = 0;
            this.y = 1;
        }
    }


    @Override
    public boolean lastPhase(){
        return false;
    }
    @Override
    public boolean isFinished() {        
        if(this.phases.peek().equals(this.endPhase)) {
            return true;
        }

        if(this.phases.peek().isFinished()) {
            logger.info("!!!!!! CURRENT PHASE DONE !!!!!!");
            if(this.phases.peek().equals(this.findFirstEdge) || this.phases.peek().equals(this.findSecondEdge) || this.phases.peek().equals(this.findThirdEdge) ||this.phases.peek().equals(this.findFourthEdge)) {
                increaseXorY();
                swapXorY();
            }

            this.phases.remove();

            if(this.phases.peek().equals(this.flyPastDeterminedA) || this.phases.peek().equals(this.flyPastDeterminedB)) {
                setFlyActionsLeft();
            }

            if(this.phases.peek().equals(this.endPhase)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public JSONObject createDecision(Drone drone) {
        JSONObject decision = new JSONObject();
        decision = this.phases.peek().createDecision(drone);
        return decision;
    }

    @Override
    public void checkDrone(Drone drone) {
        logger.info("** PREVIOUS DECISION: " + drone.getPreviousDecision());
        logger.info("**");
        logger.info("**");
        
        Phase currentPhase = this.phases.peek();
        currentPhase.checkDrone(drone);

        if(drone.getPreviousDecision().equals(Action.echoRight)) {
            increaseXorY();
        }
        logger.info("** CURRENT X: " + this.x);
        logger.info("** CURRENT Y: " + this.y);
    }

    @Override
    public Phase nextPhase() {
        return new TestPhase();
    }

    private void increaseXorY() { 
        if(this.increaseX) {
            this.x += 1;
        } 
        else {
            this.y += 1;
        }
    }

    private void swapXorY() {
        this.increaseX = this.increaseX ? false : true;
    }

    private void setFlyActionsLeft() {
        if(this.phases.peek().equals(this.flyPastDeterminedA)) {
            logger.info("SETTING FLY A TO: " + Integer.toString(this.increaseX ? this.x: this.y));
            phases.remove();
            phases.remove();

            this.flyPastDeterminedA = new FlyPastDetermined(this.increaseX ? this.x: this.y);
            phases.add(this.flyPastDeterminedA);
            phases.add(this.findThirdEdge);
            phases.add(this.flyPastDeterminedB);
        }
        else if(this.phases.peek().equals(this.flyPastDeterminedB)) {
            logger.info("SETTING FLY B TO: " + Integer.toString(this.increaseX ? this.x: this.y));
            phases.remove();
            phases.remove();

            this.flyPastDeterminedB = new FlyPastDetermined(this.increaseX ? this.x: this.y);
            phases.add(this.flyPastDeterminedB);
            phases.add(this.findFourthEdge);
        }
        phases.add(this.endPhase);
    }

    // following methods are temporary abstraction leaks for unit testing
    // make copy unless return value is immutable
    public boolean getIncreaseX() {
        return this.increaseX;
    }

    public Queue<Phase> getPhases() {
        return this.phases;
    }

    public Phase getFindFirstEdge() {
        return this.findFirstEdge;
    }

    public Phase getFindSecondEdge() {
        return this.findSecondEdge;
    }

    public Phase getFindThirdEdge() {
        return this.findThirdEdge;
    }

    public Phase getFlyPastDeterminedA() {
        return this.flyPastDeterminedA;
    }

    public void removeFromPhases() {
        this.phases.remove();
    }
}
