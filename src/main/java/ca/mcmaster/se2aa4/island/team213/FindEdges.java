package ca.mcmaster.se2aa4.island.team213;

import java.util.Queue;

import org.json.JSONObject;

public class FindEdges implements Phase {
    private int x, y;
    private boolean increaseX;
    private Phase findFirstEdge, findSecondEdge, flyPastDeterminedA, findThirdEdge, flyPastDeterminedB, findFourthEdge;
    private Queue<Phase> phases;


    public FindEdges(Drone drone) {
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

        phases.add(this.findFirstEdge);
        phases.add(this.findSecondEdge);
        phases.add(this.flyPastDeterminedA);
        phases.add(this.findThirdEdge);
        phases.add(this.flyPastDeterminedB);
        phases.add(this.findFourthEdge);
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
            if(!this.phases.peek().equals(this.flyPastDeterminedA) || !this.phases.peek().equals(this.flyPastDeterminedB)) {
                increaseXorY();
                swapXorY();
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
            this.flyPastDeterminedA = new FlyPastDetermined(this.increaseX ? this.x: this.y);
        }
        else if(this.phases.peek().equals(this.flyPastDeterminedB)) {
            this.flyPastDeterminedB = new FlyPastDetermined(this.increaseX ? this.x: this.y);
        }
    }
}
