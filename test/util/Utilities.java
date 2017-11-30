package util;

import simkit.BasicEventList;
import simkit.Schedule;
import simkit.util.UnitTestEventList;

/**
 *
 * @author ahbuss
 */
public class Utilities {
    
    public static final double EPSILON = 1.0E-8;
    
    public static UnitTestEventList setupEventList() {
        BasicEventList currentDefaultEventList = Schedule.getDefaultEventList();
        UnitTestEventList eventList = null;
        if (!(currentDefaultEventList instanceof UnitTestEventList)) {
            int id = Schedule.addNewEventList(UnitTestEventList.class);
            eventList = (UnitTestEventList) Schedule.getEventList(id);
            Schedule.setDefaultEventList(eventList);
        }
        return eventList;
    }
    
}
