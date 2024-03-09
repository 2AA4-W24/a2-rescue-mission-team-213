package ca.mcmaster.se2aa4.island.team213;
import java.util.ArrayList;
import java.util.Optional;
public class GetShortestPath {

    public SiteCandidate closestSite;
    public ArrayList<PointsOfInterest> creeks;
    public ArrayList<PointsOfInterest> sites;


    public GetShortestPath(Drone drone){

    }
    public void addCreek(PointsOfInterest creek){
        creeks.add(creek);
    }
    public void addSite(PointsOfInterest site){
        sites.add(site);
    }

    public void computeClosestSite(){
        if (!sites.isEmpty() && !creeks.isEmpty()){
            for (PointsOfInterest site: sites){
                for (PointsOfInterest creek: creeks){
                    double distance = Math.sqrt(Math.pow((site.getX()-creek.getX()),2)+Math.pow((site.getY()- creek.getY()),2));
                    if (getClosestSite().isEmpty() || distance < closestSite.distanceFromCreek){ //turn into getter
                        //TODO: ^^^ make sure closesSite isn't null in the beginning
//                        closestSite.distanceFromCreek = distance;
                        closestSite = new SiteCandidate(site.getID(), distance);
                    }
                }
            }
        }


    }
    public void updateSiteID(Drone drone){
        drone.setSiteID(closestSite.id);
    }

    public Optional<SiteCandidate> getClosestSite(){
        return (closestSite == null? Optional.empty() : Optional.of(this.closestSite));
    }
}
