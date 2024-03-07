package ca.mcmaster.se2aa4.island.team213;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class FindEdges implements Phase {
    private int x, y;
    private boolean increaseX;
    private Phase findFirstEdge, findSecondEdge, flyPastDeterminedA, findThirdEdge, flyPastDeterminedB, findFourthEdge;
    private Queue<Phase> phases;

    private final Logger logger = LogManager.getLogger();

    public FindEdges(Drone drone) {
        logger.info("**");
        logger.info("**");
        logger.info("**** REACHED FIND EDGES ****");
        logger.info("**");
        logger.info("**");
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

        this.phases.add(this.findFirstEdge);
        this.phases.add(this.findSecondEdge);
        this.phases.add(this.flyPastDeterminedA);
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
    public boolean isFinished() {        
        if(this.phases.peek().equals(null)) {
            return true;
        }

        if(this.phases.peek().isFinished()) {
            logger.info("!!!!!! CURRENT PHASE DONE !!!!!!");
            if(this.phases.peek().equals(this.findFirstEdge) || this.phases.peek().equals(this.findSecondEdge) || this.phases.peek().equals(this.findThirdEdge) ||this.phases.peek().equals(this.findFourthEdge)) {
                increaseXorY();
                swapXorY();
                logger.info("!!!!!! X AND Y SWAPPED !!!!!!");
            }

            this.phases.remove();

            if(this.phases.peek().equals(this.flyPastDeterminedA) || this.phases.peek().equals(this.flyPastDeterminedB)) {
                setFlyActionsLeft();
            }

            if(this.phases.peek().equals(null)) {
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
        Phase currentPhase = this.phases.peek();
        currentPhase.checkDrone(drone);

        if(drone.getPreviousDecision().equals("echoRight")) {
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

            this.flyPastDeterminedA = new FlyPastDetermined(this.increaseX ? this.x: this.y);
            phases.add(this.flyPastDeterminedA);
            phases.add(this.findThirdEdge);
            phases.add(this.flyPastDeterminedB);
        }
        else if(this.phases.peek().equals(this.flyPastDeterminedB)) {
            logger.info("SETTING FLY B TO: " + Integer.toString(this.increaseX ? this.x: this.y));
            phases.remove();

            this.flyPastDeterminedB = new FlyPastDetermined(this.increaseX ? this.x: this.y);
            phases.add(this.flyPastDeterminedB);
            phases.add(this.findFourthEdge);
        }
    }

    // following methods are temporary abstraction leaks for unit testing
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

    public Phase getFlyPastDeterminedA() {
        return this.flyPastDeterminedA;
    }

    public void removeFromPhases() {
        this.phases.remove();
    }
}
