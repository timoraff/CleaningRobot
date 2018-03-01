import java.util.HashSet;
import java.util.Set;

public class Maze {
    private Coords maxPos;
    private Coords minPos;

    private Set<Edges> environment;
    private Set<Edges> obstacle;

    /**
     * contains the coordinate system, including the walls
     * also takes care about displaying the maze and robot movement
     */
    Maze() {
        environment = new HashSet<>();
        environment.add(new Edges(new Coords(0.0, 0.0), new Coords(30.0, 0.0)));
        environment.add(new Edges(new Coords(30.0, 0.0), new Coords(30.0, 30.0)));
        environment.add(new Edges(new Coords(30.0, 30.0), new Coords(0.0, 30.0)));
        environment.add(new Edges(new Coords(0.0, 30.0), new Coords(0.0, 0.0)));

        obstacle = new HashSet<>();
        obstacle.add(new Edges(new Coords(6.0, 6.0), new Coords(22.0, 6.0)));
        obstacle.add(new Edges(new Coords(22.0, 6.0), new Coords(22.0, 22.0)));
        obstacle.add(new Edges(new Coords(22.0, 22.0), new Coords(6.0, 22.0)));
        obstacle.add(new Edges(new Coords(6.0, 22.0), new Coords(6.0, 6.0)));
    }

    public boolean updatePosition(double x, double y) {
        //check outside of environment
        return true;
    }

    public Coords getCorrectPosition(Coords oldPos, Coords newPos) {
        for (Edges e : environment) {
            //Wall
            double Ax = e.getFrom().getX();
            double Ay = e.getFrom().getY();
            double Bx = e.getTo().getX();
            double By = e.getTo().getY();

            //Robot movement
            double Cx = oldPos.getX();
            double Cy = oldPos.getY();
            double Dx = newPos.getX();
            double Dy = newPos.getY();

            //calculations of intersection
            double Ex = Bx - Ax;
            double Ey = By - Ay;
            double Fx = Dx - Cx;
            double Fy = Dy - Cy;
            double Px = -Ey;
            double Py = Ex;
            double h = ((Ax - Cx) * Px + (Ay - Cy) * Py) / (Fx * Px + Fy * Py);

            //if h is between 0 and 1, lines are intersecting
            //if h is exact 0 or 1 the lines touch at an end point
            //if h is smaller than 0, the line is behind the given line
            //if h is bigger than 1 the line is in front of the given line
            //if FxFy and PxPy are zero, the lines are parallel
            if (h >= 0 && h <= 1) {
                return new Coords(Cx + Fx * h, Cy + Fy * h);
            }
        }
        for (Edges e : obstacle) {
            //Wall
            double Ax = e.getFrom().getX();
            double Ay = e.getFrom().getY();
            double Bx = e.getTo().getX();
            double By = e.getTo().getY();

            //Robot movement
            double Cx = oldPos.getX();
            double Cy = oldPos.getY();
            double Dx = newPos.getX();
            double Dy = newPos.getY();

            //calculations of intersection
            double Ex = Bx - Ax;
            double Ey = By - Ay;
            double Fx = Dx - Cx;
            double Fy = Dy - Cy;
            double Px = -Ey;
            double Py = Ex;
            double h = ((Ax - Cx) * Px + (Ay - Cy) * Py) / (Fx * Px + Fy * Py);

            //if h is between 0 and 1, lines are intersecting
            //if h is exact 0 or 1 the lines touch at an end point
            //if h is smaller than 0, the line is behind the given line
            //if h is bigger than 1 the line is in front of the given line
            //if FxFy and PxPy are zero, the lines are parallel
            if (h >= 0 && h <= 1) {
                return new Coords(Cx + Fx * h, Cy + Fy * h);
            }
        }
        return newPos;
    }

    public Coords getMaxPos() {
        return maxPos;
    }

    public Coords getMinPos() {
        return minPos;
    }
    
    public Set<Edges> getEnvironment() {
        return environment;
    }
    
    public Set<Edges> getObstacle() {
        return obstacle;
    }
}
