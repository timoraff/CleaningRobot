import com.sun.javafx.geom.Edge;

import java.util.HashSet;
import java.util.Set;

public class Maze {
    private double minX, minY;
    private double maxX, maxY;
    private static Set<Edges> environment;
//    private Set<Edges> obstacle;
    private static Coords currentRobotsPosition;

    /**
     * contains the coordinate system, including the walls
     * also takes care about displaying the maze and robot movement
     */
    Maze() {
        minX = minY = 0.0;
        maxX = maxY = 30.0;
        environment = new HashSet<>();
        // wall
        environment.add(new Edges(new Coords(minX, minY), new Coords(maxX, minY), "N"));
        environment.add(new Edges(new Coords(maxX, minY), new Coords(maxX, maxY), "E"));
        environment.add(new Edges(new Coords(maxX, maxY), new Coords(minX, maxY), "S"));
        environment.add(new Edges(new Coords(minX, maxY), new Coords(minX, minY), "W"));

        // obstacle
        environment.add(new Edges(new Coords(6.0, 6.0), new Coords(22.0, 6.0), "S"));
        environment.add(new Edges(new Coords(22.0, 6.0), new Coords(22.0, 22.0), "W"));
        environment.add(new Edges(new Coords(22.0, 22.0), new Coords(6.0, 22.0), "N"));
        environment.add(new Edges(new Coords(6.0, 22.0), new Coords(6.0, 6.0), "E"));

        currentRobotsPosition = new Coords(minX, minY);
    }

    public void updatePosition(double x, double y) {
        //check outside of environment
        currentRobotsPosition.setX(x);
        currentRobotsPosition.setY(y);
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
            calculateIntersection(oldPos, newPos, wall, true);
        }
//        for (Edges ob : obstacle) {
//            calculateIntersection(oldPos, newPos, ob, true);
//        }
        currentRobotsPosition = newPos;
        return newPos;
    }

    /**
     * This function calculates the intersection between 2 lines
     * @param robotFrom from coordinates of the robot
     * @param robotTo to coordinates of the robot
     * @param wall wall object (contains from and to as well)
     * @param update indicate whether to update the robots position (true) or not (false)
     *               false is used to calculate the distance to walls and obstacles
     */
    private double calculateIntersection(Coords robotFrom, Coords robotTo, Edges wall, boolean update) {
        //Wall
        double wallP1x = wall.getFrom().getX();
        double wallP1y = wall.getFrom().getY();
        double wallP2x = wall.getTo().getX();
        double wallP2y = wall.getTo().getY();

        //Robot movement
        double robotP1x = robotFrom.getX();
        double robotP1y = robotFrom.getY();
        double robotP2x = robotTo.getX();
        double robotP2y = robotTo.getY();

        //calculations of intersection
        double Ex = Math.abs(wallP2x - wallP1x); //deltaX of obstacle
        double Ey = Math.abs(wallP2y - wallP1y); //deltaY of obstacle
        double Fx = Math.abs(robotP2x - robotP1x); //deltaX of robot
        double Fy = Math.abs(robotP2y - robotP1y); //deltaY of robot
        double h = ((wallP1x - robotP1x) * (-Ey) + (wallP1y - robotP1y) * Ex) / (Fx * (-Ey) + Fy * Ex);

        //if h is between 0 and 1, lines are intersecting
        //if h is exact 0 or 1 the lines touch at an end point
        //if h is smaller than 0, the line is behind the given line
        //if h is bigger than 1 the line is in front of the given line
        //if FxFy and PxPy are zero, the lines are parallel
        if (update && h >= 0.0 && h <= 1.0) {
            robotTo.setX(robotP1x + Fx * h);
            robotTo.setY(robotP1y + Fy * h);
            if (wallP1x == wallP2x) { //vertical line
                if (robotP2x - robotP1x < 0 && robotP2y - robotP1y < 0) robotTo.setAngle(90);
                else if (robotP2x - robotP1x < 0 && robotP2y - robotP1y > 0) robotTo.setAngle(270);
                else if (robotP2x - robotP1x > 0 && robotP2y - robotP1y > 0) robotTo.setAngle(270);
                else if (robotP2x - robotP1x > 0 && robotP2y - robotP1y < 0) robotTo.setAngle(90);
                else if (robotP2y - robotP1y == 0 && robotP2y < maxY / 2) robotTo.setAngle(270);
                else if (robotP2y - robotP1y == 0 && robotP2y > maxY / 2) robotTo.setAngle(90);
            }
            if (wallP1y == wallP2y) { //horizontal line
                if (robotP2x - robotP1x < 0 && robotP2y - robotP1y < 0) robotTo.setAngle(180);
                else if (robotP2x - robotP1x < 0 && robotP2y - robotP1y > 0) robotTo.setAngle(180);
                else if (robotP2x - robotP1x > 0 && robotP2y - robotP1y > 0) robotTo.setAngle(0);
                else if (robotP2x - robotP1x > 0 && robotP2y - robotP1y < 0) robotTo.setAngle(0);
                else if (robotP2x - robotP1x == 0 && robotP2x < maxX / 2) robotTo.setAngle(0);
                else if (robotP2x - robotP1x == 0 && robotP2x > maxX / 2) robotTo.setAngle(180);
            }
        }
        return h;
    }

    public static double[] calculateSensorValues() {
        double sensors[]= new double[15];
        double[] degrees = {0,30,60,90,120,150,180,210,240,270,300,330};
        for(int i = 0; i < degrees.length; i++) {
            sensors[i] = Double.MAX_VALUE;
        }

        double RX = currentRobotsPosition.getX();
        double RY = currentRobotsPosition.getY();

        for(int i = 0; i < degrees.length; i++) {
            for(Edges wall : environment) {
                
                // ray
                double A1;
                double B1 = 0;
                if (degrees[i] == 90||degrees[i] == 270) {
                    A1 = Integer.MAX_VALUE;
                } else if (degrees[i] == 180||degrees[i] == 0) {
                    A1 = 0;
                } else {
                    A1  = Math.tan(Math.toRadians(degrees[i]));
                    B1 = RY - RX * A1;
                }

                // edge
                double A2;
                double B2 = 0;
                if (wall.getTo().getX() == wall.getFrom().getX()) {
                    A2 = Integer.MAX_VALUE;
                } else if (wall.getTo().getY() == wall.getFrom().getY()) {
                    A2 = 0;
                } else {
                    A2 = (wall.getTo().getY() - wall.getFrom().getY()) / (wall.getTo().getX() - wall.getFrom().getX());
                    B2 = wall.getFrom().getY() - wall.getFrom().getX() * A2;
                }

                // in case they are not parralel
                if (A1 != A2) {
                    double x, y;
                    if (A2 == Integer.MAX_VALUE && A1 == 0) {
                        x = wall.getFrom().getX();
                        y = RY;
                    } else if (A1 == Integer.MAX_VALUE && A2 == 0) {
                        x = RY;
                        y = wall.getFrom().getY();
                    } else if (A1 == 0) {
                        y = RY;
                        x = (y - B2) / A2;				
                    } else if (A2 == 0) {
                        y = wall.getFrom().getY();
                        x = (y - B1) / A1;
                    } else if (A1 == Integer.MAX_VALUE) {
                        x = RX;
                        y = x*A2 + B2;	
                    } else if (A2 == Integer.MAX_VALUE) {
                        x = wall.getFrom().getX();
                        y = x*A1 + B1;
                    } else {
                        x = (B2 - B1) / (A1 - A2);
                        y = A1 * x + B1;
                    }

                    double minX = Math.min(wall.getFrom().getX(), wall.getTo().getX());
                    double maxX = Math.max(wall.getFrom().getX(), wall.getTo().getX());
                    double minY = Math.min(wall.getFrom().getY(), wall.getTo().getY());
                    double maxY = Math.min(wall.getFrom().getY(), wall.getTo().getY());
                    if(minX <= x && x <= maxX && minY <= y && y <= maxY) {
                        double distance = Math.sqrt(Math.pow(x - RX, 2) + Math.pow(y - RY, 2));
                        if (degrees[i] == 60) {
                            System.out.println("dis: " + distance + " x: " + x + " y: " + y);
                        }

                        if (0.0 < degrees[i] && 90.0 > degrees[i]) {
                            if (x > RX && y > RY) {
                                sensors[i] = Math.min(sensors[i], distance);
                            }
                        } else if (90.0 < degrees[i] && 180.0 > degrees[i]) {
                            if (x < RX && y > RY) {
                                sensors[i] = Math.min(sensors[i], distance);
                            }

                        } else if (180.0 < degrees[i] && 270.0 > degrees[i]) {
                            if (x < RX && y < RY) {
                                sensors[i] = Math.min(sensors[i], distance);
                            }

                        } else if (270.0 < degrees[i] && 360.0 > degrees[i]) {
                            if (x > RX && y < RY) {
                                sensors[i] = Math.min(sensors[i], distance);
                            }

                        } else if(degrees[i] == 0) {
                            if (x > RX && y == RY) {
                                sensors[i] = Math.min(sensors[i], distance);
                            }
                        } else if(degrees[i] == 90) {
                            if (x == RX && y > RY) {
                                sensors[i] = Math.min(sensors[i], distance);
                            }
                        } else if(degrees[i] == 180) {
                            if (x < RX && y == RY) {
                                sensors[i] = Math.min(sensors[i], distance);
                            }
                        } else {
                            if (x == RX && y < RY) {
                                sensors[i] = Math.min(sensors[i], distance);
                            }
                        }
                    }
                }
            }
        }
        return sensors;
    }

    public Set<Edges> getEnvironment() {
        return environment;
    }
    
//    public Set<Edges> getObstacle() {
//        return obstacle;
//    }

    public Coords getCurrentRobotsPosition() {
        return currentRobotsPosition;
    }
}
