import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Julian Gorfer i6168920
 * @author Pit Schneider i6173083
 */
public class Maze {
    private static Set<Edges> environment;
    private static Set<Coords> beacons;
    private int l = 0;
    private double minY, maxY;
    private double minX, maxX;
    private Coords robotsCurrentPosition;
    private double rangeOfBeacons = 100;

    /**
     * Constructor for the maze.
     * sets the minimum and maximum coordinates of the environment
     * it defines obstacles
     * only horizontal and vertical obstacles until now
     */
    Maze() {
        minX = minY = 0.0;
        maxX = 40;
        maxY = 20.0;
        environment = new HashSet<>();
        beacons = new HashSet<>();

        // wall
        environment.add(new Edges(new Coords(minX, minY), new Coords(maxX, minY))); //bottom line
        environment.add(new Edges(new Coords(maxX, minY), new Coords(maxX, maxY))); //top line
        environment.add(new Edges(new Coords(maxX, maxY), new Coords(minX, maxY))); //right line
        environment.add(new Edges(new Coords(minX, maxY), new Coords(minX, minY))); //left line

        //beacons for the walls
        beacons.add(new Coords(minX, minY));
        beacons.add(new Coords(maxX, minY));
        beacons.add(new Coords(minX, maxY));
        beacons.add(new Coords(maxX, maxY));

        // obstacle
//        environment.add(new Edges(new Coords(minX + (maxX / 5), minY + (maxY / 5)), new Coords(maxX - (maxX / 5), minY + (maxY / 5)))); //bottom line
//        environment.add(new Edges(new Coords(maxX - (maxX / 5), minY + (maxY / 5)), new Coords(maxX - (maxX / 5), maxY - (maxY / 5)))); //right line
//        environment.add(new Edges(new Coords(maxX - (maxX / 5), maxY - (maxY / 5)), new Coords(minX + (maxX / 5), maxY - (maxY / 5)))); //top line
//        environment.add(new Edges(new Coords(minX + (maxX / 5), minY + (maxY / 5)), new Coords(minX + (maxX / 5), maxY - (maxY / 5)))); //left line
    }

    /**
     * check for collisions between the robot and obstacles (walls)
     * @param oldPos old position of the robot
     * @param newPos new position of the robot
     * @return  true if collision is detected,
     *          false if no collision is detected
     */
    public boolean checkForCollision(Coords oldPos, Coords newPos) {
        double angle = Math.atan2(newPos.getY() - oldPos.getY(), newPos.getX() - oldPos.getX());
        double degrees = Math.toDegrees(angle);
        if (degrees < 0)
            degrees += 360;
        else if (degrees > 360)
            degrees -= 360;

        Coords posLeft = new Coords(oldPos.getX() + l/2 * Math.cos(Math.toRadians(degrees + 90)), oldPos.getY() + l/2 * Math.sin(Math.toRadians(degrees + 90)));
        Coords posRight = new Coords(oldPos.getX() - l/2 * Math.cos(Math.toRadians(degrees + 90)), oldPos.getY() - l/2 * Math.sin(Math.toRadians(degrees + 90)));
        double distance = Math.sqrt(Math.pow(newPos.getX() - oldPos.getX(), 2) + Math.pow(newPos.getY() - oldPos.getY(), 2));
        return distance > castRay(posLeft, degrees) || distance > castRay(posRight, degrees);
    }

    /**
     * Calculate the distance between the robot and walls in 12 directions depending on the current robots view angle
     * @param currentPosition pos of the robot
     * @return array of measurements
     */
    public double[] calculateSensorValues(Coords currentPosition) {
        double sensors[]= new double[12];
        double[] degrees = {
                currentPosition.getAngle(),
                currentPosition.getAngle() + 30,
                currentPosition.getAngle() + 60,
                currentPosition.getAngle() + 90,
                currentPosition.getAngle() + 120,
                currentPosition.getAngle() + 150,
                currentPosition.getAngle() + 180,
                currentPosition.getAngle() + 210,
                currentPosition.getAngle() + 240,
                currentPosition.getAngle() + 270,
                currentPosition.getAngle() + 300,
                currentPosition.getAngle() + 330
        };

        // cast a ray in 12 directions and save the distance to the nearest wall
        for(int i = 0; i < degrees.length; i++) {
            sensors[i] = castRay(currentPosition, degrees[i]);
        }

//        //get the maximum distance from the sensors
//        //used to calcualte the noise afterwards
//        double maxDistance = 0;
//        for (double distance : sensors) {
//            if(distance != Integer.MAX_VALUE)
//                maxDistance = Math.max(distance, maxDistance);
//        }
//
//        //add noise to the distance sensors
//        double percent = 10;
//        for (double distance : sensors) {
//            //generate random value between 0 and 1
//            double rand = Math.random();
//            double noisyDistance;
//            //if random value is smaller than 0.5 decrease distance else increase
//            if (rand < 0.5) {
//                //the current distance gets decreased by 10 percent of the ratio of the distance to the max distance mu;tiplied by the random factor
//                noisyDistance = distance - distance * (distance/maxDistance) * percent/100 * rand;
//            } else {
//                //the current distance gets increased by 10 percent of the ratio of the distance to the max distance mu;tiplied by the random factor
//                noisyDistance = distance + distance * (distance/maxDistance) * percent/100 * rand;
//            }
//            //System.out.println("Distance: " + distance + " Noisy distance: " + noisyDistance + " Random factor: " + rand);
//        }
        return sensors;
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
     * this method calculates the exact position of the robot based on the beacons intersection point
     * @return coordinates of the intersection of the beacons (circles)
     *          if there are at least 3 beacons in range, return a triangulation of the 3 closest circles
     *          if there are at least 2 beacons in range, return the interseciton point of the 2 closest circles
     *          if there are at most 1 beacon in range return null (TBD)
     */
    public Coords intersectionPointOfBeacons(Coords RobotPos) {
    // public Coords intersectionPointOfBeacons() {

        // calculate first the number of beacons in range
        int numberOfBeaconsInRange = 0;
        Coords[] beaconsInRange = new Coords[beacons.size()];
        for(Coords beacon : beacons){
            double d = Math.sqrt(Math.pow(beacon.getX()-RobotPos.getX(),2) + Math.pow(beacon.getY() - RobotPos.getY(), 2));
            // double d = Math.sqrt(Math.pow(coords.getX()-robotsCurrentPosition.getX(),2) + Math.pow(coords.getY() - robotsCurrentPosition.getY(), 2));
            if(d <= rangeOfBeacons) {
                double angle = Math.toDegrees(Math.atan2(beacon.getY() - RobotPos.getY(), beacon.getX() - RobotPos.getX()));
                // double angle = Math.toDegrees(Math.atan2(beacon.getY() - robotsCurrentPosition.getY(), beacon.getX() - robotsCurrentPosition.getX()));
                if (angle < 0) {
                    angle = (-angle) % 360;
                } else {
                    angle = angle % 360;
                }
                if(castRay(RobotPos, angle) >= d) {
                // if(castRay(robotsCurrentPosition, angle) >= d) {
                    //add the beacons to the list
                    beaconsInRange[numberOfBeaconsInRange] = beacon;
                    numberOfBeaconsInRange++;
                }
            }
        }

        //need at least 2 beacons to calculate position of robot
        if (numberOfBeaconsInRange > 1) {
            double x1 = beaconsInRange[0].getX();
            double x2 = beaconsInRange[1].getX();
            double y1 = beaconsInRange[0].getY();
            double y2 = beaconsInRange[1].getY();
            // double r1 = Math.sqrt(Math.pow(beaconsInRange[0].getX()-robotsCurrentPosition.getX(),2) + Math.pow(beaconsInRange[0].getY() - robotsCurrentPosition.getY(), 2));
            // double r2 = Math.sqrt(Math.pow(beaconsInRange[1].getX()-robotsCurrentPosition.getX(),2) + Math.pow(beaconsInRange[1].getY() - robotsCurrentPosition.getY(), 2));
            double r1 = Math.sqrt(Math.pow(beaconsInRange[0].getX()-RobotPos.getX(),2) + Math.pow(beaconsInRange[0].getY() - RobotPos.getY(), 2));
            double r2 = Math.sqrt(Math.pow(beaconsInRange[1].getX()-RobotPos.getX(),2) + Math.pow(beaconsInRange[1].getY() - RobotPos.getY(), 2));
            //if at least 3 beacons in range (easier to calculate exact position)
            if(numberOfBeaconsInRange >= 3) {
                double x3 = beaconsInRange[2].getX();
                double y3 = beaconsInRange[2].getY();
                double r3 = Math.sqrt(Math.pow(beaconsInRange[2].getX()-RobotPos.getX(),2) + Math.pow(beaconsInRange[2].getY() - RobotPos.getY(), 2));
                // double r3 = Math.sqrt(Math.pow(beaconsInRange[2].getX()-robotsCurrentPosition.getX(),2) + Math.pow(beaconsInRange[2].getY() - robotsCurrentPosition.getY(), 2));

                double x = ((y2 - y3) * ((Math.pow(y2, 2) - Math.pow(y1, 2)) + (Math.pow(x2, 2) - Math.pow(x1, 2)) + (Math.pow(r1, 2) - Math.pow(r2, 2))) - (y1 - y2) * ((Math.pow(y3, 2) - Math.pow(y2, 2)) + (Math.pow(x3, 2) - Math.pow(x2, 2)) + (Math.pow(r2, 2) - Math.pow(r3, 2)))) / (2 * ((x1 - x2) * (y2 - y3) - (x2 - x3) * (y1 - y2)));
                double y = ((x2 - x3) * ((Math.pow(x2, 2) - Math.pow(x1, 2)) + (Math.pow(y2, 2) - Math.pow(y1, 2)) + (Math.pow(r1, 2) - Math.pow(r2, 2))) - (x1 - x2) * ((Math.pow(x3, 2) - Math.pow(x2, 2)) + (Math.pow(y3, 2) - Math.pow(y2, 2)) + (Math.pow(r2, 2) - Math.pow(r3, 2)))) / (2 * ((y1 - y2) * (x2 - x3) - (y2 - y3) * (x1 - x2)));
                return new Coords(x, y);
            } else {
                //if there are at most 2 beacons (trickier to calculate position because robot could be in 2 different locations)
                double distanceBetweenBeacons = Math.sqrt(Math.pow(beaconsInRange[0].getX() - beaconsInRange[1].getX(), 2) + Math.pow(beaconsInRange[0].getY() - beaconsInRange[1].getY(), 2));
                //in case distance is bigger than the 2 radius of the circles
                //or one circle is inside another one
                //or one circle lays on top of another with the same radius
                if (distanceBetweenBeacons > (r1 + r2) || distanceBetweenBeacons < Math.abs(r1 - r2) || (distanceBetweenBeacons == 0 && r1 == r2))
                    return null;
                else {
                    double a = (Math.pow(r1, 2) - Math.pow(r2, 2) + Math.pow(distanceBetweenBeacons, 2)) / (2 * distanceBetweenBeacons);
                    double h = Math.sqrt(Math.pow(r1, 2) - Math.pow(a, 2));
                    double tmpX = x1 + a * Math.abs(x2 - x1) / distanceBetweenBeacons;
                    double tmpY = y1 + a * Math.abs(y2 - y1) / distanceBetweenBeacons;
                    double x, y;
                    //if the x coordinates of the robot are bigger than the x of the center line of the 2 circles intersecting
                    // if (robotsCurrentPosition.getX() > tmpX) {
                    if (RobotPos.getX() > tmpX) {
                        x = tmpX + h * Math.abs(y2 - y1) / distanceBetweenBeacons;
                    } else {
                        x = tmpX - h * Math.abs(y2 - y1) / distanceBetweenBeacons;
                    }

                    //if the y coordinates of the robot are bigger than the y of the center line of the 2 circles intersecting
                    // if (robotsCurrentPosition.getY() > tmpY) {
                    if (RobotPos.getY() > tmpY) {
                        y = tmpY + h * Math.abs(x2 - x1) / distanceBetweenBeacons;
                    } else {
                        y = tmpY - h * Math.abs(x2 - x1) / distanceBetweenBeacons;
                    }
                    return new Coords(x, y);
                }
            }
        }else {
            return null;
        }
    }

    /**
     * set the distance between the 2 wheels of the robot
     * @param l distance
     */
    public void setLength(int l) {
        this.l = l;
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
}
