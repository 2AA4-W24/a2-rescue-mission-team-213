package ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan;

public class CreekCandidate {

    private final double distanceFromCreek;
    private final PointOfInterest creekCandidate;
    public CreekCandidate(PointOfInterest creekCandidate, double distanceFromCreek) {
        this.creekCandidate = creekCandidate;
        this.distanceFromCreek = distanceFromCreek;
    }

    public double getDistanceFromCreek() {
        return this.distanceFromCreek;
    }

    public String getClosestCreekID(){
        return this.creekCandidate.getID();
    }

    public PointOfInterest getClosestCreek(){
        return this.creekCandidate;
    }
}
