package example;

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
import simkit.util.UnitTestEventList;
import static util.Utilities.EPSILON;
import static util.Utilities.setupEventList;

/**
 *
 * @author ahbuss
 */
public class EntityArrivalProcessTest {
    
    private static UnitTestEventList eventList;
    
    private static EntityArrivalProcess instance;
        
    public EntityArrivalProcessTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        eventList = setupEventList();
        instance = new EntityArrivalProcess();
        instance.setInterarrivalTimeGenerator(RandomVariateFactory.getInstance("Constant", 2.3));
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of doArrival method, of class EntityArrivalProcess.
     */
    @Test
    public void testDoArrival() {
        System.out.println("doArrival");
        double simTime = 17579.4;
        eventList.setSimTime(simTime);
        double interarrivalTime = 31.5;
        instance.setInterarrivalTimeGenerator(RandomVariateFactory.getInstance("Constant", interarrivalTime));
        instance.doArrival();
        
        assertEquals(2, eventList.getScheduledEvents().size());
        SimEvent scheduledEvent = eventList.getScheduledEvent("Arrival", new Object[0]);
        assertNotNull(scheduledEvent);
        assertEquals(eventList.getSimTime() + interarrivalTime, scheduledEvent.getScheduledTime() , EPSILON);

//        This is a bit tricky, since we can't get a direct reference to the
//        Entity argument. However, since the Arrival(Entity) event should
//        come before the Arrival() event, since the latter is scheduled with
//        a delay and the former with a delay of 0.0, it should be the first
//        scheduled event. We can then at least check to 
        SortedSet<SimEvent> allEvents = eventList.getScheduledEvents("Arrival");
        scheduledEvent = allEvents.first();
        assertNotNull(scheduledEvent);
        assertEquals(eventList.getSimTime(), scheduledEvent.getScheduledTime(), EPSILON);
        Object[] parameters = scheduledEvent.getParameters();
        assertEquals(1, parameters.length);
        
//        Not 100% necessary, since the Entity class (presumably) "works"
        assertTrue(parameters[0] instanceof Entity);
        Entity entity = (Entity) parameters[0];
        assertEquals(0.0, entity.getElapsedTime(), EPSILON);
        assertEquals(0.0, entity.getAge(), EPSILON);
        
//        Run to next Arrival event - the Entity's age should be equal to
//        interarrivalTime
        eventList.stopOnEvent(1, "Arrival");
        eventList.startSimulation();
        assertEquals(interarrivalTime, entity.getAge(), EPSILON);
        
    }
    
}
