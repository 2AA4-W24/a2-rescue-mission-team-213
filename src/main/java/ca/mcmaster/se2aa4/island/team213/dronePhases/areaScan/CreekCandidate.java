package ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan;

public record CreekCandidate(PointOfInterest creekCandidate, double distanceFromCreek) {

    public String getClosestCreekID() {
        return this.creekCandidate.getID();
    }
}
