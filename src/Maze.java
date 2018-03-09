import java.util.HashSet;
import java.util.Set;

public class Maze {
    private static Set<Edges> environment;
    private int l = 0;
    private double maxY;
    private double maxX;

    /**
     * contains the coordinate system, including the walls
     * also takes care about displaying the maze and robot movement
     */
    Maze() {
        double minY;
        double minX = minY = 0.0;
        maxX = maxY = 30.0;
        environment = new HashSet<>();
        // wall
        environment.add(new Edges(new Coords(minX, minY), new Coords(maxX, minY))); //bottom line
        environment.add(new Edges(new Coords(maxX, minY), new Coords(maxX, maxY))); //top line
        environment.add(new Edges(new Coords(maxX, maxY), new Coords(minX, maxY))); //right line
        environment.add(new Edges(new Coords(minX, maxY), new Coords(minX, minY))); //left line

        // obstacle
        environment.add(new Edges(new Coords(minX + 6, minY + 6), new Coords(maxX - 6, minY + 6))); //bottom line
        environment.add(new Edges(new Coords(maxX - 6, minY + 6), new Coords(maxX - 6, maxY - 6))); //right line
        environment.add(new Edges(new Coords(maxX - 6, maxY - 6), new Coords(minX + 6, maxY - 6))); //top line
        environment.add(new Edges(new Coords(minX + 6, minY + 6), new Coords(minX + 6, maxY - 6))); //left line
    }

    /**
     * check for collisions
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
        return !(!(distance > castRay(posLeft, degrees)) && !(distance > castRay(posRight, degrees)));
    }

    /**
     * Calculate the distance between the robot and walls in 12 directions
     * @param currentPosition pos of the robot
     * @return array of measurements
     */
    public double[] calculateSensorValues(Coords currentPosition) {
        double sensors[]= new double[12];
        double[] degrees = {0,30,60,90,120,150,180,210,240,270,300,330};

        for(int i = 0; i < degrees.length; i++) {
            sensors[i] = castRay(currentPosition, degrees[i]);
        }

        double maxDistance = 0;
        for (double distance : sensors) {
            if(distance != Integer.MAX_VALUE)
                maxDistance = Math.max(distance, maxDistance);
        }

        double percent = 10;
        for (double distance : sensors) {
            double rand = Math.random();
            double noisyDistance;
            if (rand < 0.5) {
                noisyDistance = distance - distance * (distance/maxDistance) * percent/100 * rand;
            } else {
                noisyDistance = distance + distance * (distance/maxDistance) * percent/100 * rand;
            }
            //System.out.println("Distance: " + distance + " Noisy distance: " + noisyDistance + " Random factor: " + rand);
        }

        return sensors;
    }

    /**
     * getter for the environment
     * @return set of edges
     */
    public Set<Edges> getEnvironment() {
        return environment;
    }

    /**
     * set the distance between the 2 wheels of the robot
     * @param l distance
     */
    public void setLength(int l) {
        this.l = l;
    }

    /**
     * Cast a ray from a given position with a given angle
     * @param pos Coords position the ray should be casted
     * @param angle angle the ray should be casted
     * @return the distance to the closest intersection between the ray and an object in the environment
     */
    private double castRay(Coords pos, double angle) {
            
        if (angle < 0) {
            angle = (-angle) % 360;
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
            // in case they are not parallel
            if (rayA != edgeA) {
                double x, y;
                if (edgeA == Integer.MAX_VALUE && rayA == 0) {
                    x = edge.getFrom().getX();
                    y = posY;
                } else if (rayA == Integer.MAX_VALUE && edgeA == 0) {
                    x = posX;
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
                double maxY = Math.max(edge.getFrom().getY(), edge.getTo().getY());
                
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
        }
        return minDistance;
    }

	public double getMaxY() {
		return maxY;
	}

	public double getMaxX() {
		return maxX;
	}
    
}
