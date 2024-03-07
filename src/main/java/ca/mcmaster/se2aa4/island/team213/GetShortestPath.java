package ca.mcmaster.se2aa4.island.team213;
import java.util.ArrayList;
import java.util.Optional;
public class GetShortestPath {

    private String closestSite;

//    private PointsOfInterest creeks;
//    private PointsOfInterest sites;
    private ArrayList<PointsOfInterest> creeks;
    private ArrayList<PointsOfInterest> sites;

    public GetShortestPath() {

    }

    //    public GetShortestPath(PointsOfInterest poi){
//
//    }
    public void addCreek(PointsOfInterest creek){
        creeks.add(creek);
    }



    public Optional<String> getClosestSite(){
        return (closestSite == null? Optional.empty() : Optional.of(this.closestSite));
    }
}
