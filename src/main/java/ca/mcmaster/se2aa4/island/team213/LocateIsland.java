package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

public class LocateIsland implements DecisionMakerInterface {
    public boolean landFound;

    private Queue<JSONObject> taskQueue = new LinkedList<>();

    @Override
    public String makeDecision(Drone drone) {
        JSONObject decision = new JSONObject();
        // test
        //        if (drone.getEchoNorth() ==  EchoResult.OUT_OF_RANGE && )
        return null;
    }



}
