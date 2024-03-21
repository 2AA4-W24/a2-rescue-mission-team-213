package ca.mcmaster.se2aa4.island.team213;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
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
        site = new PointOfInterest(10, 10, "1");
        creek = new PointOfInterest(13, 14, "2");
    }

    @Test
    public void testImpossibleTiles() {
        map.determineImpossibleTiles(site, creek);
        indices = map.getMap();

        int siteX = site.getX();
        int siteY = site.getY();
        int creekX = creek.getX();
        int creekY = creek.getY();
        double distanceX = Math.abs(siteX - creekX);
        double distanceY = Math.abs(siteY - creekY);
        int distance = (int) Math.ceil(Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2)));
        
        assertFalse(indices[siteY + distance + 1][siteX]);
        assertTrue(indices[siteY + distance + 2][siteX]);

        assertFalse(indices[siteY - distance - 1][siteX]);
        assertTrue(indices[siteY - distance - 2][siteX]);

        assertFalse(indices[siteY][siteX + distance + 1]);
        assertTrue(indices[siteY][siteX + distance + 2]);

        assertFalse(indices[siteY][siteX - distance - 1]);
        assertTrue(indices[siteY][siteX - distance - 2]);
    }
}
