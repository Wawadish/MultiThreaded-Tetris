import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class Bouncer {
    public static final int DOWN = 0;
    public static final int RIGHT = 1;
    public static final int STOP = 2;

    // Each element of the array needs to be volatile which is why I used Atomic, the array is used to handle different
    // rounds.
    private AtomicBoolean[] goRight;
    private AtomicInteger[] last;
    public int aBouncerId;

    // Initialize bouncer and arrays
    public Bouncer(int pBouncerId, int numRounds){
        aBouncerId = pBouncerId;
        goRight = new AtomicBoolean[numRounds];
        last = new AtomicInteger[numRounds];
        for(int i = 0; i < numRounds; i++){
            goRight[i] = new AtomicBoolean(false);
            last[i] = new AtomicInteger(-1);
        }
    }

    // Go LEFT, RIGHT or STOP
    int visit(int id, int round) {
        // Everything is always within the context of a round, I didn't modify the Bouncer logic.
        last[round].set(id);

        if (goRight[round].get())
            return RIGHT;

        goRight[round].set(true);

        if (last[round].get() == id)
            return STOP;
        else
            return DOWN;
    }
}