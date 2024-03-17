package ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan;
import ca.mcmaster.se2aa4.island.team213.*;
import java.util.ArrayList;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class GetShortestPath {
    private final Logger logger = LogManager.getLogger();

    public CreekCandidate closestCreek;
    public ArrayList<PointsOfInterest> creeks;
    public ArrayList<PointsOfInterest> sites;

    public PointsOfInterest closestCreekPOI;

    boolean havePair = false;
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
            for (PointsOfInterest site: sites){
                for (PointsOfInterest creek: creeks){
                    double distance = Math.sqrt(Math.pow((site.getX()-creek.getX()),2)+Math.pow((site.getY()- creek.getY()),2));
                    if (getClosestCreek().isEmpty() || distance < closestCreek.distanceFromCreek){ //TODO turn into getter
                        closestCreek = new CreekCandidate(creek.getID(), distance);
                        closestCreekPOI = new PointsOfInterest(creek.getX(), creek.getY(), creek.getID());
                        havePair = true;
                    }
                }
            }
        }
        /*
         * Picks last creek it finds even if a site has not been found
         */
        else if (!creeks.isEmpty()){
            closestCreek = new CreekCandidate(creeks.get(creeks.size()-1).getID(), Double.MAX_VALUE);
        }
    }
    public void updateCreekID(Drone drone){
        if (closestCreek != null){
            drone.setCreekID(closestCreek.id);
        }

    }

    /*
     * Returns true if a site and creek pair exist
     */
    public boolean checkIfPair(){
        return havePair;
    }

    public PointsOfInterest getSite(){
        if (!sites.isEmpty()){
            return sites.get(0);
        }
        return null;
    }

    public PointsOfInterest getCreek(){
        if (closestCreekPOI != null){
            return closestCreekPOI;
        }
        return null;
    }


    public Optional<CreekCandidate> getClosestCreek(){
        return (closestCreek == null? Optional.empty() : Optional.of(this.closestCreek));
    }
}
