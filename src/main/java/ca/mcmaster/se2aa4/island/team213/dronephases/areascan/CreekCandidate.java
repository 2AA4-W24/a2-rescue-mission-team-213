package ca.mcmaster.se2aa4.island.team213.dronephases.areascan;

public record CreekCandidate(PointOfInterest creekCandidate, double distanceFromSite) {

    public String getClosestCreekID() {
        return this.creekCandidate.getID();
    }
}
