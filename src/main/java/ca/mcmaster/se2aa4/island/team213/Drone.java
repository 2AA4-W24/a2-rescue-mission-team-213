package ca.mcmaster.se2aa4.island.team213;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Drone {
    private Integer battery;
    private String direction;
    private EchoStatus echo = new EchoStatus();
    private String droneStatus;
    private Direction echoing;

    private final Logger logger = LogManager.getLogger();
    
    public Drone(String direction, Integer battery){
        this.direction = direction;
        this.battery = battery;
    }

    public void updateStatus(JSONObject response){

        //        logger.info("** Response received:\n"+response.toString(2));
        //        logger.info("The cost of the action was {}", cost);
        battery -= response.getInt("cost");
        //        logger.info("The status of the drone is {}", status);
        droneStatus = response.getString("status");


        // TECHNICAL DEBT: currently just assumes content of extra is echo response object, does not consider for scan
        //        logger.info("Additional information received: {}", extraInfo);
        JSONObject extraInfo = response.getJSONObject("extras");
        String result = extraInfo.getString("found");
        Integer range = extraInfo.getInt("range");

        switch (echoing) {
            case N -> {
                echo.north = EchoResult.valueOf(result);
                echo.rangeNorth = range;
            }
            case E -> {
                echo.east = EchoResult.valueOf(result);
                echo.rangeEast = range;

            }
            case S -> {
                echo.south = EchoResult.valueOf(result);
                echo.rangeSouth = range;
            }
            case W -> {
                echo.west = EchoResult.valueOf(result);
                echo.rangeWest = range;
            }
        }
        logger.info(echo.east.toString());
        logger.info(echo.rangeEast);

        // set echoing to null after
        echoing = null;

    }

    public EchoResult getEchoNorth(){
        return echo.north;
    }
    public EchoResult getEchoEast(){
        return echo.east;
    }
    public EchoResult getEchoSouth(){
        return echo.south;
    }
    public EchoResult getEchoWest(){
        return echo.west;
    }

    public void setEcho(Direction echoing) {
        this.echoing = echoing;
    }
}
