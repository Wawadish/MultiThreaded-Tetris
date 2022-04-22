import java.util.concurrent.atomic.AtomicInteger;

/* IncrementableSenseBarrier code below is mostly from the book */
public class IncrementableSenseBarrier {

    private AtomicInteger aCount;
    private AtomicInteger aSize;
    private volatile boolean aSense = false;
    private ThreadLocal<Boolean> threadSense;
    private Grid aGrid; // Will not change
    private AtomicInteger aTimestep;

    public IncrementableSenseBarrier(int numThreads, Grid pGrid, int pTimeStep){
        aCount = new AtomicInteger(numThreads);
        aSize = new AtomicInteger(numThreads);
        aSense = false;
        threadSense = ThreadLocal.withInitial(() -> !aSense);
        aGrid = pGrid;
        aTimestep = new AtomicInteger(pTimeStep);
    }

    public int getTimestep(){
        return aTimestep.get();
    }

    public void await(){
        boolean mySense = threadSense.get();
        int position = aCount.getAndDecrement();
        if (position == 1){
            nextPhase(mySense);
        }else{
            while(aSense != mySense){Thread.yield();}
        }
        threadSense.set(!mySense);
    }

    public void increment(){
        aSize.incrementAndGet();
        aCount.incrementAndGet();
    }

    public void leave(){
        aSize.decrementAndGet();

        boolean mySense = threadSense.get();
        int position = aCount.getAndDecrement();
        if (position == 1){
            nextPhase(mySense);
        }
    }

    private void nextPhase(boolean newSense){
        System.out.println("timestep: " + aTimestep);
        System.out.println(aGrid);
        aTimestep.incrementAndGet();
        aCount.set(aSize.get());
        aSense = newSense;
    }

}
