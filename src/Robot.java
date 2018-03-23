import java.util.Arrays;

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
	Coords expectation;
	Coords variance;
	
	Visualizer visulizer;

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
		boolean colision = true;
		while (colision) {
			
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
			colision = maze.checkForCollision(currentPosition)|| newx < maze.getMinX() || newx > maze.getMaxX() || newy < maze.getMinY() ||newy > maze.getMaxY();
			if (colision)
				currentPosition.setAngle(Math.random() * 360);
		}
		//update belief. 
		//not sure what exactly the action is
		//TODO inset method for triangulation here.
		kalman_filter(new Coords(0, 0, 0), new Coords(newx-x, newy-y, newtheta-theta));
		currentPosition.setX(newx);
		currentPosition.setY(newy);
		currentPosition.setAngle(newtheta);
		return;/*
						 * || newx < maze.getMinX() || newx > maze.getMaxX() || newy < maze.getMinY() ||
						 * newy > maze.getMaxY()*/
	
	}

	public void kalman_filter(Coords measurements, Coords action) {
		//measurements = z
		//position = u
		/*
		 * berechnung nach folien ausführen
		 * einfach die lokelen objekte (expectation und variance) überschreiben		 *  
		 */
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

}
