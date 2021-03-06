package example;

import simkit.SimEntityBase;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * <P>
 * Simplest non-trivial Event Graph. A basic arrival process that can have any
 * interarrival probability distribution.
 *
 * @author Arnold Buss
 *
 */
public class ArrivalProcess extends SimEntityBase {

    /**
     * The interarrival distribution (parameter)
     *
     */
    private RandomVariate interarrivalTimeGenerator;
    /**
     * The number of arrivals (state variable)
     *
     */
    protected int numberArrivals;

    /**
     * Zero-argument constructor - after instantiation, much set the
     * interarrivalTimeGenerator
     */
    public ArrivalProcess() {
    }

    /**
     * Construct an <code>ArrivalProcess</code> instance with given interarrival
     * distribution. The distribution must generate values that are &ge; 0. This
     * is the preferred way to construct and ArrivalProcess instance.
     *
     * @param interarrivalTimeGenerator The interarrival distribution
     * RandomVariate
     *
     */
    public ArrivalProcess(RandomVariate interarrivalTimeGenerator) {
        this.setInterarrivalTimeGenerator(interarrivalTimeGenerator);
    }

    /**
     * Initializes the number of arrivals to 0
     */
    @Override
    public void reset() {
        super.reset();
        numberArrivals = 0;
    }

    /**
     * Schedule the first arrival with delay generated by
     * interarrivalTimeGenerator. (Event Method)
     */
    public void doRun() {
        firePropertyChange("numberArrivals", getNumberArrivals());
        waitDelay("Arrival", interarrivalTimeGenerator);
    }

    /**
     * Arrival event: Increments number of arrivals and schedules next arrival.
     * (Event Method)
     */
    public void doArrival() {
        int oldNumberArrivals = getNumberArrivals();
        numberArrivals += 1;
        firePropertyChange("numberArrivals", getNumberArrivals());

        waitDelay("Arrival", interarrivalTimeGenerator);
    }

    /**
     * @return number of arrivals (state variable)
     */
    public int getNumberArrivals() {
        return numberArrivals;
    }

    /**
     * @param interarrivalTimeGenerator Generates interarrival times
     */
    public void setInterarrivalTimeGenerator(RandomVariate interarrivalTimeGenerator) {
        this.interarrivalTimeGenerator = RandomVariateFactory.getInstance(interarrivalTimeGenerator);
    }

    /**
     * @return RandomVariate that generates interarrival times
     */
    public RandomVariate getInterarrivalTimeGenerator() {
        return RandomVariateFactory.getInstance(interarrivalTimeGenerator);
    }

}
