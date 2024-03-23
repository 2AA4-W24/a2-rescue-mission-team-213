package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.StringReader;

public class Configuration {
    private String direction;
    private Integer batteryLevel;

    public Configuration(String s) {
        initializeConfiguration(s);
    }

    private void initializeConfiguration(String s){
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        this.direction = info.getString("heading");
        this.batteryLevel = info.getInt("budget");

    }
    
    public String getDirection() {
        return this.direction;
    }

    public Integer getBatteryLevel() {
        return this.batteryLevel;
    }
}
