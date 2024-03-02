package ca.mcmaster.se2aa4.island.team213;

class PointsOfInterest {
    private int x;
    private int y;
    private String id;

    public PointsOfInterest(int x, int y, String id){
        this.x = x;
        this.y = y;
        this.id = id;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public String getID(){
        return id;
    }
}
