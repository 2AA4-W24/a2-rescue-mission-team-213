package ca.mcmaster.se2aa4.island.team213;

import org.json.JSONObject;

public interface DecisionMakerInterface {

    // Technical Debt: Not every kind of decision may need the drone object
    JSONObject makeDecision(Drone drone);


}
