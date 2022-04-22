import java.util.concurrent.atomic.AtomicBoolean;

/* A cell represents a position in the grid. The string representation of the cell is simply the symbol of the tetromino
* that owns it. The cell can only be freed by the tetromino that owns it. A tetromino can only own a cell if no other
* tetromino owns the cell. */
public class Cell {

    // If null, the lock is free, if not null then lock owner is the tetromino reference held by aOwner
    private volatile Tetromino aOwner;

    @Override
    public String toString(){
        return aOwner == null ? " " : aOwner.getSymbol();
    }

    // Returns whether the input tetromino is the owner of the cell
    public synchronized boolean isOwner(Tetromino pTetromino){
        if(aOwner == pTetromino){
            return true;
        }
        return false;
    }


    // Returns false if the lock could not be acquired, takes as input the tetromino requesting the lock
    public synchronized boolean acquireLock(Tetromino pTetromino){
        if(aOwner == pTetromino){
            return true;
        }
        if (aOwner == null){
            aOwner = pTetromino;
            return true;
        }
        return false;
    }

    // Release a lock if the input tetromino (the one requesting the lock to be released) is the current owner
    public synchronized void releaseLock(Tetromino pOwner){
        if(aOwner == pOwner){
            aOwner = null;
        }else {
            System.out.println("A Tetromino tried to release a lock it does not own");
        }
    }
}
