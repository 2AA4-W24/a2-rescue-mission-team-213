package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.BooleanMap;

public class BooleanMapTest {
    BooleanMap map;
    boolean[][] indices;
    String mapRow;
    
    private final Logger logger = LogManager.getLogger();

    @BeforeEach
    public void setUp() {
        map = new BooleanMap(50, 50);
        map.determineImpossibleTiles(10, 10, 13, 14);
        indices = map.getMap();
        
        for(int i = 0; i < map.getIslandY(); i++) {
            mapRow = "";
            for(int j = 0; j < map.getIslandX(); j++) {
                mapRow += (indices[i][j] ? "- " : "0 ");
            }
            logger.info(mapRow);
        }
    }

    @Test
    public void testImpossibleTiles() {
        assertFalse(indices[10][15]);
        assertTrue(indices[10][16]);
    }
}
