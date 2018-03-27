
import java.util.ArrayList;
import org.ejml.simple.SimpleMatrix;

import com.sun.org.apache.xalan.internal.xsltc.dom.ArrayNodeListIterator;

/**
 * 
 * @author Rouven Bonke, Timo Raff
 *
 *         class for calculating position updates and the fitness functino
 */
public class Robot {
	// contains movement methods for the Robot and i think the position ?

	private double l; // distance between the 2 wheels
	private Coords currentPosition;
	private Maze maze;
	private int speedInc;

	// for the 2nd assignement
	private Coords expectation;
	private Coords variance;

	// 3x3 Matrixes
	private SimpleMatrix A, B, C, R, K, Q;

	Robot(double x, double y, Maze maze, double l, int speedInc) {
		this.l = l;
		this.speedInc = speedInc;
		currentPosition = new Coords(x, y);
		currentPosition.setAngle(1);
		// init with known position. current position is not needed anymore ? maze
		// should know the exact position of the robot;
		expectation = new Coords(x, y);
		expectation.setAngle(1);

		variance = new Coords(0, 0, 0);
		this.maze = maze;

		// init simple 3x3 matrices
		// edit to add noise!?
		A = SimpleMatrix.identity(3);
		B = SimpleMatrix.identity(3);
		C = SimpleMatrix.identity(3);
		double movesigmax =1, movesigmay =1, movesigmatheta =1;
		double meassigmax=1, meassigmay=1, meassigmatheta=1;
		R = SimpleMatrix.diag(movesigmax, movesigmay, movesigmatheta); // TODO What is R movement error
		Q = SimpleMatrix.diag(meassigmax, meassigmay, meassigmatheta); // TODO What is Q measurement error

	}

	public void setPosition(int x, int y) {
		currentPosition.setX(x);
		currentPosition.setY(y);
	}

	public void move() {
		// gets information from the NN Vr and Vl
		// change vL and vR to values from -1to 1
		double vL = Math.random() * speedInc;
		double vR = Math.random() * speedInc;
		double x = maze.getRobotsCurrentPosition().getX();
		double y = maze.getRobotsCurrentPosition().getY();
		double theta = Math.toRadians(maze.getRobotsCurrentPosition().getAngle());
		double newx;
		double newy;
		double newtheta = 0;
		// for using cos and sin we need Radians
		double deltat = 0.3;
		double w;
		double r;
		double iccX;
		double iccY;

		// calculating new position (newx and newy) using the formulas discussed in
		// class
		if (vR == vL) {
			newx = x + Math.cos(theta) * vR * deltat;
			newy = y + Math.sin(theta) * vR * deltat;
			// just move forward;
		} else {
			r = (l / 2.) * ((vL + vR) / (vR - vL));
			w = (vR - vL) / l;
			iccX = x - r * Math.sin(theta);
			iccY = y + r * Math.cos(theta);
			newx = Math.cos(w * deltat) * (x - iccX) - (Math.sin(w * deltat) * (y - iccY)) + iccX;
			newy = Math.sin(w * deltat) * (x - iccX) + (Math.cos(w * deltat) * (y - iccY)) + iccY;
			newtheta = Math.toDegrees(theta + w * deltat) % 360;
		}

		double oldPosX = maze.getRobotsCurrentPosition().getX();
		double oldPosY = maze.getRobotsCurrentPosition().getY();
		double oldPosAngle = maze.getRobotsCurrentPosition().getAngle();

		maze.getRobotsCurrentPosition().setX(newx);
        maze.getRobotsCurrentPosition().setY(newy);
        maze.getRobotsCurrentPosition().setAngle(newtheta);

		boolean colision = maze.checkForCollision();
		if (colision) {
			double newAngle = (oldPosAngle + Math.random() * 360) % 360;
            maze.getRobotsCurrentPosition().setX(oldPosX);
            maze.getRobotsCurrentPosition().setY(oldPosY);
            maze.getRobotsCurrentPosition().setAngle(newAngle);
		}
		// update belief.
		// not sure what exactly the action is
		// TODO inset method for triangulation here.
		double deltameas = (Math.random()-1)*2/10;
		double deltamove = (Math.random()-1)*2/10;
		kalman_filter(new Coords (calculatePosition().getX()*(1+deltameas), calculatePosition().getY()*(1+deltameas), calculatePosition().getAngle()), new Coords((newx - x)*(1+deltamove), (newy - y)*(1+deltamove), newtheta - theta));
		//expectation=calculatePosition();
		//System.out.println(expectation+" - "+ currentPosition);
        double angle = Math.atan2(expectation.getY() - currentPosition.getY(), expectation.getX() - currentPosition.getX());
        double degrees = Math.toDegrees(angle);
        if (degrees < 0)
            degrees += 360;
        else if (degrees > 360)
            degrees -= 360;
        /*
        currentPosition.setX(expectation.getX());
        currentPosition.setY(expectation.getY());
        currentPosition.setAngle(degrees);
		*/
	}
	

	/*
	 * updates the belief of the robot on the base of executed action and measured position (triangulation vbia beacons)
	 */
	public void kalman_filter(Coords measurements, Coords action) {
		// measurements = z
		// position = u
		/*
		 * berechnung nach folien ausfhren einfach die lokalen objekte (expectation und
		 * variance) beschreiben *
		 */

		SimpleMatrix mu = new SimpleMatrix(3, 1);
		SimpleMatrix sigma = new SimpleMatrix(3, 3);
		SimpleMatrix act = new SimpleMatrix(3, 1);
		SimpleMatrix meas = new SimpleMatrix(3, 1);

		mu.set(0, 0, expectation.getX());
		mu.set(1, 0, expectation.getY());
		mu.set(2, 0, expectation.getAngle());

		sigma.set(0, 0, variance.getX());
		sigma.set(1, 1, variance.getY());
		sigma.set(2, 2, variance.getAngle());

		act.set(0, 0, action.getX());
		act.set(1, 0, action.getY());
		act.set(2, 0, action.getAngle());

		meas.set(0, 0, measurements.getX());
		meas.set(1, 0, measurements.getY());
		meas.set(2, 0, measurements.getAngle());

		SimpleMatrix tempmu = A.mult(mu).plus(B.mult(act));
		SimpleMatrix tempsigma = A.mult(sigma).mult(A.transpose()).plus(R);
		K = tempsigma.mult(C.transpose()).mult(C.mult(tempsigma).mult(C.transpose()).plus(Q)).invert();
		mu = tempmu.plus(K.mult(meas.minus(C.mult(tempmu))));
		sigma = SimpleMatrix.identity(3).minus(K.mult(C)).mult(tempsigma);

		expectation.setX(mu.get(0, 0));
		expectation.setY(mu.get(1, 0));
		expectation.setAngle(mu.get(2, 0));

		variance.setX(sigma.get(0, 0));
		variance.setY(sigma.get(1, 1));
		variance.setAngle(sigma.get(2, 2));

		/*
		 * newavexpectation.setX(expectation.getX() + action.getX());
		 * newavexpectation.setY(expectation.getY()+ action.getY());
		 * newavexpectation.setAngle(expectation.getAngle()+ action.getAngle());
		 * 
		 * newavvariance.setX(variance.getX() +0); //+R +0 ist nur ein Platzhalter
		 * 
		 */
	}

	//TODO deleteme
	public double[] getSensorValues() {
		// length of array is 15 not 12 because last 3 inputs are posX, posY and
		// direction.
		double[] sensors = new double[15];
		double[] tmp = maze.calculateSensorValues(currentPosition);
		System.arraycopy(tmp, 0, sensors, 0, tmp.length);
		sensors[12] = currentPosition.getX();
		sensors[13] = currentPosition.getY();
		sensors[14] = currentPosition.getAngle();
		return sensors;
	}

	public Coords getCurrentPosition() {
		return currentPosition;
	}
	
	public Coords getBelief() {
		return expectation;
	}

	/**
	 * check for beacons in range
	 * 
	 * @return set of beacons in range of robot
	 */
	public ArrayList<Coords> beaconsInRange() {
		return maze.beaconsInRange();
	}

	public Coords calculatePosition() {
		//TODO Maze knows the exact position of robot Robot himself not.
		ArrayList<Coords> beacons = maze.beaconsInRange();

		double angleOfView = currentPosition.getAngle();

		if (beacons.size() > 1) {
			double x1 = beacons.get(0).getX();
			double x2 = beacons.get(1).getX();
			double y1 = beacons.get(0).getY();
			double y2 = beacons.get(1).getY();
			double r1 = beacons.get(0).getAngle();
			double r2 = beacons.get(1).getAngle();
			// if at least 3 beacons in range (easier to calculate exact position)
			if (beacons.size() >= 3) {
				double x3 = beacons.get(2).getX();
				double y3 = beacons.get(2).getY();
				double r3 = beacons.get(2).getAngle();

				double x = ((y2 - y3)
						* ((Math.pow(y2, 2) - Math.pow(y1, 2)) + (Math.pow(x2, 2) - Math.pow(x1, 2))
								+ (Math.pow(r1, 2) - Math.pow(r2, 2)))
						- (y1 - y2) * ((Math.pow(y3, 2) - Math.pow(y2, 2)) + (Math.pow(x3, 2) - Math.pow(x2, 2))
								+ (Math.pow(r2, 2) - Math.pow(r3, 2))))
						/ (2 * ((x1 - x2) * (y2 - y3) - (x2 - x3) * (y1 - y2)));
				double y = ((x2 - x3)
						* ((Math.pow(x2, 2) - Math.pow(x1, 2)) + (Math.pow(y2, 2) - Math.pow(y1, 2))
								+ (Math.pow(r1, 2) - Math.pow(r2, 2)))
						- (x1 - x2) * ((Math.pow(x3, 2) - Math.pow(x2, 2)) + (Math.pow(y3, 2) - Math.pow(y2, 2))
								+ (Math.pow(r2, 2) - Math.pow(r3, 2))))
						/ (2 * ((y1 - y2) * (x2 - x3) - (y2 - y3) * (x1 - x2)));
				return new Coords(-x, -y);
			} else {
				// if there are at most 2 beacons (trickier to calculate position because robot
				// could be in 2 different locations)
				double distanceBetweenBeacons = Math.sqrt(Math.pow(beacons.get(0).getX() - beacons.get(1).getX(), 2)
						+ Math.pow(beacons.get(0).getY() - beacons.get(1).getY(), 2));
				// in case distance is bigger than the 2 radius of the circles
				// or one circle is inside another one
				// or one circle lays on top of another with the same radius
				if (distanceBetweenBeacons > (r1 + r2) || distanceBetweenBeacons < Math.abs(r1 - r2)
						|| (distanceBetweenBeacons == 0 && r1 == r2)) {
					System.out.println("fehler");
					return null;
				}else {
					double x = (Math.pow(r1, 2) + Math.pow(distanceBetweenBeacons, 2) - Math.pow(r2, 2)) / (2 * distanceBetweenBeacons);
					double y = Math.sqrt(Math.pow(r1, 2) - Math.pow(x, 2));

					double Q1x = x1 + x * ((x2 - x1) / distanceBetweenBeacons);
					double Q1y = y1 + x * ((y2 - y1) / distanceBetweenBeacons);

					if(y == 0)
						return new Coords(Q1x, Q1y);
					double Q2x = Q1x + y * ((y2 - y1) / distanceBetweenBeacons);
					double Q2y = Q1y - y * ((x2 - x1) / distanceBetweenBeacons);
					Q1x -= y * ((y2 - y1) / distanceBetweenBeacons);
					Q1y += y * ((x2 - x1) / distanceBetweenBeacons);

					if(angleOfView > 0 && angleOfView <= 90) {
                        return new Coords(Q1x, Q1y);
					} else if(angleOfView > 90 && angleOfView <= 180) {
						return new Coords(Q2x, Q1y);
					} else if(angleOfView > 180 && angleOfView <= 270) {
						return new Coords(Q1x, Q2y);
					} else {
						return new Coords(Q2x, Q2y);
					}
				}
			}
		} else {
			// What to do if not enough beacons in range?
			System.out.println("not enough beacons!!!!!!");
			return null;
		}
	}

}
