package ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter;

import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;

public class DronePosition {
    private int droneX, droneY;

    public DronePosition(int islandX, int islandY, Direction droneDirection) {
        initializeDroneXY(islandX, islandY, droneDirection);
    }

    private void initializeDroneXY(int islandX, int islandY, Direction droneDirection) {
        switch (droneDirection){
            case N -> {
                this.droneX = 0;
                this.droneY = islandY - 2;
            }
            case E -> {
                this.droneX = 1;
                this.droneY = 0;
            }
            case S -> {
                this.droneX = islandX - 1;
                this.droneY = 1;
            }
            case W -> {
                this.droneX = islandX - 2;
                this.droneY = islandY - 1;
            }
        }
    }

    public void updatePositionAfterDecision(Action previousDecision, Direction droneDirection) {
        switch (previousDecision){
            case FLY -> updateAfterFly(droneDirection);
            case TURN_RIGHT -> updateAfterRightTurn(droneDirection);
            case TURN_LEFT -> updateAfterLeftTurn(droneDirection);
            default -> { /* do nothing */}
        }
    }

    private void updateAfterFly(Direction droneDirection) {
        switch (droneDirection){
            case N -> decreaseDroneY();
            case E -> increaseDroneX();
            case S -> increaseDroneY();
            case W -> decreaseDroneX();
        }
    }

    /*
     * Right Turn
     */
    private void updateAfterRightTurn(Direction droneDirection) {
        switch (droneDirection){
            case N -> {
                decreaseDroneX();
                decreaseDroneY();
            }
            case E -> {
                increaseDroneX();
                decreaseDroneY();
            }
            case S -> {
                increaseDroneX();
                increaseDroneY();
            }
            case W -> {
                decreaseDroneX();
                increaseDroneY();
            }
        }
    }

    /*
     * Left Turn
     */
    private void updateAfterLeftTurn(Direction droneDirection) {
        switch (droneDirection){
            case N -> {
                increaseDroneX();
                decreaseDroneY();
            }
            case E -> {
                increaseDroneX();
                increaseDroneY();
            }
            case S -> {
                decreaseDroneX();
                increaseDroneY();
            }
            case W -> {
                decreaseDroneX();
                decreaseDroneY();
            }
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
