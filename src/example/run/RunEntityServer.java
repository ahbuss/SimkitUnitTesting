package example.run;

import example.EntityArrivalProcess;
import example.EntityServer;
import simkit.Schedule;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;
import simkit.stat.CollectionSizeTimeVaryingStats;
import simkit.stat.SimpleStatsTally;
import simkit.stat.SimpleStatsTimeVarying;
import simkit.util.SimplePropertyDumper;

/**
 *
 * @author ahbuss
 */
public class RunEntityServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RandomVariate interarrivalTimeGenerator
                = RandomVariateFactory.getInstance("Uniform", 0.9, 2.2);
        EntityArrivalProcess entityCreator = new EntityArrivalProcess(interarrivalTimeGenerator);

        int totalNumerServers = 2;
        RandomVariate serviceTimeGenerator
                = RandomVariateFactory.getInstance("Gamma", 1.7, 1.8);
        EntityServer entityServer = new EntityServer(totalNumerServers, serviceTimeGenerator);

        entityCreator.addSimEventListener(entityServer);

        System.out.println(entityCreator);
        System.out.println(entityServer);

        CollectionSizeTimeVaryingStats numberAvailableServersStat = new CollectionSizeTimeVaryingStats("availableServers");
        CollectionSizeTimeVaryingStats numberInQueueStat = new CollectionSizeTimeVaryingStats("queue");
        SimpleStatsTally delayInQueueStat = new SimpleStatsTally("delayInQueue");
        SimpleStatsTally timeInSystemStat = new SimpleStatsTally("timeInSystem");

        entityServer.addPropertyChangeListener(numberAvailableServersStat);
        entityServer.addPropertyChangeListener(numberInQueueStat);
        entityServer.addPropertyChangeListener(delayInQueueStat);
        entityServer.addPropertyChangeListener(timeInSystemStat);

        SimplePropertyDumper simplePropertyDumper = new SimplePropertyDumper(true);
//        entityCreator.addPropertyChangeListener(simplePropertyDumper);
//        entityServer.addPropertyChangeListener(simplePropertyDumper);

//        Schedule.setVerbose(true);
        double stopTime = 10000;
        Schedule.stopAtTime(stopTime);

        Schedule.reset();
        Schedule.startSimulation();
        
        System.out.println("Simulation ended at time " + Schedule.getSimTime());
        
        System.out.printf("Avg # available servers: %.3f%n", numberAvailableServersStat.getMean());
        System.out.printf("Avg utilization: %.3f%n", 1.0 - numberAvailableServersStat.getMean() / 
                    entityServer.getTotalNumberServers());
        System.out.printf("Avg # in queue: %.3f%n", numberInQueueStat.getMean());
        System.out.printf("Avg delay in queue: %.3f%n", delayInQueueStat.getMean());
        System.out.printf("Avg time in System: %.3f%n", timeInSystemStat.getMean());
        
        double arrivalRate = entityCreator.getNumberArrivals() / Schedule.getSimTime();
        System.out.printf("Avg arrival rate: %.3f%n", arrivalRate);
        
        double delayInQueueLittle = numberInQueueStat.getMean() / arrivalRate;
        
        System.out.printf("Delay in queue via Little: %.3f%n", delayInQueueLittle );
        
        double avgNumberInSystem = numberInQueueStat.getMean() + entityServer.getTotalNumberServers() -
                numberAvailableServersStat.getMean();
        
        System.out.printf("Avg # in System: %.3f%n", avgNumberInSystem);
        System.out.printf("Avg time in system via Little: %.3f%n",
                 avgNumberInSystem / arrivalRate);
        
    }

}
