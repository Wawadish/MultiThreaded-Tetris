import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class q2 {

    private static int numThreads;
    private static int numRounds;

    // There will be (numberThreads^2)/2 possible ids
    private static int bouncerId = 0;

    // A[id] counts the amount of threads that received that id throughout any round
    private static AtomicInteger[] A;

    // The first ID each thread gets
    private static AtomicInteger initialId = new AtomicInteger(0);
    private static Bouncer[][] bouncers;

    public static void main(String[] args) {
        // Parse inputs
        if (args.length != 2) {
            System.out.println(String.format("ERROR: Not enough arguments, found: %d expected %d", args.length, 2));
            System.exit(1);
        }

        numThreads = Integer.parseInt(args[0]);
        numRounds = Integer.parseInt(args[1]);

        /* Initialize bouncers triangle */
        bouncers = new Bouncer[numThreads][numThreads];
        for(int i = 0; i < numThreads;  i++){
            for(int j = 0; j < numThreads - i; j++){
                // We don't really care about how the bouncerIds are ordered, just that they are unique & bounded
                bouncers[j][i] = new Bouncer(bouncerId++, numRounds);
            }
        }

        /* Initializing array and counters to 0 */
        A = new AtomicInteger[bouncerId];
        for(int i = 0; i < A.length; i++){
            A[i] = new AtomicInteger(0);
        }

        /* Start all threads */
        Thread[] threads = new Thread[numThreads];
        for(int i = 0; i < numThreads; i++){
            threads[i] = new Thread(() -> threadHandler());
            threads[i].start();
        }

        /* Wait for all threads to finish */
        for(int i = 0; i < numThreads; i++){
            try {threads[i].join();} catch (InterruptedException e) {e.printStackTrace();}
        }

        // Validate result
        int total = 0;
        for(int id = 0; id < A.length; id++){
            int count = A[id].get();
            if(count > numRounds){
                System.out.println("ERROR: One of the IDs was not unique during a round: " + id);
            }
            total += count;
        }
        if(total != numThreads*numRounds) {
            System.out.println("ERROR:  Too many or too little IDs have been distributed, " +
                    "ensure all threads get one and only ID per round");
        }
    }

    public static void threadHandler(){
        // Used for sleeping
        Random r = new Random();

        int id = -1;
        int round = 0;
        // Round 0, typical increment atomic integer and get ID, this is obviously not bounded by the amount of threads
        // Therefore we only use this method in the first round id in [0, n-1], clearly bounded by thread count
        id = initialId.getAndIncrement();

        // Now we use the bouncer using initial ID (can technically skip round 0, but I won't)
        while(round < numRounds){

            // Enter renaming network
            int x = 0, y = 0, result;
            while(true){
                result = bouncers[y][x].visit(id, round);
                if(result == Bouncer.RIGHT){
                    x++;
                } else if(result == Bouncer.DOWN){
                    y++;
                } else if(result == Bouncer.STOP){
                    break;
                }else{
                    System.out.println("ERROR: invalid bouncer result");
                    System.exit(1);
                }
            }

            // Get id
            id = bouncers[y][x].aBouncerId;

            A[id].incrementAndGet();
            try {Thread.sleep(r.nextInt(11));} catch (InterruptedException e) {e.printStackTrace();}
            round++;
        }
    }

}
