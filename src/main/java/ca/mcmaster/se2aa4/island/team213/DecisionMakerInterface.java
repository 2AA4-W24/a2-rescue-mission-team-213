package ca.mcmaster.se2aa4.island.team213;

public interface DecisionMakerInterface {

    // Technical Debt: Not every kind of decision may need the drone object
    String makeDecision(Drone drone);


}
