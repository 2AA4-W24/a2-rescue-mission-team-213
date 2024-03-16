package ca.mcmaster.se2aa4.island.team213.carvePerimeter;

import ca.mcmaster.se2aa4.island.team213.Action;
import ca.mcmaster.se2aa4.island.team213.Direction;

public class DronePosition {
    private int droneX, droneY;

    public DronePosition(int islandX, int islandY, Direction droneDirection) {
        initializeDroneXY(islandX, islandY, droneDirection);
    }

    private void initializeDroneXY(int islandX, int islandY, Direction droneDirection) {
        if(droneDirection.equals(Direction.N)) {
            this.droneX = 0;
            this.droneY = islandY - 2;
        }
        else if(droneDirection.equals(Direction.E)) {
            this.droneX = 1;
            this.droneY = 0;
        }
        else if(droneDirection.equals(Direction.S)) {
            this.droneX = islandX - 1;
            this.droneY = 1;
        }
        else if(droneDirection.equals(Direction.W)) {
            this.droneX = islandX - 2;
            this.droneY = islandY - 1;
        }
    }

    public void updatePositionAfterDecision(Action previousDecision, Direction droneDirection) {
        if(previousDecision.equals(Action.FLY)) {
            updateAfterFly(droneDirection);
        }
        else if(previousDecision.equals(Action.TURN_RIGHT)) {
            updateAfterTurn(droneDirection);
        }
    }

    private void updateAfterFly(Direction droneDirection) {
        if(droneDirection.equals(Direction.N)) {
            decreaseDroneY();
        }
        else if(droneDirection.equals(Direction.E)) {
            increaseDroneX();
        }
        else if(droneDirection.equals(Direction.S)) {
            increaseDroneY();
        }
        else if(droneDirection.equals(Direction.W)) {
            decreaseDroneX();
        }
    }

    private void updateAfterTurn(Direction droneDirection) {
        if(droneDirection.equals(Direction.N)) {
            decreaseDroneX();
            decreaseDroneY();
        }
        else if(droneDirection.equals(Direction.E)) {
            increaseDroneX();
            decreaseDroneY();
        }
        else if(droneDirection.equals(Direction.S)) {
            increaseDroneX();
            increaseDroneY();
        }
        else if(droneDirection.equals(Direction.W)) {
            decreaseDroneX();
            increaseDroneY();
        }
    }

    private void increaseDroneX() {
        this.droneX += 1;
    }

    private void decreaseDroneX() {
        this.droneX -= 1;
    }

    private void increaseDroneY() {
        this.droneY += 1;
    }

    private void decreaseDroneY() {
        this.droneY -= 1;
    }

    public int getDroneX() {
        return this.droneX;
    }

    public int getDroneY() {
        return this.droneY;
    }
}
