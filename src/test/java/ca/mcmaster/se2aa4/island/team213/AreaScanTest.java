package ca.mcmaster.se2aa4.island.team213;

import ca.mcmaster.se2aa4.island.team213.dronephases.areascan.AreaScanInterlaced;
import ca.mcmaster.se2aa4.island.team213.dronephases.carveperimeter.BooleanMap;
import ca.mcmaster.se2aa4.island.team213.dronephases.carveperimeter.DronePosition;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AreaScanTest {

    private Drone dE;
    private AreaScanInterlaced areaScanNorth;
    private DronePosition dronePosition;

    @BeforeEach
    public void setUp() {
        dE = new Drone("E", 15000);
        dronePosition = new DronePosition(1,0,Direction.E);
        areaScanNorth = new AreaScanInterlaced(dronePosition, new BooleanMap(20,20), Direction.E);
    }

    @Test
    public void finalPositionTest(){
        while(!areaScanNorth.isFinished()){
            areaScanNorth.createDecision(dE);
        }
        //Expected final position of drone for 20x20 map starting facing East
        assertEquals(dronePosition.getDroneX(), 19);
        assertEquals(dronePosition.getDroneY(), 17);
    }



}
