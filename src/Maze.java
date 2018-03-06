import com.sun.javafx.geom.Edge;

import java.util.HashSet;
import java.util.Set;

public class Maze {
    private static Set<Edges> environment;
//    private Set<Edges> obstacle;
//    private static Coords currentRobotsPosition;
    private int l = 0;

    /**
     * contains the coordinate system, including the walls
     * also takes care about displaying the maze and robot movement
     */
    Maze() {
        double minY;
        double minX = minY = 0.0;
        double maxY;
        double maxX = maxY = 30.0;
        environment = new HashSet<>();
        // wall
        environment.add(new Edges(new Coords(minX, minY), new Coords(maxX, minY), 1)); //bottom line
        environment.add(new Edges(new Coords(maxX, minY), new Coords(maxX, maxY), -1)); //top line
        environment.add(new Edges(new Coords(maxX, maxY), new Coords(minX, maxY), -1)); //right line
        environment.add(new Edges(new Coords(minX, maxY), new Coords(minX, minY), 1)); //left line

        // obstacle
        environment.add(new Edges(new Coords(minX + 6, minY + 6), new Coords(maxX - 6, minY + 6), -1)); //bottom line
        environment.add(new Edges(new Coords(maxX - 6, minY + 6), new Coords(maxX - 6, maxY - 6), 1)); //left line
        environment.add(new Edges(new Coords(maxX - 6, maxY - 6), new Coords(minX + 6, maxY - 6), 1)); //top line
        environment.add(new Edges(new Coords(minX + 6, maxY - 6), new Coords(minX + 6, minY + 6), -1)); //right line

//        currentRobotsPosition = new Coords(minX + 1, minY + 1);
    }

//    public void updatePosition(double x, double y) {
//        //check outside of environment
//        currentRobotsPosition.setX(x);
//        currentRobotsPosition.setY(y);
//    }

    public boolean getCorrectPosition(Coords oldPos, Coords newPos) {
        double angle = Math.atan2(newPos.getY() - oldPos.getY(), newPos.getX() - oldPos.getX());
        double degrees = Math.toDegrees(angle);
        if (degrees < 0)
            degrees += 360;
        else if (degrees > 360)
            degrees -= 360;

        Coords oldPosLeft = new Coords(oldPos.getX() + l/2 * Math.cos(Math.toRadians(degrees + 90)), oldPos.getY() + l/2 * Math.sin(Math.toRadians(degrees + 90)));
        Coords newPosLeft = new Coords(newPos.getX() + l/2 * Math.cos(Math.toRadians(degrees + 90)), newPos.getY() + l/2 * Math.sin(Math.toRadians(degrees + 90)));
        Coords oldPosRight = new Coords(oldPos.getX() - l/2 * Math.cos(Math.toRadians(degrees + 90)), oldPos.getY() - l/2 * Math.sin(Math.toRadians(degrees + 90)));
        Coords newPosRight = new Coords(newPos.getX() - l/2 * Math.cos(Math.toRadians(degrees + 90)), newPos.getY() - l/2 * Math.sin(Math.toRadians(degrees + 90)));
        for(Edges wall : environment) {
            // ray
            double ARobotLeft, ARobotRight;
            double BRobotLeft = 0, BRobotRight = 0;
            if (degrees == 90 || degrees == 270) {
                ARobotLeft = Integer.MAX_VALUE;
            } else if (degrees == 180 || degrees == 0) {
                ARobotLeft = 0;
            } else {
                ARobotLeft = (newPosLeft.getY() - oldPosLeft.getY()) / (newPosLeft.getX() - oldPosLeft.getX());
                BRobotLeft = oldPosLeft.getY() - oldPosLeft.getX() * ARobotLeft;
            }

            if (degrees == 90 || degrees == 270) {
                ARobotRight = Integer.MAX_VALUE;
            } else if (degrees == 180 || degrees == 0) {
                ARobotRight = 0;
            } else {
                ARobotRight = (newPosRight.getY() - oldPosRight.getY()) / (newPosRight.getX() - oldPosRight.getX());
                BRobotRight = oldPosRight.getY() - oldPosRight.getX() * ARobotRight;
            }

            // edge
            double AWall;
            double BWall = 0;
            if (wall.getTo().getX() == wall.getFrom().getX()) {
                AWall = Integer.MAX_VALUE;
            } else if (wall.getTo().getY() == wall.getFrom().getY()) {
                AWall = 0;
            } else {
                AWall = (wall.getTo().getY() - wall.getFrom().getY()) / (wall.getTo().getX() - wall.getFrom().getX());
                BWall = wall.getFrom().getY() - wall.getFrom().getX() * AWall;
            }

            // in case they are not parallel
            double xLeft, yLeft, xRight, yRight;
            if (ARobotLeft != AWall && ARobotRight != AWall) {
                if (AWall == Integer.MAX_VALUE && ARobotLeft == 0) {
                    xLeft = wall.getFrom().getX();
                    yLeft = oldPos.getY();
                } else if (ARobotLeft == Integer.MAX_VALUE && AWall == 0) {
                    xLeft = oldPos.getY();
                    yLeft = wall.getFrom().getY();
                } else if (ARobotLeft == 0) {
                    yLeft = oldPos.getY();
                    xLeft = (yLeft - BWall) / AWall;
                } else if (AWall == 0) {
                    yLeft = wall.getFrom().getY();
                    xLeft = (yLeft - BRobotLeft) / ARobotLeft;
                } else if (ARobotLeft == Integer.MAX_VALUE) {
                    xLeft = oldPos.getX();
                    yLeft = xLeft*AWall + BWall;
                } else if (AWall == Integer.MAX_VALUE) {
                    xLeft = wall.getFrom().getX();
                    yLeft = xLeft*ARobotLeft + BRobotLeft;
                } else {
                    xLeft = (BWall - BRobotLeft) / (ARobotLeft - AWall);
                    yLeft = ARobotLeft * xLeft + BRobotLeft;
                }

                if (AWall == Integer.MAX_VALUE && ARobotRight == 0) {
                    xRight = wall.getFrom().getX();
                    yRight = oldPos.getY();
                } else if (ARobotRight == Integer.MAX_VALUE && AWall == 0) {
                    xRight = oldPos.getY();
                    yRight = wall.getFrom().getY();
                } else if (ARobotRight == 0) {
                    yRight = oldPos.getY();
                    xRight = (yRight - BWall) / AWall;
                } else if (AWall == 0) {
                    yRight = wall.getFrom().getY();
                    xRight = (yRight - BRobotRight) / ARobotRight;
                } else if (ARobotRight == Integer.MAX_VALUE) {
                    xRight = oldPos.getX();
                    yRight = xRight * AWall + BWall;
                } else if (AWall == Integer.MAX_VALUE) {
                    xRight = wall.getFrom().getX();
                    yRight = xRight * ARobotRight + BRobotRight;
                } else {
                    xRight = (BWall - BRobotRight) / (ARobotRight - AWall);
                    yRight = ARobotRight * xRight + BRobotRight;
                }

                double minX = Math.min(wall.getFrom().getX(), wall.getTo().getX());
                double maxX = Math.max(wall.getFrom().getX(), wall.getTo().getX());
                double minY = Math.min(wall.getFrom().getY(), wall.getTo().getY());
                double maxY = Math.min(wall.getFrom().getY(), wall.getTo().getY());

                if(minX <= xLeft && xLeft <= maxX && minY <= yLeft && yLeft <= maxY) {
                    if (0.0 < degrees && 90.0 > degrees) {
                        if (xLeft > oldPosLeft.getX() && yLeft > oldPosLeft.getY()) {
                            return false;
                        }
                    } else if (90.0 < degrees && 180.0 > degrees) {
                        if (xLeft < oldPosLeft.getX() && yLeft > oldPosLeft.getY()) {
                            return false;
                        }
                    } else if (180.0 < degrees && 270.0 > degrees) {
                        if (xLeft < oldPosLeft.getX() && yLeft < oldPosLeft.getY()) {
                            return false;
                        }
                    } else if (270.0 < degrees && 360.0 > degrees) {
                        if (xLeft > oldPosLeft.getX() && yLeft < oldPosLeft.getY()) {
                            return false;
                        }
                    } else if(degrees == 0) {
                        if (xLeft > oldPosLeft.getX() && yLeft == oldPosLeft.getY()) {
                            return false;
                        }
                    } else if(degrees == 90) {
                        if (xLeft == oldPosLeft.getX() && yLeft > oldPosLeft.getY()) {
                            return false;
                        }
                    } else if(degrees == 180) {
                        if (xLeft < oldPosLeft.getX() && yLeft == oldPosLeft.getY()) {
                            return false;
                        }
                    } else {
                        if (xLeft == oldPosLeft.getX() && yLeft < oldPosLeft.getY()) {
                            return false;
                        }
                    }
                }

                if(minX <= xRight && xRight <= maxX && minY <= yRight && yRight <= maxY) {
                    if (0.0 < degrees && 90.0 > degrees) {
                        if (xRight > oldPosRight.getX() && yRight > oldPosRight.getY()) {
                            return false;
                        }
                    } else if (90.0 < degrees && 180.0 > degrees) {
                        if (xRight < oldPosRight.getX() && yRight > oldPosRight.getY()) {
                            return false;
                        }
                    } else if (180.0 < degrees && 270.0 > degrees) {
                        if (xRight < oldPosRight.getX() && yRight < oldPosRight.getY()) {
                            return false;
                        }
                    } else if (270.0 < degrees && 360.0 > degrees) {
                        if (xRight > oldPosRight.getX() && yRight < oldPosRight.getY()) {
                            return false;
                        }
                    } else if(degrees == 0) {
                        if (xRight > oldPosRight.getX() && yRight == oldPosRight.getY()) {
                            return false;
                        }
                    } else if(degrees == 90) {
                        if (xRight == oldPosRight.getX() && yRight > oldPosRight.getY()) {
                            return false;
                        }
                    } else if(degrees == 180) {
                        if (xRight < oldPosRight.getX() && yRight == oldPosRight.getY()) {
                            return false;
                        }
                    } else {
                        if (xRight == oldPosRight.getX() && yRight < oldPosRight.getY()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public double[] calculateSensorValues(Coords currentPosition) {
        double sensors[]= new double[15];
        double[] degrees = {0,30,60,90,120,150,180,210,240,270,300,330};
        for(int i = 0; i < degrees.length; i++) {
            sensors[i] = Double.MAX_VALUE;
        }

        double RX = currentPosition.getX();
        double RY = currentPosition.getY();
//        double view = currentRobotsPosition.getAngle();

        for(int i = 0; i < degrees.length; i++) {
            for(Edges wall : environment) {
                
                // ray
                double ARobot;
                double BRobot = 0;
                if (degrees[i] == 90||degrees[i] == 270) {
                    ARobot = Integer.MAX_VALUE;
                } else if (degrees[i] == 180||degrees[i] == 0) {
                    ARobot = 0;
                } else {
                    ARobot  = Math.tan(Math.toRadians(degrees[i]));
                    BRobot = RY - RX * ARobot;
                }

                // edge
                double AWall;
                double BWall = 0;
                if (wall.getTo().getX() == wall.getFrom().getX()) {
                    AWall = Integer.MAX_VALUE;
                } else if (wall.getTo().getY() == wall.getFrom().getY()) {
                    AWall = 0;
                } else {
                    AWall = (wall.getTo().getY() - wall.getFrom().getY()) / (wall.getTo().getX() - wall.getFrom().getX());
                    BWall = wall.getFrom().getY() - wall.getFrom().getX() * AWall;
                }

                // in case they are not parallel
                if (ARobot != AWall) {
                    double x, y;
                    if (AWall == Integer.MAX_VALUE && ARobot == 0) {
                        x = wall.getFrom().getX();
                        y = RY;
                    } else if (ARobot == Integer.MAX_VALUE && AWall == 0) {
                        x = RY;
                        y = wall.getFrom().getY();
                    } else if (ARobot == 0) {
                        y = RY;
                        x = (y - BWall) / AWall;				
                    } else if (AWall == 0) {
                        y = wall.getFrom().getY();
                        x = (y - BRobot) / ARobot;
                    } else if (ARobot == Integer.MAX_VALUE) {
                        x = RX;
                        y = x*AWall + BWall;	
                    } else if (AWall == Integer.MAX_VALUE) {
                        x = wall.getFrom().getX();
                        y = x*ARobot + BRobot;
                    } else {
                        x = (BWall - BRobot) / (ARobot - AWall);
                        y = ARobot * x + BRobot;
                    }

                    double minX = Math.min(wall.getFrom().getX(), wall.getTo().getX());
                    double maxX = Math.max(wall.getFrom().getX(), wall.getTo().getX());
                    double minY = Math.min(wall.getFrom().getY(), wall.getTo().getY());
                    double maxY = Math.min(wall.getFrom().getY(), wall.getTo().getY());
                    if(minX <= x && x <= maxX && minY <= y && y <= maxY) {
                        double distance = Math.sqrt(Math.pow(x - RX, 2) + Math.pow(y - RY, 2));
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

    private double castRay(Edges wall, Coords pos, double degrees) {
        double ARobot;
        double BRobot = 0;
        if (degrees == 90||degrees == 270) {
            ARobot = Integer.MAX_VALUE;
        } else if (degrees == 180||degrees == 0) {
            ARobot = 0;
        } else {
            ARobot  = Math.tan(Math.toRadians(degrees));
            BRobot = pos.getY() - pos.getX() * ARobot;
        }
        // edge
        double AWall;
        double BWall = 0;
        if (wall.getTo().getX() == wall.getFrom().getX()) {
            AWall = Integer.MAX_VALUE;
        } else if (wall.getTo().getY() == wall.getFrom().getY()) {
            AWall = 0;
        } else {
            AWall = (wall.getTo().getY() - wall.getFrom().getY()) / (wall.getTo().getX() - wall.getFrom().getX());
            BWall = wall.getFrom().getY() - wall.getFrom().getX() * AWall;
        }
        // in case they are not parallel
        if (ARobot != AWall) {
            double x, y;
            if (AWall == Integer.MAX_VALUE && ARobot == 0) {
                x = wall.getFrom().getX();
                y = RY;
            } else if (ARobot == Integer.MAX_VALUE && AWall == 0) {
                x = RY;
                y = wall.getFrom().getY();
            } else if (ARobot == 0) {
                y = RY;
                x = (y - BWall) / AWall;
            } else if (AWall == 0) {
                y = wall.getFrom().getY();
                x = (y - BRobot) / ARobot;
            } else if (ARobot == Integer.MAX_VALUE) {
                x = pos.getX();
                y = x*AWall + BWall;
            } else if (AWall == Integer.MAX_VALUE) {
                x = wall.getFrom().getX();
                y = x*ARobot + BRobot;
            } else {
                x = (BWall - BRobot) / (ARobot - AWall);
                y = ARobot * x + BRobot;
            }

            double minX = Math.min(wall.getFrom().getX(), wall.getTo().getX());
            double maxX = Math.max(wall.getFrom().getX(), wall.getTo().getX());
            double minY = Math.min(wall.getFrom().getY(), wall.getTo().getY());
            double maxY = Math.min(wall.getFrom().getY(), wall.getTo().getY());
            if(minX <= x && x <= maxX && minY <= y && y <= maxY) {
                double distance = Math.sqrt(Math.pow(x - pos.getX(), 2) + Math.pow(y - RY, 2));
                if (0.0 < degrees && 90.0 > degrees) {
                    if (x > RX && y > RY) {
                        sensors = Math.min(sensors[i], distance);
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
        return distance;
    }
    public Set<Edges> getEnvironment() {
        return environment;
    }
    
    public void setLength(int l) {
        this.l = l;
    }

    public double castRay(Coords pos, double angle) {
            
        if (angle < 0) {
            ange = (-angle) % 360;
        } else {
            angle = angle % 360;
        }

        double posX = pos.getX();
        double posY = pos.getY();
        
        double minDistance = Integer.MAX_VALUE;
        
        for (Edges edge : environment) {

            // ray
            double rayA;
            double rayB = 0;
            if (angle == 90 || angle == 270) {
                rayA = Integer.MAX_VALUE;
            } else if (angle == 180 || angle == 0) {
                rayA = 0;
            } else {
                rayA = Math.tan(Math.toRadians(angle));
                rayB = posY - posX * rayA;
            }
            // edge
            double edgeA;
            double edgeB = 0;
            if (edge.getTo().getX() == edge.getFrom().getX()) {
                edgeA = Integer.MAX_VALUE;
            } else if (edge.getTo().getY() == edge.getFrom().getY()) {
                edgeA = 0;
            } else {
                edgeA = (edge.getTo().getY() - edge.getFrom().getY()) / (edge.getTo().getX() - edge.getFrom().getX());
                edgeB = edge.getFrom().getY() - edge.getFrom().getX() * edgeA;
            }
            // in case they are not parralel
            if (rayA != edgeA) {
                double x, y;
                if (edgeA == Integer.MAX_VALUE && rayA == 0) {
                    x = edge.getFrom().getX();
                    y = posY;
                } else if (rayA == Integer.MAX_VALUE && edgeA == 0) {
                    x = posY;
                    y = edge.getFrom().getY();
                } else if (rayA == 0) {
                    y = posY;
                    x = (y - edgeB) / edgeA;
                } else if (edgeA == 0) {
                    y = edge.getFrom().getY();
                    x = (y - rayB) / rayA;
                } else if (rayA == Integer.MAX_VALUE) {
                    x = posX;
                    y = x * edgeA + edgeB;
                } else if (edgeA == Integer.MAX_VALUE) {
                    x = edge.getFrom().getX();
                    y = x * rayA + rayB;
                } else {
                    x = (edgeB - rayB) / (rayA - edgeA);
                    y = rayA * x + rayB;
                }

                double minX = Math.min(edge.getFrom().getX(), edge.getTo().getX());
                double maxX = Math.max(edge.getFrom().getX(), edge.getTo().getX());
                double minY = Math.min(edge.getFrom().getY(), edge.getTo().getY());
                double maxY = Math.min(edge.getFrom().getY(), edge.getTo().getY());
                
                if (minX <= x && x <= maxX && minY <= y && y <= maxY) {
                    double distance = Math.sqrt(Math.pow(x - posX, 2) + Math.pow(y - posY, 2));
                    if (0.0 < angle && 90.0 > angle) {
                        if (x > posX && y > posY) {
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (90.0 < angle && 180.0 > angle) {
                        if (x < posX && y > posY) {
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (180.0 < angle && 270.0 > angle) {
                        if (x < posX && y < posY) {
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (270.0 < angle && 360.0 > angle) {
                        if (x > posX && y < posY) {
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (angle == 0) {
                        if (x > posX && y == posY) {
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (angle == 90) {
                        if (x == posX && y > posY) {
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (angle == 180) {
                        if (x < posX && y == posY) {
                            minDistance = Math.min(minDistance, distance);
                        }
                    } else if (angle == 270) {
                        if (x == posX && y < posY) {
                            minDistance = Math.min(minDistance, distance);
                        }
                    }
                }
            }
            if (minDistance == Integer.MAX_VALUE) {
                return null;
            } else {
                return minDistance;
            }
        }
    }
}
