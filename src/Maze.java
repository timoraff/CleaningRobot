import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Julian Gorfer i6168920
 * @author Pit Schneider i6173083
 */
public class Maze {
    private static Set<Edges> environment;
    private static Set<Coords> beacons;
    private double l;
    private double minY, maxY;
    private double minX, maxX;
    private Coords robotsCurrentPosition;
    private double rangeOfBeacons;

    /**
     * Constructor for the maze.
     * sets the minimum and maximum coordinates of the environment
     * it defines obstacles
     * only horizontal and vertical obstacles until now
     */
    Maze(double minX, double minY, double maxX, double maxY, double lengthOfRobot, double rangeOfBeacons) {
        this.minX = minX;
        this.minY = minY;
        this.maxX =maxX;
        this.maxY = maxY;
        this.l = lengthOfRobot;
        this.rangeOfBeacons = rangeOfBeacons;

        environment = new HashSet<>();
        beacons = new HashSet<>();

        // outer walls
        environment.add(new Edges(new Coords(minX, minY), new Coords(maxX, minY))); //bottom line
        environment.add(new Edges(new Coords(maxX, minY), new Coords(maxX, maxY))); //top line
        environment.add(new Edges(new Coords(maxX, maxY), new Coords(minX, maxY))); //right line
        environment.add(new Edges(new Coords(minX, maxY), new Coords(minX, minY))); //left line

        // obstacle in the middle of map
        environment.add(new Edges(new Coords(10, 8), new Coords(30, 8)));
        environment.add(new Edges(new Coords(10, 8), new Coords(10, 11)));
        environment.add(new Edges(new Coords(30, 8), new Coords(30, 11)));
        environment.add(new Edges(new Coords(10, 22), new Coords(30, 22)));
        environment.add(new Edges(new Coords(10, 22), new Coords(10, 19)));
        environment.add(new Edges(new Coords(30, 22), new Coords(30, 19)));

        //beacons for the walls
        beacons.add(new Coords(minX, minY));
        beacons.add(new Coords(minX, maxY / 3));
        beacons.add(new Coords(minX, 2 * maxY / 3));
        beacons.add(new Coords(minX, maxY));
        beacons.add(new Coords(maxX, minY));
        beacons.add(new Coords(maxX / 3, minY));
        beacons.add(new Coords(2 * maxX / 3, minY));
        beacons.add(new Coords(maxX, maxY));
        beacons.add(new Coords(maxX, maxY / 3));
        beacons.add(new Coords(maxX, 2 * maxY / 3));
        beacons.add(new Coords(maxX / 3, maxY));
        beacons.add(new Coords(2 * maxX / 3, maxY));

        beacons.add(new Coords(10, 8));
        beacons.add(new Coords(20, 8));
        beacons.add(new Coords(30, 8));

        beacons.add(new Coords(10, 22));
        beacons.add(new Coords(20, 22));
        beacons.add(new Coords(30, 22));
    }

    /**
     * check for collisions between the robot and obstacles (walls)
     * @return  true if collision is detected,
     *          false if no collision is detected
     */
    public boolean checkForCollision() {
        Coords position = robotsCurrentPosition;

        for (Edges edge : environment) {
                        
            // get edge equation
            double m;
            double c = 0;
            if (edge.getTo().getX() == edge.getFrom().getX()) { //vertical edge
                m = Integer.MAX_VALUE;
            } else if (edge.getTo().getY() == edge.getFrom().getY()) { //horizontal edge
                m = 0;
                c = edge.getFrom().getY();
            } else { // diagonal edge
                m = (edge.getTo().getY() - edge.getFrom().getY()) / (edge.getTo().getX() - edge.getFrom().getX());
                c = edge.getFrom().getY() - edge.getFrom().getX() * m;
            }
            
            // get robot equation
            double r = l/2.0 + 0.0 ;
            double p = position.getX();
            double q = position.getY();
            
            // calculate delta - calculating b^2-4*a*c
            double delta;
            if (m == Integer.MAX_VALUE) { // special delta for vertical line
                delta = Math.pow(-2*q,2) - 4 * (Math.pow(q,2)-Math.pow(r,2)+Math.pow(edge.getTo().getX()-p,2));			
            } else { // regular case
                delta = Math.pow(2*(m*c-m*q-p),2) - 4 * (Math.pow(m,2)+1) * (Math.pow(q,2)-Math.pow(r,2)+Math.pow(p,2)-2*c*q+Math.pow(c,2));			
            }
            
            // line is tangent to circle or intersecting with 2 distinct points
            if (delta >= 0) {
                
                double edgeMinX = Math.min(edge.getFrom().getX(), edge.getTo().getX());
                double edgeMaxX = Math.max(edge.getFrom().getX(), edge.getTo().getX());
                double edgeMinY = Math.min(edge.getFrom().getY(), edge.getTo().getY());
                double edgeMaxY = Math.max(edge.getFrom().getY(), edge.getTo().getY());
                
                double robotMinX = position.getX()-l/2.0;
                double robotMaxX = position.getX()+l/2.0;
                double robotMinY = position.getY()-l/2.0;
                double robotMaxY = position.getY()+l/2.0;
                
                // checking whether collision is with actual edge of map (not infinite line described by equation)
                if (!(robotMaxX < edgeMinX||robotMinX > edgeMaxX||robotMaxY < edgeMinY||robotMinY > edgeMaxY)) {
                    return true;
                }
            }
        }
        
        // no intersection
        return false;
    }

    /**
     * Cast a ray from a given position with a given angle
     * @param pos Coords position the ray should be casted
     * @param angle angle the ray should be casted
     * @return the distance to the closest intersection between the ray and an object in the environment
     */
    private double castRay(Coords pos, double angle) {

        //preprocess angle if its smaller than 0 or bigger than 360
        if (angle < 0) {
            angle = (-angle) % 360;
        } else {
            angle = angle % 360;
        }

        double posX = pos.getX();
        double posY = pos.getY();
        
        double minDistance = Integer.MAX_VALUE;

        //loop over all edges in the environment
        for (Edges edge : environment) {

            // ray
            double rayA;
            double rayB = 0;
            if (angle == 90 || angle == 270) { //vertical ray
                rayA = Integer.MAX_VALUE;
            } else if (angle == 180 || angle == 0) { //horizontal ray
                rayA = 0;
            } else {
                rayA = Math.tan(Math.toRadians(angle));
                rayB = posY - posX * rayA;
            }
            // edge
            double edgeA;
            double edgeB = 0;
            if (edge.getTo().getX() == edge.getFrom().getX()) { //vertical edge
                edgeA = Integer.MAX_VALUE;
            } else if (edge.getTo().getY() == edge.getFrom().getY()) { //horizontal edge
                edgeA = 0;
            } else {
                edgeA = (edge.getTo().getY() - edge.getFrom().getY()) / (edge.getTo().getX() - edge.getFrom().getX());
                edgeB = edge.getFrom().getY() - edge.getFrom().getX() * edgeA;
            }

            // in case ray and edge are not parallel
            if (rayA != edgeA) {
                double x, y;
                if (edgeA == Integer.MAX_VALUE && rayA == 0) { //horizontal edge and vertical ray
                    x = edge.getFrom().getX();
                    y = posY;
                } else if (rayA == Integer.MAX_VALUE && edgeA == 0) { //vertical edge and horizontal ray
                    x = posX;
                    y = edge.getFrom().getY();
                } else if (rayA == 0) { //vertical ray
                    y = posY;
                    x = (y - edgeB) / edgeA;
                } else if (edgeA == 0) { //vertical edge
                    y = edge.getFrom().getY();
                    x = (y - rayB) / rayA;
                } else if (rayA == Integer.MAX_VALUE) { //horizontal ray
                    x = posX;
                    y = x * edgeA + edgeB;
                } else if (edgeA == Integer.MAX_VALUE) { //horizontal edge
                    x = edge.getFrom().getX();
                    y = x * rayA + rayB;
                } else {
                    x = (edgeB - rayB) / (rayA - edgeA);
                    y = rayA * x + rayB;
                }

                //set min and max coords depending on edges
                double minX = Math.min(edge.getFrom().getX(), edge.getTo().getX());
                double maxX = Math.max(edge.getFrom().getX(), edge.getTo().getX());
                double minY = Math.min(edge.getFrom().getY(), edge.getTo().getY());
                double maxY = Math.max(edge.getFrom().getY(), edge.getTo().getY());

                //if our calculated x is between min and max X/Y
                if (minX <= x && x <= maxX && minY <= y && y <= maxY) {
                    double distance = Math.sqrt(Math.pow(x - posX, 2) + Math.pow(y - posY, 2));
                    if (0.0 < angle && 90.0 > angle) { //first quarter
                        if (x > posX && y > posY) { //if x is bigger than current pos x and y bigger than current pos y
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (90.0 < angle && 180.0 > angle) { //second quarter
                        if (x < posX && y > posY) { //if x is smaller than current pos x and y bigger than current pos y
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (180.0 < angle && 270.0 > angle) { //third quarter
                        if (x < posX && y < posY) { //if x is smaller than current pos x and y smaller than current pos y
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (270.0 < angle && 360.0 > angle) { //fourth quarter
                        if (x > posX && y < posY) { //if x is bigger than current pos x and y smaller than current pos y
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (angle == 0) { //horizontal right
                        if (x > posX && y == posY) { //if x is bigger than current pos x and y equal to current pos y
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (angle == 90) { //veritcal up
                        if (x == posX && y > posY) { //if x is equal to current pos x and y bigger than current pos y
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (angle == 180) { //horizontal left
                        if (x < posX && y == posY) { //if x is smaller than current pos x and y equal to current pos y
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (angle == 270) { //vertical down
                        if (x == posX && y < posY) { //if x is equal to current pos x and y smaller than current pos y
                            minDistance = Math.min(minDistance, distance);
                        }
                    }
                }
            }
        }
        return minDistance;
    }

    /**
     * this method calculates the distances of the robots position to the beacons in sight and range of the robot and returns a list of beacon coordinates
     * @return ArrayList of coordinates of the beacons in range and sight of the robot
     *
     */
    public ArrayList<Coords> beaconsInRange () {
        Coords robotPos = robotsCurrentPosition;
        // calculate first the number of beacons in range
        ArrayList<Coords> beaconsInRange = new ArrayList<>();
        for(Coords beacon : beacons){
            double d = Math.sqrt(Math.pow(beacon.getX()-robotPos.getX(),2) + Math.pow(beacon.getY() - robotPos.getY(), 2));
            if(d <= rangeOfBeacons) {
                double angle;
                
                if (robotPos.getX()-beacon.getX() == 0) {
                    angle = 90;
                } else {
                    angle = Math.toDegrees(Math.atan((robotPos.getY()-beacon.getY())/(robotPos.getX()-beacon.getX())));
                }
                if (robotPos.getX() > beacon.getX() && robotPos.getY() <= beacon.getY()) {
                    angle += 180;
                } else if (robotPos.getX() >= beacon.getX() && robotPos.getY() > beacon.getY()) {
                    angle += 180;
                } else if (robotPos.getX() < beacon.getX() && robotPos.getY() > beacon.getY()) {
                    angle += 360;
                }
                d = d - (d % 0.0001);
                double rayDistance = castRay(robotPos, angle);
                rayDistance = rayDistance - (rayDistance % 0.0001);

                if(rayDistance >= d) {
                    beacon.setAngle(d);
                    //add the beacons to the list
                    beaconsInRange.add(beacon);
                }
            }
        }
        return beaconsInRange;
    }

    /**
     * getter for the environment
     * @return set of edges
     */
    public Set<Edges> getEnvironment() {
        return environment;
    }

    /**
     *  getter maxY
     * @return maximum Y coordinate
     */
	public double getMaxY() {
		return maxY;
	}

    /**
     *  getter maxX
     * @return maximum X coordinate
     */
	public double getMaxX() {
		return maxX;
	}

    /**
     *  getter minY
     * @return minimum Y coordinate
     */
    public double getMinY() {
        return minY;
    }

    /**
     *  getter minX
     * @return minimum X coordinate
     */
    public double getMinX() {
        return minX;
    }

    /**
     * setter for the robots position
     * @param robotsCurrentPosition pos of robot
     */
    public void setRobotsCurrentPosition(Coords robotsCurrentPosition) {
        this.robotsCurrentPosition = robotsCurrentPosition;
    }

    /**
     * getter for the robots position
     * @return robotsCurrentPosition pos of robot
     */
    public Coords getRobotsCurrentPosition() {
        return robotsCurrentPosition;
    }



    public static Set<Coords> getBeacons() {
        return beacons;
    }
}