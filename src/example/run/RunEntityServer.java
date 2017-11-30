package example.run;

import example.EntityArrivalProcess;
import example.EntityServer;
import simkit.Schedule;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;
import simkit.stat.CollectionSizeTimeVaryingStats;
import simkit.stat.SimpleStatsTally;
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
        double stopTime = 10000.0;
        Schedule.stopAtTime(stopTime);

        Schedule.reset();
        Schedule.startSimulation();

        System.out.printf("%nSimulation ended at time %,.3f%n%n", Schedule.getSimTime());

        System.out.printf("There have been %,d arrivals%n", entityCreator.getNumberArrivals());
        System.out.printf("There have been %,d served%n%n", timeInSystemStat.getCount());

        double arrivalRate = entityCreator.getNumberArrivals() / Schedule.getSimTime();

        System.out.printf("Average # in queue:  \t%.4f%n", numberInQueueStat.getMean());
        System.out.printf("Average utilization:  \t%.4f%n", 1.0 - numberAvailableServersStat.getMean()
                / entityServer.getTotalNumberServers());
        System.out.printf("Average arrival rate:\t%.4f%n%n", arrivalRate);

        System.out.println("Via Direct Tally:");
        System.out.printf("Average delay in queue:\t%.4f%n", delayInQueueStat.getMean());
        System.out.printf("Average time in System:\t%.4f%n%n", timeInSystemStat.getMean());

        System.out.println("Via Little's Formula:");
        double delayInQueueLittle = numberInQueueStat.getMean() / arrivalRate;

        System.out.printf("Average delay in queue:\t%.4f%n", delayInQueueLittle);
        double avgNumberInSystem = numberInQueueStat.getMean() + entityServer.getTotalNumberServers()
                - numberAvailableServersStat.getMean();
        System.out.printf("Average time in system:\t%.4f%n",
                avgNumberInSystem / arrivalRate);

    }

}
