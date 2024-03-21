package ca.mcmaster.se2aa4.island.team213;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        Logger logger = LogManager.getLogger();
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));
        this.direction = info.getString("heading");
        this.batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }
    
    public String getDirection() {
        return this.direction;
    }

    public Integer getBatteryLevel() {
        return this.batteryLevel;
    }
}
