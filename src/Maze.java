import java.util.HashSet;
import java.util.Set;

public class Maze {
    private Coords maxPos;
    private Coords minPos;
    private double minX, minY;
    private double maxX, maxY;
    private Set<Edges> environment;
    private Set<Edges> obstacle;

    /**
     * contains the coordinate system, including the walls
     * also takes care about displaying the maze and robot movement
     */
    Maze() {
        minX = minY = 0.0;
        maxX = maxY = 30.0;
        environment = new HashSet<>();
        environment.add(new Edges(new Coords(minX, minY), new Coords(maxX, minY)));
        environment.add(new Edges(new Coords(maxX, minY), new Coords(maxX, maxY)));
        environment.add(new Edges(new Coords(maxX, maxY), new Coords(minX, maxY)));
        environment.add(new Edges(new Coords(minX, maxY), new Coords(minX, minY)));

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

        double angle = Math.atan2((-newPos.getY()) - (-oldPos.getY()), newPos.getX() - oldPos.getX());
        double degrees = Math.toDegrees(angle);
        if (degrees < 0)
            degrees += 360;
        else if (degrees > 360)
            degrees -= 360;
        newPos.setAngle(degrees);

        for (Edges wall : environment) {
            //Wall
            double Ax = wall.getFrom().getX();
            double Ay = wall.getFrom().getY();
            double Bx = wall.getTo().getX();
            double By = wall.getTo().getY();

            //Robot movement
            double Cx = oldPos.getX();
            double Cy = oldPos.getY();
            double Dx = newPos.getX();
            double Dy = newPos.getY();

            //calculations of intersection
            double Ex = Bx - Ax; //deltaX of wall
            double Ey = By - Ay; //deltaY of wall
            double Fx = Dx - Cx; //deltaX of robot
            double Fy = Dy - Cy; //deltaY of robot
            double h = ((Ax - Cx) * (-Ey) + (Ay - Cy) * Ex) / (Fx * (-Ey) + Fy * Ex);

            //if h is between 0 and 1, lines are intersecting
            //if h is exact 0 or 1 the lines touch at an end point
            //if h is smaller than 0, the line is behind the given line
            //if h is bigger than 1 the line is in front of the given line
            //if FxFy and PxPy are zero, the lines are parallel
            if (h >= 0.0 && h <= 1.0) {
                newPos.setX(Cx + Fx * h);
                newPos.setY(Cy + Fy * h);
                if (Ax == Bx) { //vertical line
                    System.out.print(" wall vertical");
                    if (Dx - Cx < 0 && Dy - Cy < 0) newPos.setAngle(90);
                    else if (Dx - Cx < 0 && Dy - Cy > 0) newPos.setAngle(270);
                    else if (Dx - Cx > 0 && Dy - Cy > 0) newPos.setAngle(270);
                    else if (Dx - Cx > 0 && Dy - Cy < 0) newPos.setAngle(90);
                    else if (Dy - Cy == 0 && Dy < maxY / 2) newPos.setAngle(270);
                    else if (Dy - Cy == 0 && Dy > maxY / 2) newPos.setAngle(90);
                }
                if (Ay == By) { //horizontal line
                    System.out.print(" wall horizontal");
                    if (Dx - Cx < 0 && Dy - Cy < 0) newPos.setAngle(180);
                    else if (Dx - Cx < 0 && Dy - Cy > 0) newPos.setAngle(180);
                    else if (Dx - Cx > 0 && Dy - Cy > 0) newPos.setAngle(0);
                    else if (Dx - Cx > 0 && Dy - Cy < 0) newPos.setAngle(0);
                    else if (Dx - Cx == 0 && Dx < maxX / 2) newPos.setAngle(0);
                    else if (Dx - Cx == 0 && Dx > maxX / 2) newPos.setAngle(180);
                }
                return newPos;
            }
        }
        for (Edges ob : obstacle) {
            //Wall
            double Ax = ob.getFrom().getX();
            double Ay = ob.getFrom().getY();
            double Bx = ob.getTo().getX();
            double By = ob.getTo().getY();

            //Robot movement
            double Cx = oldPos.getX();
            double Cy = oldPos.getY();
            double Dx = newPos.getX();
            double Dy = newPos.getY();

            //calculations of intersection
            double Ex = Bx - Ax; //deltaX of obstacle
            double Ey = By - Ay; //deltaY of obstacle
            double Fx = Dx - Cx; //deltaX of robot
            double Fy = Dy - Cy; //deltaY of robot
            double h = ((Ax - Cx) * (-Ey) + (Ay - Cy) * Ex) / (Fx * (-Ey) + Fy * Ex);

            //if h is between 0 and 1, lines are intersecting
            //if h is exact 0 or 1 the lines touch at an end point
            //if h is smaller than 0, the line is behind the given line
            //if h is bigger than 1 the line is in front of the given line
            //if FxFy and PxPy are zero, the lines are parallel
            if (h >= 0.0 && h <= 1.0) {
                newPos.setX(Cx + Fx * h);
                newPos.setY(Cy + Fy * h);
                if (Ax == Bx) { //vertical line
                    System.out.print(" obstacle vertical");
                    if (Dx - Cx < 0 && Dy - Cy < 0) newPos.setAngle(90);
                    else if (Dx - Cx < 0 && Dy - Cy > 0) newPos.setAngle(270);
                    else if (Dx - Cx > 0 && Dy - Cy > 0) newPos.setAngle(270);
                    else if (Dx - Cx > 0 && Dy - Cy < 0) newPos.setAngle(90);
                    else if (Dy - Cy == 0 && Dy < maxY / 2) newPos.setAngle(270);
                    else if (Dy - Cy == 0 && Dy > maxY / 2) newPos.setAngle(90);
                }
                if (Ay == By) { //horizontal line
                    System.out.print(" obstacle horizontal");
                    if (Dx - Cx < 0 && Dy - Cy < 0) newPos.setAngle(180);
                    else if (Dx - Cx < 0 && Dy - Cy > 0) newPos.setAngle(180);
                    else if (Dx - Cx > 0 && Dy - Cy > 0) newPos.setAngle(0);
                    else if (Dx - Cx > 0 && Dy - Cy < 0) newPos.setAngle(0);
                    else if (Dx - Cx == 0 && Dx < maxX / 2) newPos.setAngle(0);
                    else if (Dx - Cx == 0 && Dx > maxX / 2) newPos.setAngle(180);
                }
                return newPos;
            }
        }
        return newPos;
    }
    
    public double[] calculateSensorValues() {
    	double sensors[]= new double[12];
    	//TODO implement logic  --> for each sensor either the distance to wall or 0 or -1 when no wall in distance 
    	// distance should be limited
    	return sensors;
    }

    public Coords getMaxPos() {
        return new Coords(maxX, maxX);
    }

    public Coords getMinPos() {
        return new Coords(minX, minX);
    }
    
    public Set<Edges> getEnvironment() {
        return environment;
    }
    
    public Set<Edges> getObstacle() {
        return obstacle;
    }
}
