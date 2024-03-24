package ca.mcmaster.se2aa4.island.team213;

import ca.mcmaster.se2aa4.island.team213.enums.Action;
import ca.mcmaster.se2aa4.island.team213.enums.Direction;
import ca.mcmaster.se2aa4.island.team213.enums.EchoResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class Drone {
    private Integer battery;
    private Direction direction;
    private EchoStatus echo = new EchoStatus();
    private ScanStatus scanInfo;
    private Action previousDecision;
    private String creekID = "";

    public Drone(String direction, Integer battery) {
        this.direction = Direction.valueOf(direction);
        this.battery = battery;
    }

    public void setCreekID(String creekID) {
        this.creekID = creekID;
    }

    public void updateStatus(JSONObject response) {
        this.battery -= response.getInt("cost");
        JSONObject extraInfo = response.getJSONObject("extras");

        if(previousDecision.equals(Action.ECHO_RIGHT)) {
            this.echo.echoRight = EchoResult.valueOf(extraInfo.getString("found"));
            this.echo.rangeRight = extraInfo.getInt("range");
        } 
        else if(previousDecision.equals(Action.ECHO_LEFT)) {
            this.echo.echoLeft = EchoResult.valueOf(extraInfo.getString("found"));
            this.echo.rangeLeft = extraInfo.getInt("range");
        } 
        else if(previousDecision.equals(Action.ECHO_AHEAD)) {
            this.echo.echoAhead = EchoResult.valueOf(extraInfo.getString("found"));
            this.echo.rangeAhead = extraInfo.getInt("range");
        }
        else if(previousDecision.equals(Action.SCAN)) {
            this.scanInfo = new ScanStatus(extraInfo);
        } else if (previousDecision.equals(Action.FLY)){
            if (!Objects.isNull(this.echo.rangeAhead)){
                this.echo.rangeAhead -= 1;
            }
        }
    }
    
    public void parseDecision(JSONObject decision) {
        if(decision.getString("action").equals("heading")) {
            JSONObject parameter = decision.getJSONObject("parameters");
            this.echo = new EchoStatus();
            if(this.direction.rightTurn().toString().equals(parameter.get("direction").toString())) {
                this.direction = this.direction.rightTurn();
                this.previousDecision = Action.TURN_RIGHT;
            }
            else if(this.direction.leftTurn().toString().equals(parameter.get("direction").toString())) {
                this.direction = this.direction.leftTurn();
                this.previousDecision = Action.TURN_LEFT;
            }
        }
        else if(decision.getString("action").equals("echo")) {
            JSONObject parameter = decision.getJSONObject("parameters");
            if(this.direction.toString().equals(parameter.getString("direction"))) {
                this.previousDecision = Action.ECHO_AHEAD;
            }
            else if(this.direction.rightTurn().toString().equals(parameter.get("direction"))) {
                this.previousDecision = Action.ECHO_RIGHT;
            }
            else if(this.direction.leftTurn().toString().equals(parameter.get("direction"))) {
                this.previousDecision = Action.ECHO_LEFT;
            }
        }
        else if(decision.getString("action").equals("scan")) {
            this.previousDecision = Action.SCAN;
        }
        else if(decision.getString("action").equals("fly")) {
            this.previousDecision = Action.FLY;
        } 
        else if (decision.getString("action").equals("stop")) {
            this.previousDecision = Action.STOP;
        }
    }

    public Integer getBattery() {
        return this.battery;
    }

    public Direction getDirection() {
        return direction;
    }

    public EchoResult getEchoRight() {
        return echo.echoRight;
    }

    public EchoResult getEchoLeft() {
        return echo.echoLeft;
    }

    public EchoResult getEchoAhead() {
        return echo.echoAhead;
    }

    public Integer getRangeRight() {
        return echo.rangeRight;
    }

    public Integer getRangeLeft() {
        return echo.rangeLeft;
    }

    public Integer getRangeAhead() {
        return echo.rangeAhead;
    }

    public JSONArray getScanInfoCreeks() {
        return this.scanInfo.scanCreeks;
    }

    public JSONArray getScanInfoSites() {
        return this.scanInfo.scanSites;
    }

    public JSONArray getScanInfoBiomes() {
        return this.scanInfo.scanBiomes;
    }

    public Action getPreviousDecision() {
        return this.previousDecision;
    }

    public String getCreekID()
    {
        if (this.creekID.isEmpty()){
            return "no creek found";
        }
        return this.creekID;
    }

}
