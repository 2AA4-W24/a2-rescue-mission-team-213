package ca.mcmaster.se2aa4.island.team213;

import ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan.AreaScanInterlaced;
import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.BooleanMap;
import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.DronePosition;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class AreaScanTest {
    private final Logger logger = LogManager.getLogger();
    private Drone dE;
    private AreaScanInterlaced areaScanNorth;
    private DronePosition dronePosition;
    private Direction droneDirection;
    @BeforeEach
    public void setUp() {
        dE = new Drone("E", 15000);
        droneDirection = Direction.E;
        dronePosition = new DronePosition(1,0,Direction.E);
        areaScanNorth = new AreaScanInterlaced(dronePosition, new BooleanMap(20,20), droneDirection);
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
