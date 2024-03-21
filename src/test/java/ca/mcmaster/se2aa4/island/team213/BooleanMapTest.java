package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan.PointOfInterest;
import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.BooleanMap;

public class BooleanMapTest {
    BooleanMap map;
    boolean[][] indices;
    PointOfInterest site, creek;
    String mapRow;
    
    private final Logger logger = LogManager.getLogger();

   @BeforeEach
   public void setUp() {
       map = new BooleanMap(50, 50);
       PointOfInterest site = new PointOfInterest(10, 10, "1");
       PointOfInterest creek = new PointOfInterest(13, 14, "2");
       map.determineImpossibleTiles(site, creek);
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
       assertFalse(indices[15][10]);
       assertTrue(indices[16][10]);

       assertFalse(indices[5][10]);
       assertTrue(indices[4][10]);

       assertFalse(indices[10][15]);
       assertTrue(indices[10][16]);

       assertFalse(indices[10][5]);
       assertTrue(indices[10][4]);
   }
}
