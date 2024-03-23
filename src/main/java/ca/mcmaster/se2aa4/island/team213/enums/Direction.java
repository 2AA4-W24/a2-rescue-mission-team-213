package ca.mcmaster.se2aa4.island.team213.enums;

public enum Direction {
    N,E,S,W;

    public Direction rightTurn(){
        return switch (this) {
            case N -> E;
            case E -> S;
            case S -> W;
            case W -> N;
        };
    }
    public Direction leftTurn(){
        return switch (this) {
            case N -> W;
            case E -> N;
            case S -> E;
            case W -> S;
        };
    }

    @Override
    public String toString(){
        return switch (this) {
            case N -> "N";
            case E -> "E";
            case S -> "S";
            case W -> "W";
        };
    }
}