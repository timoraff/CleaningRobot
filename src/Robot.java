import java.util.Arrays;

import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;

public class Robot {
	// contains movement methods for the Robot and i think the position ?

	final static int COLISIONDECREASE = 2;
	int l = 2; // distance between the 2 wheels
	double posX; // Coordinates in the maze
	double posY;
	double direction;
	Maze maze;
	double fitness;
	double lastMinSensorValue;
	// for fitness calculation:
	boolean[][] grid;
	final static int GRIDSIZE = 500;
	Visualizer visulizer;

	Robot(double x, double y, Maze maze) {
		this.posX = x;
		this.posY = y;
		this.direction = 0;
		this.maze = maze;
		this.fitness = 0;
		this.lastMinSensorValue = 0;
		maze.updatePosition(x, y);

		grid = new boolean[GRIDSIZE][GRIDSIZE];

	}

	public void move(double[] velocity) {
		// gets information from the NN Vr and Vl
		// change vL and vR to values from -1to 1
		double vL = (velocity[0] - 0.5) * 2;
		double vR = (velocity[1] - 0.5) * 2;
		double x = 0;
		double y = 0;
		double theta = 0.2;
		double deltat = 0.1;
		double w;
		double r;
		double iccX;
		double iccY;
		if (vR == vL) {
			// just move forward;
		} else {
			r = (l / 2) * ((vL + vR) / (vR - vL));
			w = (vR - vL) / l;
			iccX = x - r * Math.sin(theta);
			iccY = y + r * Math.cos(theta);

		}

		boolean colision = maze.updatePosition(x, y);
		if (colision) {
			// decrease fitnesfunction
			fitness -= COLISIONDECREASE;
		} else {
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
			double i = 1;
			if (lastMinSensorValue < 6) {
				i = lastMinSensorValue / 6.;

			}
			fitness += v * (1 - Math.sqrt(deltaV)) * i;
			posX = x;
			posY = y;
			// direction has to be update too
		}
	}

	public double[] getSensorValues() {
		double[] sensors = maze.calculateSensorValues();
		// length of array is 15 not 12 because last 3 inputs are posX, posY and
		// direction.
		// double[] tmp = maze.calculateSensorValues();
		// for (int i = 0; i < tmp.length; i++) {
		// sensors[i] = tmp[i];
		// }
		lastMinSensorValue = sensors[0];
		for (int i = 1; i < sensors.length; i++) {
			if (sensors[i] < lastMinSensorValue) {
				lastMinSensorValue = sensors[i];
			}
		}
		sensors[12] = posX;
		sensors[13] = posY;
		sensors[14] = direction;
		return sensors;
		// return maze.calculateSensorValues();
	}

	public void updateFitness(double oldX, double oldY, double x, double y) {
		// take a look in the grid and see how much (%) is visited
		// update the x and y coordinates to values fitting at the grid!??
		// search activate the single parts in the grid --> so calcu�ate a route.
		// mapping of position:
		double width = GRIDSIZE / maze.getMaxX();// is the width of onr cell
		double height = GRIDSIZE / maze.getMaxY();
		double fromX = oldX * width;
		double fromY = oldY * height;
		double toX = x * width;
		double toY = y * height;
		// int xG
		// maze.getMaxX()
	}

	public double getFitness() {
		return 0;
	}

	public int getL() {
		return l;
	}

	public void setVisualizer(Visualizer visulizer) {
		this.visualizer = visulizer;
	}

}
