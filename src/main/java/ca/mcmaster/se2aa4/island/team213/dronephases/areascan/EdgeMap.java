package ca.mcmaster.se2aa4.island.team213.dronephases.areascan;

import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import java.util.HashMap;
import java.util.Map;

public class EdgeMap {
    private final Map<Integer, int[]> edgeMap;

    /*
     * EdgeMap is a dictionary containing the left/right-most  or top/bottom-most
     * coordinates of the start of the land, which is used in InterlacedAreaScan
     * to determine if an early turn optimization can be performed
     */
    public EdgeMap(Direction startDirection, boolean[][] mapOfCheckedTiles){
        edgeMap = new HashMap<>();
        switch (startDirection){
            case E, W -> {

                for (int y=1; y<mapOfCheckedTiles.length-1; ++y){
                    boolean foundFirstLand = false;
                    int lastX = 0;
                    int[] xPos = new int[2];
                    for (int x=0; x<mapOfCheckedTiles[0].length; ++x){
                        if (!mapOfCheckedTiles[y][x]){
                            lastX = x;
                        }
                        if (!foundFirstLand && !mapOfCheckedTiles[y][x]){
                            foundFirstLand = true;
                            xPos[0] = x-1;
                        }
                    }
                    xPos[1] = lastX+1;
                    edgeMap.put(y, xPos);
                }
            }
            case N, S -> {
                for (int x=1; x<mapOfCheckedTiles[0].length-1; ++x){
                    boolean foundFirstLand = false;
                    int lastY = 0;
                    int[] yPos = new int[2];
                    for (int y=0; y<mapOfCheckedTiles.length; ++y){
                        if (!mapOfCheckedTiles[y][x]){
                            lastY = y;
                        }
                        if (!foundFirstLand && !mapOfCheckedTiles[y][x]){
                            foundFirstLand = true;
                            yPos[0] = y-1;
                        }
                    }
                    yPos[1] = lastY+1;
                    edgeMap.put(x, yPos);
                }
            }
        }
    }

    public Map<Integer, int[]> getEdgeMap(){
        return this.edgeMap;
    }
}
