package example;

import simkit.Entity;
import simkit.random.RandomVariate;

/**
 * Creates Entity instances and "passes" them on by scheduling an
 * Arrival(Entity) event upon each Arrival() event.
 * @author ahbuss
 */
public class EntityArrivalProcess extends ArrivalProcess {

    /**
     * Zero argument constructor - be sure to set interarrivalTimeGenerator
     * after instantiation is using this constructor
     */
    public EntityArrivalProcess() {
        super();
    }
    
    /**
     * Instantiate with given RandomVariate to generate interarrival times
     * @param interarrivalTimeGenerator Given interarrivalTimeGenerator
     */
    public EntityArrivalProcess(RandomVariate interarrivalTimeGenerator) {
        super(interarrivalTimeGenerator);
    }
    
    /**
     * Calls super.doArrival(); schedules Arrival(Entity) with delay of 0.0
     */
    @Override
    public void doArrival() {
        super.doArrival();
        waitDelay("Arrival", 0.0, new Entity());
    }

}
