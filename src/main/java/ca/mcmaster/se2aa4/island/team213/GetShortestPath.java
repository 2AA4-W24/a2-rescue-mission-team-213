package ca.mcmaster.se2aa4.island.team213;
import java.util.ArrayList;
import java.util.Optional;
public class GetShortestPath {

    public SiteCandidate closestCreek;
    public ArrayList<PointsOfInterest> creeks;
    public ArrayList<PointsOfInterest> sites;


    public GetShortestPath(){
        creeks = new ArrayList<>();
        sites = new ArrayList<>();

    }
    public void addCreek(PointsOfInterest creek){
        creeks.add(creek);
    }
    public void addSite(PointsOfInterest site){
        sites.add(site);
    }

    public void computeClosestSite(){
        if (!sites.isEmpty() && !creeks.isEmpty()){
//            System.out.println("TEST -----------------------------------------------------");
            for (PointsOfInterest site: sites){
                for (PointsOfInterest creek: creeks){
                    double distance = Math.sqrt(Math.pow((site.getX()-creek.getX()),2)+Math.pow((site.getY()- creek.getY()),2));
                    if (getClosestCreek().isEmpty() || distance < closestCreek.distanceFromCreek){ //TODO turn into getter
                        closestCreek = new SiteCandidate(creek.getID(), distance);
                    }
                }
            }

        }


    }
    public void updateCreekID(Drone drone){
        drone.setCreekID(closestCreek.id);
    }

    public Optional<SiteCandidate> getClosestCreek(){
        return (closestCreek == null? Optional.empty() : Optional.of(this.closestCreek));
    }
}
