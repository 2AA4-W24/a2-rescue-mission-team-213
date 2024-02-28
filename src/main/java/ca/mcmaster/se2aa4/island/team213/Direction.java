package ca.mcmaster.se2aa4.island.team213;

public enum Direction {
    N,E,S,W;

    public Direction rightTurn(){
        return switch (this) {
            case N -> Direction.E;
            case E -> Direction.S;
            case S -> Direction.W;
            case W -> Direction.N;
        };
    }
}
