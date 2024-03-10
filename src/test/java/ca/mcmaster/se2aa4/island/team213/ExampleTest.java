package ca.mcmaster.se2aa4.island.team213;

import ca.mcmaster.se2aa4.island.team213.areaScan.AreaScanNew;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class ExampleTest {

    @Test
    public void sampleTest() {
        assertTrue(1 == 1);
    }

//    @Test
//    public void areaScanTest(){
//        int width = 5;
//        int height = 5;
//        Perimeter testPerim = new Perimeter(width,height,CornerPosition.BOTTOMLEFT);
//        AreaScan areaScan = new AreaScan(testPerim);
////        ArrayList<ArrayList<Integer>> grid = new ArrayList<>();
////        for (int i=0; i<height; i++){
////            grid.add(new ArrayList<>());
////        }
////        for (int i=0; i<height; i++){
////            for (int j=0; j<width; j++){
////                grid.get(i).add(0);
////            }
////        }
////        int i = 500;
//        do {
//            areaScan.makeDecision(null);
//
////            grid.get(areaScan.y-1).set(areaScan.x-1, 1);
//
////            for (int i=0; i<height; i++){
////                for (int j=0; j<width; j++){
////                    System.out.printf("%d ", grid.get(i).get(j));
////                }
////                System.out.println();
////            }
////            System.out.println();
////            System.out.println();
////            i--;
//
//            System.out.printf("x: %d, y: %d, maxhoriz: %d, maxvert: %d, distmaxHorz: %d, distmaxVert: %d\n", areaScan.x, areaScan.y, areaScan.maxHorizontalDistance, areaScan.maxVerticalDistance, areaScan.distanceTillHorizontal, areaScan.distanceTillVertical);
//        } while (!areaScan.isFinished());
//        assertEquals(areaScan.maxVerticalDistance, 0);
//        assertEquals(areaScan.maxHorizontalDistance, 0);
//    }


//     @Test
//     public void areaScanNewTest(){
//         int width = 46;
//         int height = 34;
//         AreaScanNew areaScanNew = new AreaScanNew(width, height, Direction.E);
//         while (!areaScanNew.isFinished()){
//             areaScanNew.createDecision(null);
//             System.out.printf("x: %d, y: %d, maxX: %d, maxY: %d, xSteps: %d, ySteps: %d\n", areaScanNew.x, areaScanNew.y, areaScanNew.maxX, areaScanNew.maxY, areaScanNew.xSteps, areaScanNew.ySteps);
//         }
//
//         assertTrue(areaScanNew.maxX - 2 <= 0 || areaScanNew.maxY - 2 <= 0);
//
//     }


}
