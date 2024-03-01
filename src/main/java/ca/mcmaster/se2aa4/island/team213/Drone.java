package ca.mcmaster.se2aa4.island.team213;

import eu.ace_design.island.game.actions.Echo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Drone {
    private Integer battery;
    private Direction directionHeading;
    private EchoStatus echo = new EchoStatus();
    private String droneStatus;


    private Direction echoing;

    private final Logger logger = LogManager.getLogger();
    
    public Drone(String direction, Integer battery){
        this.directionHeading = Direction.valueOf(direction);
        this.battery = battery;
    }

    public void updateStatus(JSONObject response){
        logger.info("updating status...");
        //        logger.info("** Response received:\n"+response.toString(2));

        battery -= response.getInt("cost");
//                logger.info("New battery: {}", battery);
        droneStatus = response.getString("status");
//                logger.info("The status of the drone is {}", droneStatus);


        // TECHNICAL DEBT: currently just assumes content of extra is echo response object, does not consider for scan
        //        logger.info("Additional information received: {}", extraInfo);

        JSONObject extraInfo = response.getJSONObject("extras");
        if (!extraInfo.isEmpty()){
            String result = extraInfo.getString("found");
            Integer range = extraInfo.getInt("range");

            if (echoing != null){
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
//            logger.info(echo.east.toString());
//            logger.info(echo.rangeEast);

                // set echoing to null after
                echoing = null;
            }
        }




    }

    public EchoResult getEchoNorth(){
        return echo.north;
    }
    public EchoResult getEchoEast(){return echo.east;}
    public EchoResult getEchoSouth(){
        return echo.south;
    }
    public EchoResult getEchoWest(){
        return echo.west;
    }

    public Integer getRangeHeading(){
        switch (directionHeading){
            case N ->{
                return echo.rangeNorth;
            }
            case E ->{
                return echo.rangeEast;
            }
            case S ->{
                return echo.rangeSouth;
            }
            case W ->{
                return echo.rangeWest;
            }
        }

        // Should not arrive at this case
        return null;
    }
    public void setDirectionHeading(Direction newDirection){
        this.directionHeading = newDirection;
    }
    public void subtractRangeHeading(){
        switch (directionHeading){
            case N ->{
                echo.rangeNorth--;
            }
            case E ->{
                echo.rangeEast--;
            }
            case S ->{
                echo.rangeSouth--;
            }
            case W ->{
                echo.rangeWest--;
            }
        }

    }

    public EchoResult getEchoHeading(){
        switch (directionHeading){
            case N ->{
                return echo.north;
            }
            case E ->{
                return echo.east;
            }
            case S ->{
                return echo.south;
            }
            case W ->{
                return echo.west;
            }
        }

        // Should not arrive at this case
        return null;
    }

    public void setEcho(Direction echoing) {
        this.echoing = echoing;
    }
}
