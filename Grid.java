import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Grid {
    private final int aWidth = 10;
    private int aHeight;
    private volatile Cell[][] aGrid;

    // Initialize all cells in the grid
    public Grid(int pHeight){
        aHeight = pHeight;
        aGrid = new Cell[aHeight][aWidth];

        for (int x = 0; x < aWidth; x++){
            for(int y = 0; y < aHeight; y++){
                aGrid[y][x] = new Cell();
            }
        }

    }

    // Return true if the point falls within the grid's bounds
    public boolean inBounds(Point p){
        return (0 <= p.x && p.x < aWidth) && (0 <= p.y && p.y < aHeight);
    }

    // Return true if the transformation does not send the tetromino out of bounds.
    public boolean feasible(Transformation t){
        return inBounds(t.aCenter) && t.aOtherPoints.stream().allMatch(p -> inBounds(p));
    }

    /* Takes as input the tetromino requesting the locks and a set of cells (points) to be locked.
    * If a single lock could not be acquired, release all newly acquired locks (the set of cells to be acquired
    * may include cells the tetromino currently owns).*/
    public boolean lockAll(Tetromino pTetromino, HashSet<Point> coordinates){

        HashSet<Point> newlyAcquired = new HashSet<>();

        for(Point p: coordinates){
            Cell c = aGrid[p.y][p.x];
            if(c.isOwner(pTetromino)){continue;}

            // A lock could not be acquired
            if(!c.acquireLock(pTetromino)){
                unlockAll(pTetromino, newlyAcquired);
                return false;
            }
            newlyAcquired.add(p);
        }
        // All locks have been acquired
        return true;
    }

    // A tetromino requests a set of cells (points) to be unlocked, it will unlock all cells under the ownership
    // of the requesting tetromino.
    public void unlockAll(Tetromino pTetromino, HashSet<Point> coordinates){
        for(Point p: coordinates){
            aGrid[p.y][p.x].releaseLock(pTetromino);
        }
    }

    public int getWidth(){
        return aWidth;
    }

    public int getHeight(){
        return aHeight;
    }

    @Override
    public String toString(){
        String sb = "\n";
        for(int y = aHeight - 1; y >= 0; y--){
            sb += "#";
            for (int x = 0; x < aWidth; x++){
                sb += aGrid[y][x];
            }
            sb += "#\n";
        }
        sb += "#".repeat(aWidth + 2);
        return sb;
    }

}
