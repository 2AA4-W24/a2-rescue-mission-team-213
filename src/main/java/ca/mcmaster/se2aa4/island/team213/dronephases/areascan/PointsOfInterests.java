package ca.mcmaster.se2aa4.island.team213.dronephases.areascan;
import ca.mcmaster.se2aa4.island.team213.*;

import java.util.ArrayList;
import java.util.List;

import ca.mcmaster.se2aa4.island.team213.dronephases.carveperimeter.DronePosition;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.json.JSONArray;

public class PointsOfInterests {
    private CreekCandidate closestCreek;
    private final List<PointOfInterest> creeks;
    private final List<PointOfInterest> sites;
    private boolean hasSiteCreekPair = false;
    public PointsOfInterests(){
        this.creeks = new ArrayList<>();
        this.sites = new ArrayList<>();
    }
    public void computeClosestSite(){
        if (!sites.isEmpty() && !creeks.isEmpty()){
            for (PointOfInterest site: sites){
                for (PointOfInterest creek: creeks){
                    double distance = Math.sqrt(Math.pow(site.getX()-creek.getX(),2)+Math.pow(site.getY()- creek.getY(),2));
                    if (getClosestCreek() == null || distance < closestCreek.distanceFromSite()){
                        closestCreek = new CreekCandidate(creek, distance);
                        hasSiteCreekPair = true;
                    }
                }
            }
        }
        /*
         * Picks first creek it finds even if a site has not been found,
         * setting the creek candidate's distance to infinity
         */
        else if (!creeks.isEmpty()){
            closestCreek = new CreekCandidate(creeks.get(0), Double.MAX_VALUE);
        }
    }
    public void updateCreekID(Drone drone){
        if (closestCreek != null){
            drone.setCreekID(closestCreek.getClosestCreekID());
        }
    }

    /*
     * Returns true if a site and creek pair exist
     */
    public boolean checkIfPair(){
        return hasSiteCreekPair;
    }

    /*
     * Gets the site if it has been found
     */
    public PointOfInterest getSite(){
        if (!sites.isEmpty()){
            return sites.get(0);
        }
        return null;
    }

    public PointOfInterest getClosestCreek(){
        if (this.closestCreek != null){
            return this.closestCreek.creekCandidate();

        }
        return null;
    }

    public void addCreeks(JSONArray creeksJSON, DronePosition dronePosition){
        if (!creeksJSON.isEmpty()){
            for (int i=0; i<creeksJSON.length(); ++i){
                creeks.add(new PointOfInterest(dronePosition.getDroneX(), dronePosition.getDroneY(), creeksJSON.getString(i)));
            }
        }
    }

    public void addSites(JSONArray sitesJSON, DronePosition dronePosition){
        if (!sitesJSON.isEmpty()){
            for (int i=0; i<sitesJSON.length(); ++i){
                sites.add(new PointOfInterest(dronePosition.getDroneX(), dronePosition.getDroneY(), sitesJSON.getString(i)));
            }
        }
    }

    /*
     * Determines the maximum coordinates that is worth searching
     * based on current distance between a site-creek pair
     */
    public int maxCoord(Direction startDirection){
        if (this.checkIfPair()){
            switch (startDirection){
                case N -> {
                    return sites.get(0).getX() + (int)closestCreek.distanceFromSite();
                }
                case E -> {
                    return sites.get(0).getY() + (int)closestCreek.distanceFromSite();
                }
                case S -> {
                    return sites.get(0).getX() - (int)closestCreek.distanceFromSite();
                }
                case W -> {
                    return sites.get(0).getY() - (int)closestCreek.distanceFromSite();
                }
            }
        }
        return 0;
    }


}
