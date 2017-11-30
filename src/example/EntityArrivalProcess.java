package example;

import simkit.Entity;
import simkit.examples.ArrivalProcess;
import simkit.random.RandomVariate;

/**
 *
 * @author ahbuss
 */
public class EntityArrivalProcess extends ArrivalProcess {

    public EntityArrivalProcess(RandomVariate iat) {
        super(iat);
    }
    
    @Override
    public void doArrival() {
        super.doArrival();
        waitDelay("Arrival", 0.0, new Entity());
    }

}
