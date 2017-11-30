package example;

import java.util.Set;
import java.util.SortedSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import simkit.Entity;
import simkit.Schedule;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;
import simkit.util.PropertyChangeListenerHelper;
import simkit.util.UnitTestEventList;

/**
 *
 * @author ahbuss
 */
public class EntityServerTest {
    
    private static UnitTestEventList eventList;
    
    private static PropertyChangeListenerHelper queuePropertyChangeListener;
    private static PropertyChangeListenerHelper availableServersPropertyChangeListener;
    private static EntityServer instance;
    
    public EntityServerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        int id = Schedule.addNewEventList(UnitTestEventList.class);
        eventList = (UnitTestEventList )Schedule.getEventList(id);
        Schedule.setDefaultEventList(eventList);
        
        instance = new EntityServer();
        instance.setTotalNumberServers(5);
        instance.setServiceTimeGenerator(RandomVariateFactory.getInstance("Constant", 1.2));
        queuePropertyChangeListener = new PropertyChangeListenerHelper();
        availableServersPropertyChangeListener = new PropertyChangeListenerHelper();
        instance.addPropertyChangeListener(queuePropertyChangeListener);
        instance.addPropertyChangeListener(availableServersPropertyChangeListener);
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance.reset();
        queuePropertyChangeListener.reset();
        availableServersPropertyChangeListener.reset();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of reset method, of class EntityServer.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        EntityServer instance = null;
        instance.reset();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doRun method, of class EntityServer.
     */
    @Test
    public void testDoRun() {
        System.out.println("doRun");
        EntityServer instance = null;
        instance.doRun();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doArrival method, of class EntityServer.
     */
    @Test
    public void testDoArrival() {
        System.out.println("doArrival");
        Entity entity = null;
        EntityServer instance = null;
        instance.doArrival(entity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doStartService method, of class EntityServer.
     */
    @Test
    public void testDoStartService() {
        System.out.println("doStartService");
        EntityServer instance = null;
        instance.doStartService();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doEndService method, of class EntityServer.
     */
    @Test
    public void testDoEndService() {
        System.out.println("doEndService");
        int server = 0;
        Entity entity = null;
        EntityServer instance = null;
        instance.doEndService(server, entity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doJobComplete method, of class EntityServer.
     */
    @Test
    public void testDoJobComplete() {
        System.out.println("doJobComplete");
        Entity entity = null;
        EntityServer instance = null;
        instance.doJobComplete(entity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTotalNumberServers method, of class EntityServer.
     */
    @Test
    public void testSetTotalNumberServers() {
        System.out.println("setTotalNumberServers");
        int totalNumberServers = 0;
        try {
            instance.setTotalNumberServers(totalNumberServers);
            fail("setTotalNumberServers(0) should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) { }
        
        totalNumberServers = -1;
        try {
            instance.setTotalNumberServers(totalNumberServers);
            fail("setTotalNumberServers(-1) should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) { }
        
        totalNumberServers = 1;
        instance.setTotalNumberServers(totalNumberServers);
        assertEquals(totalNumberServers, instance.getTotalNumberServers());
    }

    /**
     * Test of getAvailableServers method, of class EntityServer.
     */
    @Test
    public void testGetAvailableServers() {
        System.out.println("getAvailableServers");
        EntityServer instance = null;
        Set<Integer> expResult = null;
        Set<Integer> result = instance.getAvailableServers();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getQueue method, of class EntityServer.
     */
    @Test
    public void testGetQueue() {
        System.out.println("getQueue");
        EntityServer instance = null;
        SortedSet<Entity> expResult = null;
        SortedSet<Entity> result = instance.getQueue();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
