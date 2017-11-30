package example;

import java.beans.PropertyChangeEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
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
public class ArrivalProcessTest {
        
    private static UnitTestEventList eventList;

    private static ArrivalProcess instance;

    private static PropertyChangeListenerHelper numberArrivalsPropertyChangeListener;

    public ArrivalProcessTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        eventList = setupEventList();
        instance = new ArrivalProcess();
        numberArrivalsPropertyChangeListener = new PropertyChangeListenerHelper();
        instance.addPropertyChangeListener("numberArrivals", numberArrivalsPropertyChangeListener);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        eventList.coldReset();
        instance.setInterarrivalTimeGenerator(RandomVariateFactory.getInstance("Constant", 2.5));
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of reset method, of class ArrivalProcess.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        instance.numberArrivals = 1000;
        instance.reset();
        assertEquals(0, instance.numberArrivals);
    }

    /**
     * Test of doRun method, of class ArrivalProcess.
     */
    @Test
    public void testDoRun() {
        System.out.println("doRun");
        
        double interarrivalTime = 1.6;
        instance.setInterarrivalTimeGenerator(
                RandomVariateFactory.getInstance("Constant", interarrivalTime));
        
        instance.numberArrivals = 0;
        instance.doRun();

        PropertyChangeEvent numberArrivalsEvent
                = numberArrivalsPropertyChangeListener.getLastEvent();
        assertNotNull(numberArrivalsEvent);
        assertNull(numberArrivalsEvent.getOldValue());
        assertNotNull(numberArrivalsEvent.getNewValue());
        assertEquals(instance.numberArrivals, numberArrivalsEvent.getNewValue());
        
        assertEquals(1, eventList.getScheduledEvents().size());
        SimEvent scheduledEvent = eventList.getScheduledEvent("Arrival");
        assertNotNull(scheduledEvent);
        assertEquals(eventList.getSimTime() + interarrivalTime,
                scheduledEvent.getScheduledTime(), EPSILON);
        assertEquals(0, scheduledEvent.getParameters().length);
    }

    /**
     * Test of doArrival method, of class ArrivalProcess.
     */
    @Test
    public void testDoArrival() {
        System.out.println("doArrival");
        
        double interarrivalTime = 3.6;
        instance.setInterarrivalTimeGenerator(
                RandomVariateFactory.getInstance("Constant", interarrivalTime));
        
        instance.numberArrivals = 200;
        instance.doArrival();

        PropertyChangeEvent numberArrivalsEvent
                = numberArrivalsPropertyChangeListener.getLastEvent();
        assertNotNull(numberArrivalsEvent);
        assertNull(numberArrivalsEvent.getOldValue());
        assertNotNull(numberArrivalsEvent.getNewValue());
        assertEquals(instance.numberArrivals, numberArrivalsEvent.getNewValue());
        
        assertEquals(1, eventList.getScheduledEvents().size());
        SimEvent scheduledEvent = eventList.getScheduledEvent("Arrival");
        assertNotNull(scheduledEvent);
        assertEquals(eventList.getSimTime() + interarrivalTime,
                scheduledEvent.getScheduledTime(), EPSILON);
        assertEquals(0, scheduledEvent.getParameters().length);
    }

}
