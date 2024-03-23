package ca.mcmaster.se2aa4.island.team213;

import ca.mcmaster.se2aa4.island.team213.dronephases.areascan.EdgeMap;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

public class EdgeMapTest {

    private EdgeMap edgeMap;
    Map<Integer, int[]> edgeHashMap;
    private final Logger logger = LogManager.getLogger();
    @BeforeEach
    public void setUp(){
        boolean[][] mapOfCheckedTiles = {
                {false, true, true, false, false},
                {true, false, false, true, true},
                {true, false, false, true, true},
                {true, false, false, true, true},
                {false, true, false, true, true},
                {false, true, false, true, true},
                {false, true, true, false, false}
        };

        edgeMap = new EdgeMap(Direction.E, mapOfCheckedTiles);
        edgeHashMap = new HashMap<>();
    }
    @Test
    public void TestEdgeMap(){
        edgeHashMap = edgeMap.getEdgeMap();
        for (HashMap.Entry<Integer, int[]> entry : edgeHashMap.entrySet()) {
            Integer key = entry.getKey();
            int[] value = entry.getValue();
            String logMessage = String.format("Key: %d, Value: [%d, %d]", key, value[0], value[1]);
            logger.info(logMessage);
        }
        assertEquals(edgeHashMap.get(2)[0], 0);
        assertEquals(edgeHashMap.get(2)[1], 3);
        assertEquals(edgeHashMap.get(3)[0], 0);
        assertEquals(edgeHashMap.get(3)[1], 3);
        assertEquals(edgeHashMap.get(4)[0], -1);
        assertEquals(edgeHashMap.get(4)[1], 3);
        assertEquals(edgeHashMap.get(5)[0], -1);
        assertEquals(edgeHashMap.get(5)[1], 3);

    }
}
