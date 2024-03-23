package ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan;

import ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter.DronePosition;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import java.util.HashMap;

public class EdgeMap {

    HashMap<Integer, int[]> edgeMap = new HashMap<>();

    public HashMap<Integer, int[]> getEdgeMap(){
        return this.edgeMap;
    }
    public EdgeMap(Direction direction, boolean[][] mapOfCheckedTiles){

        switch (direction){
            case E, W -> {

                for (int y=1; y<mapOfCheckedTiles.length-1; ++y){
                    boolean foundFirstLand = false;
                    int lastX = 0;
                    int[] xPos = new int[2];
                    for (int x=0; x<mapOfCheckedTiles[0].length; ++x){
                        //First tile that must be checked
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
                        //First tile that must be checked
                        if (!mapOfCheckedTiles[y][x]){
                            lastY = y;
                        }
                        if (!foundFirstLand && !mapOfCheckedTiles[y][x]){
                            foundFirstLand = true;
                            yPos[0] = y-1;
                        }
                    }
                    //Set rightmost island point to true
                    yPos[1] = lastY+1;

                    edgeMap.put(x, yPos);
                }
            }
        }

    }
}
