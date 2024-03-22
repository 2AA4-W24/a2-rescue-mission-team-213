package ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan;

public class PointOfInterest {
    private final int x;
    private final int y;
    private final String id;

    public PointOfInterest(int x, int y, String id){
        this.x = x;
        this.y = y;
        this.id = id;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public String getID(){
        return this.id;
    }
}
