package ca.mcmaster.se2aa4.island.team213;

import ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan.PointsOfInterests;
import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.DronePosition;
import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ShortestDistanceTest {
    private PointsOfInterests shortestPath;
    private DronePosition dronePosition;

    private Drone drone;
    @BeforeEach
    public void setup(){
        drone = new Drone("N", 15000);
        shortestPath = new PointsOfInterests();
        dronePosition = new DronePosition(50,50, Direction.E);
    }
    @Test
    public void shortestDistanceTest(){
        shortestPath.addCreeks(makeJSON("1"), dronePosition);
        for (int i=0; i<10; ++i){
            dronePosition.updatePositionAfterDecision(Action.FLY, Direction.E);
        }
        shortestPath.addCreeks(makeJSON("2"), dronePosition);
        for (int i=0; i<10; ++i){
            dronePosition.updatePositionAfterDecision(Action.FLY, Direction.E);
        }
        shortestPath.addCreeks(makeJSON("3"), dronePosition);

        for (int i=0; i<10; ++i){
            dronePosition.updatePositionAfterDecision(Action.FLY, Direction.E);
        }

        shortestPath.addSites(makeJSON("4"), dronePosition);

        shortestPath.computeClosestSite();
        shortestPath.updateCreekID(drone);

        assertEquals(drone.getSiteID(), "3");
    }

    @Test
    public void noCreekOrSite(){
        shortestPath.computeClosestSite();
        shortestPath.updateCreekID(drone);
        assertEquals(drone.getSiteID(), "no creek found");
    }

    private JSONArray makeJSON(String id){
        JSONArray result = new JSONArray();
        result.put(id);
        return result;
    }
}
