<<<<<<< HEAD
import java.util.Arrays;
import org.ejml.simple.SimpleMatrix;

=======
import java.util.Set;
>>>>>>> ee7ec62f1b5140f89efa7b7b004009cf45f78eab

/**
 * 
 * @author Rouven Bonke, Timo Raff
 *
 *         class for calculating position updates and the fitness functino
 */
public class Robot {
	// contains movement methods for the Robot and i think the position ?

	private double l = 2; // distance between the 2 wheels
	private Coords currentPosition;
	private Maze maze;
	
	//for the 2nd assignement
    private Coords expectation;
	private Coords variance;
	

	// 3x3 Matrixes
	private SimpleMatrix A, B, C, R, K, Q;
	
	// system state estimate
	private SimpleMatrix mu, sigma, tempmu, tempsigma, act, meas;
			
	
	private Visualizer visulizer;


	Robot(double x, double y, Maze maze, double l) {
		this.l = l;
		currentPosition = new Coords(x, y);
		currentPosition.setAngle(1);
		//init with known position. current position is not needed anymore ? maze should know the exact position of the robot;
		expectation= new Coords(x, y);
		expectation.setAngle(1);
		this.maze = maze;

	}

	public void setPosition(int x, int y) {
		currentPosition.setX(x);
		currentPosition.setY(y);
	}

	public void move() {
		// gets information from the NN Vr and Vl
		// change vL and vR to values from -1to 1
		double vL = Math.random();
		double vR = Math.random();
		double x = currentPosition.getX();
		double y = currentPosition.getY();
		double theta = Math.toRadians(currentPosition.getAngle());
		double newx = 0;
		double newy = 0;
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
		double oldPosX = currentPosition.getX();
		double oldPosY = currentPosition.getY();
		double oldPosAngle = currentPosition.getAngle();
        currentPosition.setX(newx);
        currentPosition.setY(newy);
        currentPosition.setAngle(newtheta);

        boolean colision = maze.checkForCollision(currentPosition);
        if (colision) {
//        	if (vR == vL) {
				currentPosition.setX(oldPosX);
				currentPosition.setY(oldPosY);
				// just move backwards;
//			}
			double newAngle = (oldPosAngle + Math.random() * 360) % 360;
            currentPosition.setAngle(newAngle);
        }
		//update belief.
		//not sure what exactly the action is
		//TODO inset method for triangulation here.
		kalman_filter(new Coords(0, 0, 0), new Coords(newx-x, newy-y, newtheta-theta));

		return;/*
						 * || newx < maze.getMinX() || newx > maze.getMaxX() || newy < maze.getMinY() ||
						 * newy > maze.getMaxY()*/
	
	}
	

	
	public void kalman_filter(Coords measurements, Coords action) {
		//measurements = z
		//position = u
		/*
		 * berechnung nach folien ausfhren
		 * einfach die lokelen objekte (expectation und variance) berschreiben		 *  
		 */
		A = SimpleMatrix.identity(3);
		B = SimpleMatrix.identity(3);
		C = SimpleMatrix.identity(3);
		R = SimpleMatrix.identity(3); //TODO What is R
		Q = SimpleMatrix.identity(3); //TODO What is Q
		
		mu.set(0,0, expectation.getX());
		mu.set(0,1, expectation.getY());
		mu.set(0,2, expectation.getAngle());
		
		sigma.set(0,0, variance.getX());
		sigma.set(0,1, variance.getY());
		sigma.set(0,2, variance.getAngle());
		
		act.set(0,0, action.getX());
		act.set(0,1, action.getY());
		act.set(0,2, action.getAngle());
		
		meas.set(0,0, measurements.getX());
		meas.set(0,1, measurements.getY());
		meas.set(0,2, measurements.getAngle());
		
		tempmu = A.mult(mu).plus(B.mult(act));
		tempsigma = A.mult(sigma).mult(A.transpose()).plus(R);
		K = tempsigma.mult(C.transpose()).mult(C.mult(tempsigma).mult(C.transpose()).plus(Q)).invert();
		mu = tempmu.plus(K.mult(meas.minus(C.mult(tempmu))));
		sigma = SimpleMatrix.identity(3).minus(K.mult(C)).mult(tempsigma);
		
		expectation.setX(mu.get(0,0));
		expectation.setY(mu.get(0,1));
		expectation.setAngle(mu.get(0,2));
		
		variance.setX(sigma.get(0,0));
		variance.setY(sigma.get(0,1));
		variance.setAngle(sigma.get(0,3));
		
		/*
		newavexpectation.setX(expectation.getX() + action.getX());
		newavexpectation.setY(expectation.getY()+ action.getY());
		newavexpectation.setAngle(expectation.getAngle()+ action.getAngle());
		
		newavvariance.setX(variance.getX() +0); //+R +0 ist nur ein Platzhalter
		
		*/
		System.out.println(A);
	}

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

	public void setVisualizer(Visualizer visulizer) {
		this.visulizer = visulizer;
	}

	public Coords getCurrentPosition() {
		return currentPosition;
	}

    /**
     * check for beacons in range
     * @return set of beacons in range of robot
     */
    public Set<Coords> beaconsInRange() {
        return maze.beaconsInRange(currentPosition);
    }
}
