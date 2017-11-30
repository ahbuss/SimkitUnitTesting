package example;

import static java.lang.Double.NaN;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import simkit.Entity;
import simkit.Priority;
import simkit.SimEntityBase;
import simkit.random.RandomVariate;

/**
 * @version $Id: EntityServer.java 486 2016-04-01 00:01:18Z ahbuss $
 * @author ahbuss
 */
public class EntityServer extends SimEntityBase {

    private int totalNumberServers;

    private RandomVariate serviceTimeGenerator;

    protected Set<Integer> availableServers;

    protected SortedSet<Entity> queue;

    protected double timeInSystem;

    public EntityServer() {
        this.queue = new TreeSet<>();
        this.availableServers = new LinkedHashSet<>();
    }

    public EntityServer(int totalNumberServers,
            RandomVariate serviceTimeGenerator) {
        this();
        this.setTotalNumberServers(totalNumberServers);
        this.setServiceTimeGenerator(serviceTimeGenerator);
    }

    /**
     * Empty queue; repopulate availableServers
     */
    @Override
    public void reset() {
        super.reset();
        this.queue.clear();
        this.availableServers.clear();
        for (int i = 0; i < getTotalNumberServers(); ++i) {
            this.availableServers.add(i);
        }
        this.timeInSystem = NaN;
    }

    /**
     * Only firePropertyChanges
     */
    public void doRun() {
        firePropertyChange("availableServers", getAvailableServers());
        firePropertyChange("queue", getQueue());
    }

    /**
     * Stamp entity's time. Add to queue; if available servers, schedule
     * StartService with delay of 0.0.
     *
     * @param entity Arriving Entity
     */
    public void doArrival(Entity entity) {

        entity.stampTime();

        SortedSet<Entity> oldQueue = getQueue();
        queue.add(entity);
        firePropertyChange("queue", oldQueue, getQueue());

        if (availableServers.size() > 0) {
            waitDelay("StartService", 0.0, Priority.HIGH);
        }
    }

    /**
     * Remove first in queue and first available server. Schedule EndService
     * with delay for service time.
     */
    public void doStartService() {
        Set<Integer> oldAvailableServers = getAvailableServers();
        int server = availableServers.iterator().next();
        availableServers.remove(server);
        firePropertyChange("availableServers", oldAvailableServers,
                getAvailableServers());

        SortedSet<Entity> oldQueue = getQueue();
        Entity entity = queue.first();
        queue.remove(entity);
        firePropertyChange("queue", oldQueue, getQueue());

        firePropertyChange("delayInQueue", entity.getElapsedTime());

        waitDelay("EndService", serviceTimeGenerator.generate(),
                server, entity);
    }

    /**
     * Add server to availableServcers. If entities in queue, schedule
     * StartService with delay of 0.0.
     *
     * @param server The server completing service
     * @param entity The entity completing service
     */
    public void doEndService(int server, Entity entity) {
        timeInSystem = entity.getElapsedTime();
        firePropertyChange("timeInSystem", getTimeInSystem());

        Set<Integer> oldAvailableServers = getAvailableServers();
        availableServers.add(server);
        firePropertyChange("availableServers", oldAvailableServers,
                getAvailableServers());

        waitDelay("JobComplete", 0.0, entity);

        if (queue.size() > 0) {
            waitDelay("StartService", 0.0, Priority.HIGH);
        }
    }

    public void doJobComplete(Entity entity) {
    }

    public int getTotalNumberServers() {
        return totalNumberServers;
    }

    public void setTotalNumberServers(int totalNumberServers) {
        if (totalNumberServers <= 0) {
            throw new IllegalArgumentException("totalNumberServers must be > 0:"
                    + totalNumberServers);
        }
        this.totalNumberServers = totalNumberServers;
    }

    public RandomVariate getServiceTimeGenerator() {
        return serviceTimeGenerator;
    }

    public void setServiceTimeGenerator(RandomVariate serviceTimeGenerator) {
        this.serviceTimeGenerator = serviceTimeGenerator;
    }

    public Set<Integer> getAvailableServers() {
        return new LinkedHashSet<>(availableServers);
    }

    public SortedSet<Entity> getQueue() {
        return new TreeSet<>(queue);
    }

    /**
     * @return the timeInSystem
     */
    public double getTimeInSystem() {
        return timeInSystem;
    }
}
