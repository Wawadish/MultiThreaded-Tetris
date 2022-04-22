import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class TetrominoHandler implements Runnable{
    private Grid aGrid;
    private Tetromino aTetromino;
    private double aTransformProbability;
    private int aMaxStep;;
    private IncrementableSenseBarrier aBarrier;
    private Random r = new Random();

    public TetrominoHandler(Grid  pGrid, Tetromino pTetromino, double pTransformProbability, int pMaxStep, IncrementableSenseBarrier pBarrier){
        aGrid = pGrid;
        aTetromino = pTetromino;
        aTransformProbability = pTransformProbability;
        aMaxStep = pMaxStep;
        aBarrier = pBarrier;
    }

    @Override
    public void run() {
        // I believe the code here is self-evident
        while(true){
            aBarrier.await();
            if(reachedBottom()){return;}
            transformStep();
            if(reachedBottom()){return;}
            moveStep();
        }
    }

    // If the bottom has been reached (one point in the tetromino has a y-position of 0). Then leave give up ownership
    // of the currently owned cells and leave the barrier.
    public boolean reachedBottom(){
        for(Point p : aTetromino){
            if(p.y == 0){
                aGrid.unlockAll(aTetromino, new HashSet<>(aTetromino.getList()));
                aBarrier.leave();
                return true;
            }
        }
        return false;
    }

    // Responsible for moving the tetromino down
    public void moveStep(){
        int d = aMaxStep;
        for(Point p: aTetromino){
            d = Math.min(d, p.y);
        }
        // Choose a random distance
        int distance = r.nextInt(d) + 1;

        // Where the tetromino will end up
        Transformation finalDescendTransformation = null;

        // Cells the tetromino has to go through
        HashSet<Point> descentCoordinates = new HashSet<>();

        // Fill the set of cells the tetromino has to go through
        for(int i = 1; i <= distance; i++){
            Transformation t = aTetromino.getDescendTransformation(i);

            // If intermediate step is impossible, abort the descent
            if(!aGrid.feasible(t)){return;}

            descentCoordinates.addAll(t.getList());
            if(i == distance){
                finalDescendTransformation = t;
            }
        }
        // Current position
        descentCoordinates.addAll(aTetromino.getList());

        if(aGrid.lockAll(aTetromino, descentCoordinates)){
            // Update tetromino coordinates
            aTetromino.applyTransformation(finalDescendTransformation);
            // Keep the final cells locked
            descentCoordinates.removeAll(aTetromino.getList());
            // Unlock/release the rest
            aGrid.unlockAll(aTetromino, descentCoordinates);
        }
    }

    // Responsible for transforming the tetromino
    public void transformStep(){
        if(r.nextDouble() < aTransformProbability){

            ArrayList<Transformation> allTransformations = aTetromino.getAllTransformations();
            Transformation pickedTransformation = allTransformations.get(r.nextInt(allTransformations.size()));

            if(!aGrid.feasible(pickedTransformation)){return;}

            // The set of cells to be acquired
            HashSet<Point> transformationCoordinates = new HashSet<>();
            transformationCoordinates.addAll(aTetromino.getList()); // Current Cells
            transformationCoordinates.addAll(pickedTransformation.getList()); // Final Cells


            if(aGrid.lockAll(aTetromino, transformationCoordinates)){
                // Transform and occupy the new final position in the grid
                aTetromino.applyTransformation(pickedTransformation);

                // Release only those that are not part of the final position
                transformationCoordinates.removeAll(aTetromino.getList());
                aGrid.unlockAll(aTetromino, transformationCoordinates);
            }
        }
    }

}
