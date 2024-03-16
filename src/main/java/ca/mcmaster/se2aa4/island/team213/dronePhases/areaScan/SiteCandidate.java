package ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan;

public class SiteCandidate{

    public double distanceFromCreek;
    String id;
    public SiteCandidate(String id, double distanceFromCreek) {
        this.id = id;
        this.distanceFromCreek = distanceFromCreek;
    }
}
