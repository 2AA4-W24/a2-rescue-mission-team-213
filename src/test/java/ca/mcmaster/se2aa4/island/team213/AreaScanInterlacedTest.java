package ca.mcmaster.se2aa4.island.team213;
import ca.mcmaster.se2aa4.island.team213.areaScan.AreaScanInterlaced;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
public class AreaScanInterlacedTest {
        @Test
    public void areaScanNewTest(){
        int width = 46;
        int height = 34;
        int i=150;

            AreaScanInterlaced areaScanInterlaced = new AreaScanInterlaced(9, 10, Direction.N);
            System.out.printf("%d %d\n", areaScanInterlaced.stepsBeforeTurn, areaScanInterlaced.turnsBeforeReturn);
        while (!areaScanInterlaced.isFinished()){
            --i;
            areaScanInterlaced.createDecision(null);
            System.out.printf("x: %d, y: %d turned: %b, turns: %d\n", areaScanInterlaced.x, areaScanInterlaced.y, areaScanInterlaced.turnedAround, areaScanInterlaced.turns);
        }

        assertTrue(true);

    }
}
