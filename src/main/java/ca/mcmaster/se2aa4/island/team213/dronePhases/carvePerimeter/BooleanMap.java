package ca.mcmaster.se2aa4.island.team213.dronePhases.carvePerimeter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import ca.mcmaster.se2aa4.island.team213.enums.EchoResult;

public class BooleanMap {
    boolean[][] map;
    int islandX, islandY;

    private final Logger logger = LogManager.getLogger();

    public BooleanMap(int x, int y) {
        this.islandX = x;
        this.islandY = y;
        this.map = new boolean[y][x];
    }

    public boolean[][] getMap() {
        return this.map;
    }
    public int getIslandX() {
        return this.islandX;
    }

    public int getIslandY() {
        return this.islandY;
    }

    public void updateMapFromEchoRight(Direction droneDirection, Integer echoRange, EchoResult echoRight, DronePosition dronePosition) {
        int loopBound;
        if(droneDirection.equals(Direction.N)) {
            loopBound = echoRight.equals(EchoResult.OUT_OF_RANGE) ? this.islandX : echoRange;
            for(int i = dronePosition.getDroneX(); i < loopBound; i++) {
                this.map[dronePosition.getDroneY()][i] = true;
            }
        }
        else if(droneDirection.equals(Direction.E)) {
            loopBound = echoRight.equals(EchoResult.OUT_OF_RANGE) ? this.islandY : echoRange;
            for(int i = dronePosition.getDroneY(); i < loopBound; i++) {
                this.map[i][dronePosition.getDroneX()] = true;
            }
        }
        else if(droneDirection.equals(Direction.S)) {
            logger.info("hello");
            loopBound = echoRight.equals(EchoResult.OUT_OF_RANGE) ? 0 : dronePosition.getDroneX() - echoRange + 1;
            for(int i = dronePosition.getDroneX(); i >= loopBound; i--) {
                this.map[dronePosition.getDroneY()][i] = true;
            }
        }
        else if(droneDirection.equals(Direction.W)) {
            loopBound = echoRight.equals(EchoResult.OUT_OF_RANGE) ? 0 : dronePosition.getDroneY() - echoRange + 1;
            for(int i = dronePosition.getDroneY(); i >= loopBound; i--) {
                this.map[i][dronePosition.getDroneX()] = true;
            }
        }
    }

    public void determineImpossibleTiles(int siteX, int siteY, int creekX, int creekY) {
        double distanceX = Math.abs(siteX - creekX);
        double distanceY = Math.abs(siteY - creekY);
        int distance = (int) Math.ceil(Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2)));
        
        for(int i = 0; i < this.islandY; i++) {
            for(int j = 0; j < this.islandX; j++) {
                if(i < siteX - distance || i > siteX + distance || j < siteY - distance || j > siteY + distance) {
                    this.map[i][j] = true;
                }
            }
        }
    }

}
