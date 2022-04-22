import java.awt.*;
import java.util.ArrayList;

/* Within the context of my program, a Transformation is the resulting set of points after a Rotation or Translation
*  on a Tetromino. A transformation represents where the tetromino would end up. A transformation can later be applied
*  on the tetromino (by swapping the tetromino's center and otherPoints with the transformation's).
* */
public class Transformation {
    public Point aCenter;
    public ArrayList<Point> aOtherPoints;

    public Transformation(Point pCenter, ArrayList<Point> pOtherPoints){
        aCenter = pCenter;
        aOtherPoints = pOtherPoints;
    }

    public ArrayList<Point> getList(){
        ArrayList<Point> points = new ArrayList<>(aOtherPoints);
        points.add(aCenter);
        return points;
    }
}
