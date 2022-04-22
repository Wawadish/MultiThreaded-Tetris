import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

/* A set of points, a symbol and methods to transform that set of points, the feasibility of the transformation(s)
*  will be handled by the TetrominoHandler, the tetromino only handles the transformation logic (rotating a point
*  around another point, or translating points). It does not concern itself with the grid or the tetris game.
*  The glue between the Grid and a Tetromino is the TetrominoHandler.*/
public class Tetromino implements Iterable<Point> {

    private volatile char aSymbol;
    private volatile Block aTetrominoType;
    private volatile Point aCenter;
    private volatile ArrayList<Point> aOtherPoints;

    public Tetromino(char pSymbol, Block pTetrominoType, Point pCenter, Point[] pOtherPoints){
        aSymbol = pSymbol;
        aTetrominoType = pTetrominoType;

        aCenter = pCenter;
        aOtherPoints = new ArrayList<>(Arrays.asList(pOtherPoints));
    }

    public String getSymbol(){
        return Character.toString(aSymbol);
    }

    // Vertical translation, similar to horizontal transformation. Positive d = down, Negative d = up (unused).
    public Transformation getDescendTransformation(int d){
        return transform(p -> new Point(p.x, p.y - d));
    }

    // After the feasibility of a transformation has been determined, it will apply the transformation
    public void applyTransformation(Transformation pTransformation){
        aCenter = pTransformation.aCenter;
        aOtherPoints = pTransformation.aOtherPoints;
    }

    // All transformation possible during the transform step in the TetrominoHandler
    public ArrayList<Transformation> getAllTransformations(){
        return new ArrayList<>(Arrays.asList(new Transformation[] {getLeftTranslation(), getRightTranslation(),
                getClockWiseRotation(), getCounterClockWiseRotation()}));
    }

    public Transformation getLeftTranslation(){
        return getHorizontalTranslation(-1);
    }

    public Transformation getRightTranslation(){
        return getHorizontalTranslation(1);
    }

    // Translate this tetromino horizontally by the given distance (negative = left, positive = right),
    // return the resulting transformation.
    // @post current tetromino remains unmodified
    public Transformation getHorizontalTranslation(int d){
        return transform( p -> new Point(p.x + d, p.y));
    }

    public Transformation getCounterClockWiseRotation(){
        return getRotation(90);
    }

    public Transformation getClockWiseRotation(){
        return getRotation(-90);
    }

    // Rotate this tetromino by the given degrees, return the resulting transformation.
    // @post current tetromino remains unmodifiedd
    public Transformation getRotation(int degree){
        // Square block does not rotate
        if(aTetrominoType == Block.O_BLOCK){
            return transform( p -> new Point(p.x, p.y));
        }else{
            return transform( p -> rotatePoint(p, degree));
        }
    }

    // Rotate p around the tetromino's center, output a new Point with the result.
    private Point rotatePoint(Point p, int degree){
        double rad = Math.toRadians(degree);
        double x = aCenter.x + (p.x - aCenter.x) * Math.cos(rad) - (p.y - aCenter.y) * Math.sin(rad);
        double y = aCenter.y + (p.x - aCenter.x) * Math.sin(rad) + (p.y - aCenter.y) * Math.cos(rad);
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    // @pre transformationFunction Point->Point returns a new Point(), does not modify the original Point
    private Transformation transform(Function<Point, Point> transformationFunction){
        ArrayList<Point> transformedOtherPoints = new ArrayList<>();
        for(Point p : aOtherPoints){
            transformedOtherPoints.add(transformationFunction.apply(p));
        }
        return new Transformation(transformationFunction.apply(aCenter), transformedOtherPoints);
    }

    public ArrayList<Point> getList(){
        ArrayList<Point> points = new ArrayList<>(aOtherPoints);
        points.add(aCenter);
        return points;
    }

    @Override
    public Iterator<Point> iterator() {
        return getList().iterator();
    }
}
