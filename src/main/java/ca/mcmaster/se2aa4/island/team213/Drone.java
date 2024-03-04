package ca.mcmaster.se2aa4.island.team213;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.json.JsonConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Drone {
    private Integer battery;
    private Direction direction;
    private EchoStatus echo = new EchoStatus();
    private Direction echoing;
    private String droneStatus;
    private JSONArray scanInfo;
    private String previousDecision;

    private final Logger logger = LogManager.getLogger();
    private Direction directionHeading = Direction.E;

    public Drone(String direction, Integer battery){
        stringToDirection(direction);
        this.battery = battery;
    }

    private void stringToDirection(String direction) {
        switch(direction) {
            case ("N") -> this.direction = Direction.N;
            case ("E") -> this.direction = Direction.E;
            case ("S") -> this.direction = Direction.S;
            case ("W") -> this.direction = Direction.W;
        }
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
        if (!extraInfo.isEmpty() && echoing != null){
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
//            logger.info(echo.east.toString());
//            logger.info(echo.rangeEast);

            // set echoing to null after
            echoing = null;

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
        switch (direction){
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
        this.direction = newDirection;
    }
    public void subtractRangeHeading(){
        switch (direction){
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
        switch (direction){
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

    public void setEcho(Direction direction){
        this.echoing = direction;
    }

    // Section below added by Gary, includes some temporary accessor methods to make other classes work

    private EchoResult echoRight, echoLeft;

    // determines what the decision being sent to Explorer is
    // also updates direction or position if the decision was a "fly" or "heading" action
    public void parseDecision(JSONObject decision) {
        if(decision.getString("action").equals("heading")) {
            JSONObject parameter = decision.getJSONObject("parameters");

            if(this.direction.rightTurn().equals(parameter.get("direction"))) {
                this.direction = this.direction.rightTurn();
                this.previousDecision = "turnRight";
            }
            else if(this.direction.leftTurn().equals(parameter.get("direction"))) {
                this.direction = this.direction.leftTurn();
                this.previousDecision = "turnLeft";
            }
        }
        else if(decision.getString("action").equals("echo")) {
            JSONObject parameter = decision.getJSONObject("parameters");

            if(this.direction.rightTurn().equals(parameter.get("direction"))) {
                this.previousDecision = "echoRight";
            }
            else if(this.direction.leftTurn().equals(parameter.get("direction"))) {
                this.previousDecision = "echoLeft";
            }
        }
        else if(decision.getString("action").equals("scan")) {
            this.previousDecision = "scan";
        }
        else if(decision.getString("action").equals("fly")) {
            this.previousDecision = "fly";
        }
    }

    public Direction getDirection() {
        return this.direction;
    }

    public JSONArray getScanInfo() {
        return this.scanInfo;
    }

    public String getPreviousDecision() {
        return this.previousDecision;
    }

    public EchoResult getEchoRight() {
        return this.echoRight;
    }

    public EchoResult getEchoLeft() {
        return this.echoLeft;
    }


}
