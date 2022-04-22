import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class q1 {

    private static ArrayList<TetrominoTemplate> tetrominoTemplates = new ArrayList<>();
    private static char startascii = '!'; // You can set this a-z or A-Z if you prefer
    private static char endascii = '~';

    public static void main(String[] args){
        // Parse inputs
        if (args.length != 5){
            System.out.println(String.format("ERROR: Not enough arguments, found: %d expected %d", args.length, 5));
            System.exit(1);
        }

        // Program input variables
        int gridHeight = Integer.parseInt(args[0]);
        int periodicity = Integer.parseInt(args[1]);
        double transformProbability = Double.parseDouble(args[2]);
        int maxStep = Integer.parseInt(args[3]);
        int tetrominosCount = Integer.parseInt(args[4]);

        // Initialize grid and tetromino templates (templates are initial form of tetrominos)
        Grid grid = new Grid(gridHeight);
        initializeTemplates(grid.getWidth(), grid.getHeight());

        ArrayList<Thread> threads = new ArrayList<>();

        // The barrier takes as input the number of thread to rendez-vous, this number is incrementable
        // The last thread at the rendez-vous displays the grid and increments the timestep
        IncrementableSenseBarrier barrier = new IncrementableSenseBarrier(1, grid, 0);

        char ascii = startascii;
        Random templateRandom = new Random(tetrominoTemplates.size());


        while(threads.size() < tetrominosCount) {

            // Attempt to spawn a new Tetromino
            if (barrier.getTimestep() % periodicity == 0){
                TetrominoTemplate tt = tetrominoTemplates.get(templateRandom.nextInt(tetrominoTemplates.size()));
                Tetromino tetromino = tt.createTetrominoFromTemplate(ascii);

                // Tetromino "starts" in the middle, find the ways to transform by horizontal displacement
                // I could've started  it on the left and had a single loop, but this I originally thought it would
                // alway spawn in the middle, so this is kind of a hotfix
                ArrayList<Transformation> initialPositions = new ArrayList<>();
                for (int j = 0; j < grid.getWidth(); j++) {
                    initialPositions.add(tetromino.getHorizontalTranslation(j));
                }
                for (int j = 1; j < grid.getWidth(); j++) {
                    initialPositions.add(tetromino.getHorizontalTranslation(-j));
                }
                initialPositions.removeIf(t -> !grid.feasible(t));

                // Only spawn tetromino if the position is feasible
                if(initialPositions.size() > 0) {

                    // We pick a random start position by picking a random feasible horizontal translation
                    Random r = new Random(initialPositions.size());
                    Transformation initialPosition = initialPositions.get(r.nextInt(initialPositions.size()));
                    tetromino.applyTransformation(initialPosition);

                    // Only spawn tetromino if all locks can be acquired
                    if(grid.lockAll(tetromino, new HashSet<>(tetromino.getList()))){
                        // New ASCII character
                        if(ascii++ > endascii){
                            ascii = startascii;
                        }
                        // The barrier now needs to wait for one more thread
                        barrier.increment();
                        // Start the tetromino thread
                        TetrominoHandler handler = new TetrominoHandler(grid, tetromino, transformProbability, maxStep, barrier);
                        Thread t = new Thread(handler);
                        t.start();
                        threads.add(t);
                    }
                }
            }
            barrier.await();
        }

        barrier.leave();

        for(Thread t : threads){
            try{ t.join(); }catch (InterruptedException e){}
        }

    }

    // Initializes all type of tetrominos by defining their starting points, all of them start at the middle
    // This is later randomized when they are spawned by picking a random horizontal translation transformation
    public static void initializeTemplates(int width, int height){
        int middle = width/2;
        int topRow = height - 1;
        tetrominoTemplates.add(new TetrominoTemplate(Block.I_BLOCK, new Point(middle, topRow),new Point[] {
                new Point(middle - 1, topRow), new Point(middle - 2, topRow), new Point(middle + 1, topRow)
        }));

        tetrominoTemplates.add(new TetrominoTemplate(Block.J_BLOCK, new Point(middle, topRow - 1),new Point[] {
                new Point(middle - 1, topRow - 1), new Point(middle - 1, topRow), new Point(middle + 1, topRow - 1)
        }));

        tetrominoTemplates.add(new TetrominoTemplate(Block.L_BLOCK, new Point(middle, topRow - 1),new Point[] {
                new Point(middle - 1, topRow - 1), new Point(middle + 1, topRow), new Point(middle + 1, topRow - 1)
        }));

        tetrominoTemplates.add(new TetrominoTemplate(Block.O_BLOCK, new Point(middle, topRow),new Point[] {
                new Point(middle - 1, topRow), new Point(middle - 1, topRow - 1), new Point(middle, topRow - 1)
        }));

        tetrominoTemplates.add(new TetrominoTemplate(Block.S_BLOCK, new Point(middle, topRow),new Point[] {
                new Point(middle + 1, topRow), new Point(middle, topRow - 1), new Point(middle - 1, topRow - 1)
        }));

        tetrominoTemplates.add(new TetrominoTemplate(Block.T_BLOCK, new Point(middle, topRow - 1),new Point[] {
                new Point(middle - 1, topRow - 1), new Point(middle, topRow), new Point(middle + 1, topRow - 1)
        }));

        tetrominoTemplates.add(new TetrominoTemplate(Block.Z_BLOCK, new Point(middle, topRow),new Point[] {
                new Point(middle - 1, topRow), new Point(middle, topRow - 1), new Point(middle + 1, topRow - 1)
        }));

    }
}
