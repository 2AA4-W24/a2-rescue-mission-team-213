package ca.mcmaster.se2aa4.island.team213.dronePhases.areaScan;

import ca.mcmaster.se2aa4.island.team213.enums.Direction;

public class EdgeMap {
    boolean[][] edgeMap;
    boolean[][] mapOfCheckedTiles;

    Direction droneDirection;

    public EdgeMap(boolean[][] mapOfCheckedTiles, Direction droneDirection, int islandX, int islandY){
        this.mapOfCheckedTiles = mapOfCheckedTiles;
        this.droneDirection = droneDirection;

        this.edgeMap = new boolean[islandY][islandX];


        switch (droneDirection){
            case E, W -> {

                for (int y=0; y<mapOfCheckedTiles.length; ++y){
                    boolean foundFirstLand = false;
                    int lastX = 0;
                    for (int x=0; x<mapOfCheckedTiles[0].length; ++x){
                        //First tile that must be checked
                        if (!mapOfCheckedTiles[y][x]){
                            lastX = x;
                        }
                        if (!foundFirstLand && !mapOfCheckedTiles[y][x]){
                            foundFirstLand = true;
                            //Set leftmost island point to true
                            edgeMap[y][x] = true;
                        }
                    }
                    //Set rightmost island point to true
                    edgeMap[y][lastX] = true;
                }
            }
            case N, S -> {
                for (int x=0; x<mapOfCheckedTiles[0].length; ++x){
                    boolean foundFirstLand = false;
                    int lastY = 0;
                    for (int y=0; y<mapOfCheckedTiles.length; ++y){
                        //First tile that must be checked
                        if (!mapOfCheckedTiles[y][x]){
                            lastY = y;
                        }
                        if (!foundFirstLand && !mapOfCheckedTiles[y][x]){
                            foundFirstLand = true;
                            //Set leftmost island point to true
                            edgeMap[y][x] = true;
                        }
                    }
                    //Set rightmost island point to true
                    edgeMap[lastY][x] = true;
                }
            }
        }
    }
    public boolean[][] getEdgeMap(){
        return this.edgeMap;
    }


}
