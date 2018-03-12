import java.util.Arrays;

import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;

public class Robot {
	// contains movement methods for the Robot and i think the position ?

	private final static int COLISIONDECREASE = 10;
	private int l = 2; // distance between the 2 wheels
	private Coords currentPosition;
	private Maze maze;
	private double fitness;
	private double lastMinSensorValue;
	// for fitness calculation:
	boolean[][] grid;
	final static int GRIDSIZE = 100;
	Visualizer visulizer;

	Robot(double x, double y, Maze maze) {
		currentPosition = new Coords(x, y);
		currentPosition.setAngle(0);
		this.maze = maze;
		maze.setLength(l);
		this.fitness = 0;
		this.lastMinSensorValue = 0;

		grid = new boolean[GRIDSIZE][GRIDSIZE];

	}
	public void setPosition(int x, int y) {
		currentPosition.setX(x);
		currentPosition.setY(y);
	}

	public void move(double[] velocity) {
		// gets information from the NN Vr and Vl
		// change vL and vR to values from -1to 1
		double vL = (velocity[0] - 0.5) * 2;
		double vR = (velocity[1] - 0.5) * 2;
		double x = currentPosition.getX();
		double y = currentPosition.getY();
		//for using cos and sin we need Radians
		double theta = Math.toRadians(currentPosition.getAngle());
		double deltat = 0.3;
		double w;
		double r;
		double iccX;
		double iccY;
		double newx = 0;
		double newy = 0;
		double newtheta = 0;
		//calculating new position (newx and newy) using the formulas discussed in class
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
			newtheta = Math.toDegrees(theta + w * deltat)%360;
		}
		boolean colision = maze.checkForCollision(currentPosition, new Coords(newx, newy, newtheta));

		if (colision || newx < maze.getMinX() || newx > maze.getMaxX() || newy < maze.getMinY() || newy > maze.getMaxY()) {
			// decrease fitnesfunction
			colision = false;
			fitness -= COLISIONDECREASE;
			//check if colision in x or y direction for position after colision. Theta also changes after colision
			colision = maze.checkForCollision(currentPosition, new Coords(newx, y, newtheta));
			if (colision) {
				currentPosition.setX(x);
				currentPosition.setY(newy);
				currentPosition.setAngle(theta + 37.5);
			} else {
				currentPosition.setX(newx);
				currentPosition.setAngle(theta + 37.5);
				currentPosition.setY(y);
			}
			colision = false;
		} else {
			//when there is no colision fitness function and currentPosition get updated 
			
			// alternative fitness function:
			// updateFitness(x,y); -> maybe also old x and y
			// calculate a fitnesupdate
			// V= average of unsigned rotation
			// deltaV= difference between the signed rotation
			// i corresponds to the distance to the next wall.
			// V*(1-Math.sqrt(deltaV))*(1-i)

			double v = (Math.abs(vL) + Math.abs(vR)) / 2;
			double deltaV = Math.abs(vL - vR);
			// wanna get away from the walls...
			// limit relevant wall distances to 6?
			double i = 6;
			if (lastMinSensorValue < 6) {
				i = lastMinSensorValue;
			}
			i /= 6.;
			// System.out.println("V: " +v +" deltaV= " +deltaV+" i:"+i);

			//fitness += (Math.abs(x-newx)+Math.abs(y-newy)) - deltaV + vL + vR ;

			fitness += v * (1 - Math.sqrt(deltaV)) * i;
			// fitness+=1;
			//updateFitness(newx, newy);
			currentPosition.setX(newx);
			currentPosition.setY(newy);
			currentPosition.setAngle(newtheta);
			// System.out.println("to: "+currentPosition);
		}
	}

	public double[] getSensorValues() {
		// length of array is 15 not 12 because last 3 inputs are posX, posY and
		// direction.
		double[] sensors = new double[15];
		double[] tmp = maze.calculateSensorValues(currentPosition);
		System.arraycopy(tmp, 0, sensors, 0, tmp.length);
		lastMinSensorValue = sensors[0];
		// dont take last 3 inputs in account --> these are last output + orientation
		for (int i = 1; i < sensors.length - 3; i++) {
			if (sensors[i] < lastMinSensorValue) {
				lastMinSensorValue = sensors[i];
			}
		}
		sensors[12] = currentPosition.getX();
		sensors[13] = currentPosition.getY();
		sensors[14] = currentPosition.getAngle();
		return sensors;
	}

	// currently not used.
	public void updateFitness(/* double oldX, double oldY, */ double x, double y) {
		if (x < 0 || y < 0 || y > maze.getMaxY() || x > maze.getMaxX()) {
			fitness -= COLISIONDECREASE;
		} else {
			double width = GRIDSIZE / maze.getMaxX();// is the width of onr cell
			double height = GRIDSIZE / maze.getMaxY();
			// double fromX = oldX * width;
			// double fromY = oldY * height;
			int toX = (int) (x * width);
			int toY = (int) (y * height);
			//System.out.println("grid position: x: " + toX + " y: " + toY);
			if (!grid[toX][toY]) {
				grid[toX][toY] = true;
				fitness += 4;
			}
		}
		// take a look in the grid and see how much (%) is visited
		// update the x and y coordinates to values fitting at the grid!??
		// search activate the single parts in the grid --> so calcuöate a route.
		// mapping of position:
		// double width = GRIDSIZE / maze.getMaxX();// is the width of onr cell
		// double height = GRIDSIZE / maze.getMaxY();
		// double fromX = oldX * width;
		// double fromY = oldY * height;
		// int toX = (int) (x * width);
		// int toY = (int) (y * height);
		// if (!grid[toX][toY]) {
		// grid[toX][toY] = true;
		// fitness += 10;
		// }
		// int xG
		// maze.getMaxX()
	}

	public double getFitness() {
		return fitness;
	}

	public int getL() {
		return l;
	}

	public void setVisualizer(Visualizer visulizer) {
		this.visulizer = visulizer;
	}

	public Coords getCurrentPosition() {
		return currentPosition;
	}

}
