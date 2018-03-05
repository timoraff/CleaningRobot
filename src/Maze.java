import java.util.HashSet;
import java.util.Set;

public class Maze {
    private double minX, minY;
    private double maxX, maxY;
    private Set<Edges> environment;
    private Set<Edges> obstacle;
    private Coords currentRobotsPosition;

    /**
     * contains the coordinate system, including the walls
     * also takes care about displaying the maze and robot movement
     */
    Maze() {
        minX = minY = 0.0;
        maxX = maxY = 30.0;
        environment = new HashSet<>();
        environment.add(new Edges(new Coords(minX, minY), new Coords(maxX, minY), "N"));
        environment.add(new Edges(new Coords(maxX, minY), new Coords(maxX, maxY), "E"));
        environment.add(new Edges(new Coords(maxX, maxY), new Coords(minX, maxY), "S"));
        environment.add(new Edges(new Coords(minX, maxY), new Coords(minX, minY), "W"));

        obstacle = new HashSet<>();
        obstacle.add(new Edges(new Coords(6.0, 6.0), new Coords(22.0, 6.0), "S"));
        obstacle.add(new Edges(new Coords(22.0, 6.0), new Coords(22.0, 22.0), "W"));
        obstacle.add(new Edges(new Coords(22.0, 22.0), new Coords(6.0, 22.0), "N"));
        obstacle.add(new Edges(new Coords(6.0, 22.0), new Coords(6.0, 6.0), "E"));

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
        for (Edges ob : obstacle) {
            calculateIntersection(oldPos, newPos, ob, true);
        }
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

    public double[] calculateSensorValuesTest() {
        double[] sensors = new double[15];
        double[] degrees = { 0.0, 30.0, 60.0, 90.0, 120.0, 150.0, 180.0, 210.0, 240.0, 270.0, 300.0, 330.0 };
        for(int i = 0; i < degrees.length; i++) {
            sensors[i] = -1;
        }
        for(Edges wall : environment) {for(int i = 0; i < degrees.length; i++) {
            double yDeg = castRay(currentRobotsPosition, degrees[i]);
            double xDeg = maxX;
            if(degrees[i] > 90 && degrees[i] < 270)
                xDeg = minX;
            if(degrees[i] == 90 || degrees[i] == 270)
                xDeg = currentRobotsPosition.getX();
            //a = y1 - y2
            //b = x2 - x1
            //c = x2y1 - x1y2
            //xs = (c1b2 - c2b1)/(a1b2 - a2b1)
            //ys = (a1c2 - a2c1)/(a1b2 - a2b1)
                //wall
//                double x1 = wall.getFrom().getX();
//                double y1 = wall.getFrom().getY();
//                double x2 = wall.getTo().getX();
//                double y2 = wall.getTo().getY();
//                double a1 = y1 - y2;
//                double b1 = x2 - x1;
//                double c1 = x2 * y1 - x1 * y2;
//
//                //robot
//                double x3 = currentRobotsPosition.getX();
//                double y3 = currentRobotsPosition.getY();
//                double x4 = xDeg;
//                double y4 = yDeg;
//                double a2 = y3 - y4;
//                double b2 = x4 - x3;
//                double c2 = x4 * y3 - x3 * y4;
//
//                double n = (a1 * b2 - a2 * b1);
//
//                double xs = (c1 * b2 - c2 * b1) / n;
//                double ys = (a1 * c2 - a2 * c1) / n;
//
//                double distance = Math.sqrt(Math.pow(x3 - xs, 2) + Math.pow(y3 - ys, 2));
//                if(degrees[i] == 0 && wall.getDirection().equals("E")) {
//                    sensors[i] = distance;
//                }
//                if(degrees[i] == 90 && wall.getDirection().equals("S")) {
//                    sensors[i] = distance;
//                }
//                if(degrees[i] == 180 && wall.getDirection().equals("W")) {
//                    sensors[i] = distance;
//                }
//                if(degrees[i] == 270 && wall.getDirection().equals("N")) {
//                    sensors[i] = distance;
//                }
                //wall
                double x1 = wall.getFrom().getX();
                double y1 = wall.getFrom().getY();
                double x2 = wall.getTo().getX();
                double y2 = wall.getTo().getY();
                double a1 = y1 - y2;
                double b1 = x2 - x1;
                double c1 = x2 * y1 - x1 * y2;

                //robot
                double x3 = currentRobotsPosition.getX();
                double y3 = currentRobotsPosition.getY();
                double x4 = xDeg;
                double y4 = yDeg;
                double a2 = y3 - y4;
                double b2 = x4 - x3;
                double c2 = x4 * y3 - x3 * y4;

                double n = (a1 * b2 - a2 * b1);
                double xs = (c1 * b2 - c2 * b1) / n;
                double ys = (a1 * c2 - a2 * c1) / n;
                double distance = Math.sqrt(Math.pow(x3 - xs, 2) + Math.pow(y3 - ys, 2));
                if(xs >= minX && xs <= maxX && ys >= minY && ys <= maxY && sensors[i] < distance)
                    sensors[i] = distance;
                System.out.println(degrees[i] + " " + xs + " " + ys + " " + distance);
            }
        }
        return sensors;
    }

    public double[] calculateSensorValues() {
        // cast rays to calculate distance to the wall/object
        // calculate distance counter clock wise
        // right -> right-right-up -> right-up-up ->
        // up -> left-up-up -> left-left-up ->
        // left -> left-left-down -> left-down-down ->
        // down -> right-down-down -> right-right-down
    	double sensors[]= new double[15];
        // cast ray right to boundary wall
        double dR = maxX - currentRobotsPosition.getX();
        // cast ray up to boundary wall
        double dU = currentRobotsPosition.getY();
        // cast ray left to boundary wall
        double dL = currentRobotsPosition.getX();
        // cast ray down to boundary wall
        double dD = maxY - currentRobotsPosition.getY();
        for (Edges ob: obstacle) {
            double right = calculateIntersection(currentRobotsPosition, new Coords(maxX, currentRobotsPosition.getY()), ob, false);
            if(right > 0.0 && right < 1.0) {
                double tmp = Math.abs(ob.getFrom().getX() - currentRobotsPosition.getX());
                if(dR > tmp)
                    dR = tmp;
            }
            double up = calculateIntersection(currentRobotsPosition, new Coords(currentRobotsPosition.getX(), minY), ob, false);
            if(up > 0.0 && up < 1.0) {
                double tmp = Math.abs(currentRobotsPosition.getY() - ob.getFrom().getY());
                if (dU > tmp)
                    dU = tmp;
            }
            double left = calculateIntersection(currentRobotsPosition, new Coords(minX, currentRobotsPosition.getY()), ob, false);
            if(left > 0.0 && left < 1.0) {
                double tmp = Math.abs(currentRobotsPosition.getX() - ob.getFrom().getX());
                if (dL > tmp)
                    dL = tmp;
            }
            double down = calculateIntersection(currentRobotsPosition, new Coords(currentRobotsPosition.getX(), maxY), ob, false);
            if(down > 0.0 && down < 1.0) {
                double tmp = Math.abs(ob.getFrom().getY() - currentRobotsPosition.getY());
                if (dD > tmp)
                    dD = tmp;
            }
        }

        // m
        double mRRU = Math.tan(Math.toRadians(30));
        double mRUU = Math.tan(Math.toRadians(60));
        double mLUU = Math.tan(Math.toRadians(120));
        double mLLU = Math.tan(Math.toRadians(150));
        double mLLD = Math.tan(Math.toRadians(210));
        double mLDD = Math.tan(Math.toRadians(240));
        double mRDD = Math.tan(Math.toRadians(300));
        double mRRD = Math.tan(Math.toRadians(330));

        // n
        double nRRU = (-currentRobotsPosition.getX()) * mRRU;
        double nRUU = (-currentRobotsPosition.getX()) * mRUU;
        double nLUU = (-currentRobotsPosition.getX()) * mLUU;
        double nLLU = (-currentRobotsPosition.getX()) * mLLU;
        double nLLD = (-currentRobotsPosition.getX()) * mLLD;
        double nLDD = (-currentRobotsPosition.getX()) * mLDD;
        double nRDD = (-currentRobotsPosition.getX()) * mRDD;
        double nRRD = (-currentRobotsPosition.getX()) * mRRD;

        // set the distances
        double dRRU = 0;
        double dRUU = 0;
        double dLUU = 0;
        double dLLU = 0;
        double dLLD = 0;
        double dLDD = 0;
        double dRDD = 0;
        double dRRD = 0;

        // y = mX + n
        double yRRU = mRRU * maxX - nRRU;
        double yRUU = mRUU * maxX - nRUU;
        double yLUU = mLUU * minX - nLUU;
        double yLLU = mLLU * minX - nLLU;
        double yLLD = mLLD * minX + nLLD;
        double yLDD = mLDD * minX + nLDD;
        double yRDD = mRDD * maxX + nRDD;
        double yRRD = mRRD * maxX + nRRD;
        for( Edges wall : environment) {
            double RRU = calculateIntersection(currentRobotsPosition, new Coords(maxX, yRRU), wall, false);
            if (RRU >= 0.0 && RRU <= 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (maxX - currentRobotsPosition.getX()) * RRU;
                double intersectionY = currentRobotsPosition.getY() + (yRRU - currentRobotsPosition.getY()) * RRU;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dRRU < distance)
                    dRRU = distance;
            }
            double RUU = calculateIntersection(currentRobotsPosition, new Coords(maxX, yRUU), wall, false);
            if (RUU >= 0.0 && RUU <= 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (maxX - currentRobotsPosition.getX()) * RUU;
                double intersectionY = currentRobotsPosition.getY() + (yRUU - currentRobotsPosition.getY()) * RUU;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dRUU < distance)
                    dRUU = distance;
            }
            double LUU = calculateIntersection(currentRobotsPosition, new Coords(minX, yLUU), wall, false);
            if (LUU >= 0.0 && LUU <= 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (minX - currentRobotsPosition.getX()) * LUU;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * LUU;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dLUU < distance)
                    dLUU = distance;
            }
            double LLU = calculateIntersection(currentRobotsPosition, new Coords(minX, yLLU), wall, false);
            if (LLU >= 0.0 && LLU <= 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (minX - currentRobotsPosition.getX()) * LLU;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * LLU;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dLLU < distance)
                    dLLU = distance;
            }
            double LLD = calculateIntersection(currentRobotsPosition, new Coords(minX, yLLD), wall, false);
            if (LLD >= 0.0 && LLD <= 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (minX - currentRobotsPosition.getX()) * LLD;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * LLD;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dLLD < distance)
                    dLLD = distance;
            }
            double LDD = calculateIntersection(currentRobotsPosition, new Coords(minX, yLDD), wall, false);
            if (LDD >= 0.0 && LDD <= 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (minX - currentRobotsPosition.getX()) * LDD;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * LDD;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dLDD < distance)
                    dLDD = distance;
            }
            double RDD = calculateIntersection(currentRobotsPosition, new Coords(maxX, yRDD), wall, false);
            if (RDD >= 0.0 && RDD <= 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (maxX - currentRobotsPosition.getX()) * RDD;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * RDD;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dRDD < distance)
                    dRDD = distance;
            }
            double RRD = calculateIntersection(currentRobotsPosition, new Coords(maxX, yRRD), wall, false);
            if (RRD >= 0.0 && RRD <= 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (maxX - currentRobotsPosition.getX()) * RRD;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * RRD;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dRRD < distance)
                    dRRD = distance;
            }
        }


        for( Edges ob : obstacle) {
            double RRU = calculateIntersection(currentRobotsPosition, new Coords(maxX, yRRU), ob, false);
            if (RRU > 0.0 && RRU < 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (maxX - currentRobotsPosition.getX()) * RRU;
                double intersectionY = currentRobotsPosition.getY() + (yRRU - currentRobotsPosition.getY()) * RRU;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dRRU > distance)
                    dRRU = distance;
            }
            double RUU = calculateIntersection(currentRobotsPosition, new Coords(maxX, yRUU), ob, false);
            if (RUU > 0.0 && RUU < 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (maxX - currentRobotsPosition.getX()) * RUU;
                double intersectionY = currentRobotsPosition.getY() + (yRUU - currentRobotsPosition.getY()) * RUU;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dRUU > distance)
                    dRUU= distance;
            }
            double LUU = calculateIntersection(currentRobotsPosition, new Coords(minX, yLUU), ob, false);
            if (LUU > 0.0 && LUU < 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (minX - currentRobotsPosition.getX()) * LUU;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * LUU;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dLUU > distance)
                    dLUU = distance;
            }
            double LLU = calculateIntersection(currentRobotsPosition, new Coords(minX, yLLU), ob, false);
            if (LLU > 0.0 && LLU < 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (minX - currentRobotsPosition.getX()) * LLU;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * LLU;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dLLU > distance)
                    dLLU = distance;
            }
            double LLD = calculateIntersection(currentRobotsPosition, new Coords(minX, yLLD), ob, false);
            if (LLD > 0.0 && LLD < 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (minX - currentRobotsPosition.getX()) * LLD;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * LLD;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dLLD > distance)
                    dLLD = distance;
            }
            double LDD = calculateIntersection(currentRobotsPosition, new Coords(minX, yLDD), ob, false);
            if (LDD > 0.0 && LDD < 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (minX - currentRobotsPosition.getX()) * LDD;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * LDD;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dLDD > distance)
                    dLDD = distance;
            }
            double RDD = calculateIntersection(currentRobotsPosition, new Coords(maxX, yRDD), ob, false);
            if (RDD > 0.0 && RDD < 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (maxX - currentRobotsPosition.getX()) * RDD;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * RDD;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dRDD > distance)
                    dRDD = distance;
            }
            double RRD = calculateIntersection(currentRobotsPosition, new Coords(maxX, yRRD), ob, false);
            if (RRD > 0.0 && RRD < 1.0) {
                double intersectionX = currentRobotsPosition.getX() + (maxX - currentRobotsPosition.getX()) * RRD;
                double intersectionY = currentRobotsPosition.getY() + (yLUU - currentRobotsPosition.getY()) * RRD;
                double distance  = Math.sqrt(Math.pow(currentRobotsPosition.getX() - intersectionX, 2.0) + Math.pow(currentRobotsPosition.getY() - intersectionY, 2.0));
                if(dRRD > distance)
                    dRRD = distance;
            }
        }

        sensors[0] = dR;
        sensors[1] = dRRU;
        sensors[2] = dRUU;
        sensors[3] = dU;
        sensors[4] = dLUU;
        sensors[5] = dLLU;
        sensors[6] = dL;
        sensors[7] = dLLD;
        sensors[8] = dLDD;
        sensors[9] = dD;
        sensors[10] = dRDD;
        sensors[11] = dRRD;
        sensors[12] = currentRobotsPosition.getX();
        sensors[13] = currentRobotsPosition.getY();
        sensors[14] = currentRobotsPosition.getAngle();

    	return sensors;
    }

    /**
     * function to calculate Y
     * @param point robot's current position
     * @param angle angle of the ray
     * @return y intersection with the wall (x=0 and x=30)
     */
    double castRay(Coords point, double angle) {
        double x = maxX;
        double m = Math.tan(angle/360 * 2 * Math.PI);
        double n = point.getY() - m * point.getX();

        if(angle == 90 || angle == 270)
            x = point.getX();
        if(angle > 90 && angle < 270)
            x = minX;
        return m * x + n;
    }

    public Set<Edges> getEnvironment() {
        return environment;
    }
    
    public Set<Edges> getObstacle() {
        return obstacle;
    }
}
