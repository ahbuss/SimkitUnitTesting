package example;

import java.beans.PropertyChangeEvent;
import java.util.Set;
import java.util.SortedSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import simkit.Entity;
import simkit.SimEvent;
import simkit.random.RandomVariateFactory;
import simkit.util.PropertyChangeListenerHelper;
import simkit.util.UnitTestEventList;
import static util.Utilities.EPSILON;
import static util.Utilities.setupEventList;

/**
 *
 * @author ahbuss
 */
public class EntityServerTest {

    private static UnitTestEventList eventList;

    private static PropertyChangeListenerHelper queuePropertyChangeListener;
    private static PropertyChangeListenerHelper availableServersPropertyChangeListener;
    private static PropertyChangeListenerHelper timeInSystemPropertyChangeListener;
    private static EntityServer instance;

    public EntityServerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
//        This creates the special EventList for unit testing and 
//        makes it the default for Schedule.
        eventList = setupEventList();
//        Instantiate EntityServer instance that will be used by all tests
        instance = new EntityServer();
        instance.setTotalNumberServers(5);
        instance.setServiceTimeGenerator(RandomVariateFactory.getInstance("Constant", 1.2));

//        Each state variable will typically have its own PropertyChangeListener
//        The PropertyChangeListenerHelper class simply saves the last
//        PropertyChangeEvent it "heard" and gives access to that for testing
//        that the correct PropertyChangeEvent was fired. In cases where the
//        corresponding state variable was not changed, the corresponding
//        PropertyChangeListenerHelper instance should return null
        queuePropertyChangeListener = new PropertyChangeListenerHelper();
        availableServersPropertyChangeListener = new PropertyChangeListenerHelper();
        timeInSystemPropertyChangeListener = new PropertyChangeListenerHelper();

//        Typically have each PropertyChangeListenerHelper instance only listen
//        for "its" state's PropertyChangeEvents
        instance.addPropertyChangeListener("queue", queuePropertyChangeListener);
        instance.addPropertyChangeListener("availableServers", availableServersPropertyChangeListener);
        instance.addPropertyChangeListener("timeInSystem", timeInSystemPropertyChangeListener);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
//         This restores eventList to its "pristine" condition
        eventList.coldReset();
//        This resets the instance's state variables to their respective 
//        initial values
        instance.reset();

//        Each PropertyChangeListenerHelper instance needs to be explicitly
//        reset.
        queuePropertyChangeListener.reset();
        availableServersPropertyChangeListener.reset();
        timeInSystemPropertyChangeListener.reset();
    }

    @After
    public void tearDown() {
    }

    /**
     * First add a "server" to availableServers and an Entity to queue. After
     * calling reset(), the queue should be empty and availableServers restored
     * to containing 0...totalNumberServers - 1
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        instance.availableServers.add(20);
        instance.queue.add(new Entity());
        instance.reset();
        assertTrue(instance.queue.isEmpty());
        assertEquals(instance.getTotalNumberServers(), instance.availableServers.size());
        for (int i = 0; i < instance.getTotalNumberServers(); ++i) {
            assertTrue(instance.availableServers.contains(i));
        }
    }

    /**
     * Verifies that the property change events are being fired and have the
     * correct values.
     */
    @Test
    public void testDoRun() {
        System.out.println("doRun");
        instance.doRun();
        PropertyChangeEvent queueEvent = queuePropertyChangeListener.getLastEvent();
        assertNotNull(queueEvent);
        assertEquals("queue", queueEvent.getPropertyName());
        assertNull(queueEvent.getOldValue());
        assertEquals(queueEvent.getNewValue(), instance.queue);

        PropertyChangeEvent availableServersEvent = availableServersPropertyChangeListener.getLastEvent();
        assertNotNull(availableServersEvent);
        assertEquals("availableServers", availableServersEvent.getPropertyName());
        assertNull(availableServersEvent.getOldValue());
        assertEquals(availableServersEvent.getNewValue(), instance.availableServers);

        PropertyChangeEvent timeInSystemEvent = timeInSystemPropertyChangeListener.getLastEvent();
        assertNotNull(timeInSystemEvent);
        assertNull(timeInSystemEvent.getOldValue());
        Object value = timeInSystemEvent.getNewValue();
        assertTrue(Double.isNaN((Double) timeInSystemEvent.getNewValue()));

    }

    /**
     * Test of doArrival method, of class EntityServer.
     */
    @Test
    public void testDoArrival() {
        System.out.println("doArrival");

        double simTime = 12345.6;
        eventList.setSimTime(simTime);

//        The Entity will have time stamp of the above simTime
        Entity entity = new Entity();
        instance.doArrival(entity);

//        Is it in the queue?
        assertEquals(1, instance.queue.size());
        assertTrue(instance.queue.contains(entity));

//        Did the queue fire a PropertyChangeEvent with the correct new value?
        PropertyChangeEvent queueEvent = queuePropertyChangeListener.getLastEvent();
        assertNotNull(queueEvent);
        assertEquals(instance.queue, queueEvent.getNewValue());

//        availableServers whould not have changed
        PropertyChangeEvent availableServersEvent = availableServersPropertyChangeListener.getLastEvent();
        assertNull(availableServersEvent);

//        timeInSystem should not have changed
        PropertyChangeEvent timeInSystemEvent = timeInSystemPropertyChangeListener.getLastEvent();
        assertNull(timeInSystemEvent);

//        The StartService event (only) should be scheduled at the current simTime
        assertEquals(1, eventList.getScheduledEvents().size());
        SimEvent scheduledEvent = eventList.getScheduledEvent("StartService");
        assertNotNull(scheduledEvent);
        assertEquals(0, scheduledEvent.getParameters().length);
        assertEquals(simTime, scheduledEvent.getScheduledTime(), EPSILON);

//        Now check that the StartService event is not scheduled when there
//        are no available servers
        eventList.coldReset();
        instance.queue.clear();
        instance.availableServers.clear();
        instance.doArrival(entity);
        assertEquals(1, instance.queue.size());
        assertTrue(instance.queue.contains(entity));

        assertTrue(eventList.getScheduledEvents().isEmpty());
    }

    /**
     * Test of doStartService method, of class EntityServer.
     */
    @Test
    public void testDoStartService() {
        System.out.println("doStartService");

//        Set an arbitrary simTime
        double simTime = 54321.0;
        eventList.setSimTime(simTime);

//        Set an arbitrary service time, using the Constant RandomVariate
        double serviceTime = 13.5;
        instance.setServiceTimeGenerator(RandomVariateFactory.getInstance("Constant", serviceTime));

//        Set up pre conditions for StartService. There must be at least one
//        Entity in the queue and at least one available server
        instance.queue.clear();
        instance.availableServers.clear();
        instance.availableServers.add(0);
        instance.availableServers.add(3);
        Entity entity = new Entity();
        instance.queue.add(entity);
        instance.doStartService();

//        Verify that the queue is empty and server 0 has been removed
//        from availableServers
        assertTrue(instance.queue.isEmpty());
        assertTrue(instance.availableServers.contains(3));
        assertFalse(instance.availableServers.contains(0));

//        The PropertyChangeEvent for queue 
        PropertyChangeEvent queueEvent = queuePropertyChangeListener.getLastEvent();
        assertNotNull(queueEvent);
        assertEquals("queue", queueEvent.getPropertyName());
        assertNotNull(queueEvent.getOldValue());
        assertEquals(queueEvent.getNewValue(), instance.queue);

//        The PropertyChangeEvent for availableServers
        PropertyChangeEvent availableServersEvent = availableServersPropertyChangeListener.getLastEvent();
        assertNotNull(availableServersEvent);
        assertEquals("availableServers", availableServersEvent.getPropertyName());
        assertNotNull(availableServersEvent.getOldValue());
        assertEquals(availableServersEvent.getNewValue(), instance.availableServers);

//       timeInSystem shoulc not have changed
        PropertyChangeEvent timeInSystemEvent = timeInSystemPropertyChangeListener.getLastEvent();
        assertNull(timeInSystemEvent);

//        Exactly 1 event, EndService, has been scheduled at simTime + serviceTime
        assertEquals(1, eventList.getScheduledEvents().size());
        SimEvent scheduledEvent = eventList.getScheduledEvent("EndService");
        assertNotNull(scheduledEvent);
        assertEquals(simTime + serviceTime, scheduledEvent.getScheduledTime(), EPSILON);
//        It has two parameters...
        assertEquals(2, scheduledEvent.getParameters().length);

//        The first being the server number, 0 ...
        assertEquals(0, scheduledEvent.getParameters()[0]);
//        ... and the second being the Entity that started service
        assertEquals(entity, scheduledEvent.getParameters()[1]);

    }

    /**
     * Test of doEndService method, of class EntityServer.
     */
    @Test
    public void testDoEndService() {
        System.out.println("doEndService");

//        Instantiate the Entity in service; its timeStamp should be the given arrivalTime
        double arrivalTime = 2468.9;
        eventList.setSimTime(arrivalTime);
        Entity completingEntity = new Entity();

//        Now advance simTime a bit
        double simTime = 35791.3;
        eventList.setSimTime(simTime);

//        This is the Entity in the queue
        Entity entityInQueue = new Entity();
        instance.queue.add(entityInQueue);
//        There are no available servers
        instance.availableServers.clear();

//        Server 3 completes service on completingEntity
        int server = 3;
        instance.doEndService(server, completingEntity);

//        IS the computed timeInSystem correct?
        double expectedTimeInSystem = simTime - arrivalTime;
        assertEquals(expectedTimeInSystem, instance.getTimeInSystem(), EPSILON);

//        Is server 3 now in availableServers
        assertEquals(1, instance.availableServers.size());
        assertTrue(instance.availableServers.contains(server));

//        queue hasn't changed
        PropertyChangeEvent queueEvent = queuePropertyChangeListener.getLastEvent();
        assertNull(queueEvent);

//        Has the PropertyChangeEvent for availableServers been fired?
        PropertyChangeEvent availableServersEvent = availableServersPropertyChangeListener.getLastEvent();
        assertNotNull(availableServersEvent);
        assertEquals("availableServers", availableServersEvent.getPropertyName());
        assertNotNull(availableServersEvent.getOldValue());
        assertEquals(availableServersEvent.getNewValue(), instance.availableServers);

//        Has the timeInSystem PropertyChangeEvent been fired?
        PropertyChangeEvent timeInSystemEvent = timeInSystemPropertyChangeListener.getLastEvent();
        assertNotNull(timeInSystemEvent);
        assertNull(timeInSystemEvent.getOldValue());
        assertEquals(expectedTimeInSystem, (Double) timeInSystemEvent.getNewValue(), EPSILON);

//        There should be 2 scheduled events: StartService and JobComplete
        assertEquals(2, eventList.getScheduledEvents().size());
        SimEvent scheduledEvent = eventList.getScheduledEvent("JobComplete", new Object[]{completingEntity});
        assertNotNull(scheduledEvent);
        assertEquals(simTime, scheduledEvent.getScheduledTime(), EPSILON);

        scheduledEvent = eventList.getScheduledEvent("StartService");
        assertNotNull(scheduledEvent);
        assertEquals(simTime, scheduledEvent.getScheduledTime(), EPSILON);

//        Now test EndService when the queue is empty
        instance.queue.clear();
        eventList.coldReset();
        eventList.setSimTime(simTime);

        server = 4;
        instance.doEndService(server, completingEntity);

        assertEquals(1, eventList.getScheduledEvents().size());
        scheduledEvent = eventList.getScheduledEvent("JobComplete", new Object[]{completingEntity});
        assertNotNull(scheduledEvent);
        assertEquals(simTime, scheduledEvent.getScheduledTime(), EPSILON);

    }

    /**
     * Test of setTotalNumberServers method, of class EntityServer. Checks that
     * IllegalArgumentException thrown when passing 0 or a negative number.
     * Normally setters are not tested if they are a simple pass through. In
     * this case, however, we need to verify that bad values (0 or less) will
     * not be let through.
     */
    @Test
    public void testSetTotalNumberServers() {
        System.out.println("setTotalNumberServers");
        int totalNumberServers = 0;
        try {
            instance.setTotalNumberServers(totalNumberServers);
            fail("setTotalNumberServers(0) should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        totalNumberServers = -1;
        try {
            instance.setTotalNumberServers(totalNumberServers);
            fail("setTotalNumberServers(-1) should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }

        totalNumberServers = 1;
        instance.setTotalNumberServers(totalNumberServers);
        assertEquals(totalNumberServers, instance.getTotalNumberServers());
    }

    /**
     * Tests that a copy of availableServers is returned, not availableServers
     * itself. Normally getters are not tested if they are a straightforward
     * pass through, but since this is not one, it needs to be tested.
     */
    @Test
    public void testGetAvailableServers() {
        System.out.println("getAvailableServers");
        Set<Integer> result = instance.getAvailableServers();
        assertNotSame(instance.availableServers, result);
    }

    /**
     * Verifies that a copy of queue is returned, not the queue itself.
     */
    @Test
    public void testGetQueue() {
        System.out.println("getQueue");
        SortedSet<Entity> result = instance.getQueue();
        assertNotSame(instance.queue, result);
    }

}
