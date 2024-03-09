package ca.mcmaster.se2aa4.island.team213;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Drone {
    private Integer battery;
    private Direction direction;
    private EchoStatus echo = new EchoStatus();
    private ScanStatus scanInfo;
    private Direction echoRequested;
    private String previousDecision;
    private String siteID;

    private final Logger logger = LogManager.getLogger();

    public Drone(String direction, Integer battery){
        stringToDirection(direction);
        this.battery = battery;
        this.previousDecision = "";
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
        logger.info("** Response received:\n"+response.toString(2));

        battery -= response.getInt("cost");

        // TECHNICAL DEBT: currently just assumes content of extra is echo response object, does not consider for scan
        //        logger.info("Additional information received: {}", extraInfo);

        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
        if (!extraInfo.isEmpty() && echoRequested != null){
            String result = extraInfo.getString("found");
            Integer range = extraInfo.getInt("range");


            if (Objects.equals(echoRequested, direction.rightTurn())){
                echo.echoRight = EchoResult.valueOf(result);
                echo.rangeRight = range;
            }
            else if (Objects.equals(echoRequested, direction.leftTurn())){
                echo.echoLeft = EchoResult.valueOf(result);
                echo.rangeLeft = range;
            }
            else if (Objects.equals(echoRequested, direction)){
                echo.echoAhead = EchoResult.valueOf(result);
                echo.rangeAhead = range;
            }


            // set echoing to null after
            echoRequested = null;

        }


        if(previousDecision.equals("echoRight")) {
            logger.info("STORING ECHO RIGHT INFO: " + extraInfo.getString("found"));
            this.echo.echoRight = EchoResult.valueOf(extraInfo.getString("found"));
            this.echo.rangeRight = extraInfo.getInt("range");
        } 
        else if(previousDecision.equals("echoLeft")) {
            logger.info("STORING ECHO LEFT INFO: " + extraInfo.getString("found"));
            this.echo.echoLeft = EchoResult.valueOf(extraInfo.getString("found"));
            this.echo.rangeLeft = extraInfo.getInt("range");
        } 
        else if(previousDecision.equals("echoAhead")) {
            logger.info("STORING ECHO AHEAD INFO: " + extraInfo.getString("found"));
            this.echo.echoAhead = EchoResult.valueOf(extraInfo.getString("found"));
            this.echo.rangeAhead = extraInfo.getInt("range");
        }
        else if(previousDecision.equals("scan")) {
            logger.info("STORING SCAN INFO:");
            this.scanInfo = new ScanStatus(extraInfo);
        }

    }
    public String getSiteID(){
        return this.siteID;
    }

    public EchoResult getEchoAhead() {
        return echo.echoAhead;
    }

    public EchoResult getEchoRight() {
        return echo.echoRight;
    }

    public EchoResult getEchoLeft() {
        return echo.echoLeft;
    }

    public Integer getRangeHeading(){
        return echo.rangeAhead;
    }

    public void subtractRangeHeading(){
        echo.rangeAhead--;

    }

    // Section below added by Gary, includes some temporary accessor methods to make other classes work

    // determines what the decision being sent to Explorer is
    // also updates direction or position if the decision was a "fly" or "heading" action
    public void parseDecision(JSONObject decision) {
        if(decision.getString("action").equals("heading")) {
            JSONObject parameter = decision.getJSONObject("parameters");
            logger.info("DRONE RECEIVED COMMAND FOR HEADING");
            if(this.direction.rightTurn().toString().equals(parameter.get("direction").toString())) {
                this.direction = this.direction.rightTurn();
                this.previousDecision = "turnRight";
            }
            else if(this.direction.leftTurn().toString().equals(parameter.get("direction").toString())) {
                this.direction = this.direction.leftTurn();
                this.previousDecision = "turnLeft";
            }
        }
        else if(decision.getString("action").equals("echo")) {
            JSONObject parameter = decision.getJSONObject("parameters");

            if(this.direction.equals(parameter.get("direction"))) {
                logger.info("DRONE RECEIVED COMMAND FOR ECHO AHEAD");
                echoRequested = direction;
                this.previousDecision = "echoAhead";
            }
            else if(this.direction.rightTurn().equals(parameter.get("direction"))) {
                logger.info("DRONE RECEIVED COMMAND FOR ECHO RIGHT");
                echoRequested = direction.rightTurn();
                this.previousDecision = "echoRight";
            }
            else if(this.direction.leftTurn().equals(parameter.get("direction"))) {
                logger.info("DRONE RECEIVED COMMAND FOR ECHO LEFT");
                echoRequested = direction.leftTurn();
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
        return direction;
    }

    public JSONArray getScanInfoBiome() {
        return this.scanInfo.scanBiomes;
    }

    public JSONArray getScanInfoCreeks() {
        return this.scanInfo.scanCreeks;
    }

    public JSONArray getScanInfoSites() {
        return this.scanInfo.scanSites;
    }

    public String getPreviousDecision() {
        return this.previousDecision;
    }

}
