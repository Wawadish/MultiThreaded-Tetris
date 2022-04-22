import java.awt.*;


/* A template tetromino, is a tetromino, as seen in the pdf, starting at the middle of the grid. Do not pay
* much attention to this class, it is only used to call the Tetromino constructor */
public class TetrominoTemplate {

    private Block aTetrominoType;
    private Point aCenter;
    private Point[] aOtherCoordinates;

    public TetrominoTemplate(Block pTetrominoType, Point pCenter, Point... pPoints){
        aTetrominoType = pTetrominoType;
        aCenter = pCenter;
        aOtherCoordinates = pPoints;
    }

    private Point copyCenter(){
        return new Point(aCenter.x, aCenter.y);
    }

    private Point[] copyOtherCoordinates(){
        Point[] pointsCopy = new Point[aOtherCoordinates.length];
        for(int i = 0; i < aOtherCoordinates.length; i++){
            pointsCopy[i] = new Point(aOtherCoordinates[i].x, aOtherCoordinates[i].y);
        }
        return pointsCopy;
    }

    // Creates a tetromino by copying the template, the initial position (currently at the middle of the grid)
    // will be randomized by the main thread (q1).
    public Tetromino createTetrominoFromTemplate(char pSymbol){
        return new Tetromino(pSymbol, aTetrominoType, copyCenter(), copyOtherCoordinates());
    }
}
