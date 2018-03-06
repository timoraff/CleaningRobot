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
	Visualizer visulizer;

	Robot(double x, double y, Maze maze) {
		this.posX = x;
		this.posY = y;
		this.direction = 0;
		this.maze = maze;
		this.fitness = 0;
		this.lastMinSensorValue = 0;
		maze.updatePosition(x, y);
		// TODO maybe change the size of the grid;
		grid = new boolean[500][500];

	}

	public void move(double[] velocity) {
		// gets information from the NN Vr and Vl
		// change vL and vR to values from -1to 1
		double vL = (velocity[0] - 0.5) * 2;
		double vR = (velocity[1] - 0.5) * 2;
		double x = posX;
		double y = posY;
		double theta = direction;
		double deltat = 0.3;
		double w;
		double r;
		double iccX;
		double iccY;
		double newx = 0;
		double newy =0;
		double newtheta = 0;
		if (vR == vL) {
			x = x + Math.cos(theta)*vR*deltat;
			y = y + Math.sin(theta)*vR*deltat;
			// just move forward;
		} else {
			r = (l / 2) * ((vL + vR) / (vR - vL));
			w = (vR - vL) / l; //evtl problem mit Rad und Deg?
			iccX = x - r * Math.sin(Math.toRadians(theta));
			iccY = y + r * Math.cos(Math.toRadians(theta));
			newx = Math.cos(w * deltat) * (x - iccX) - (Math.sin(w*deltat)*(y-iccY)) + iccX;
			newy = Math.sin(w * deltat) * (x - iccX) + (Math.cos(w*deltat)*(y-iccY)) + iccY;
			newtheta = theta + w*deltat;
			x = newx;
			y = newy;
			theta= newtheta;
		}
		

		boolean colision = maze.updatePosition(x, y);
		if (colision) {
			// decrease fitnesfunction
			fitness -= COLISIONDECREASE;
		} else {
			// alternative fitness function:
			// updateFitness(x,y);
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
			direction = theta;
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

	public void updateFitness(double x, double y) {
		// take a look in the grid and see how much (%) is visited
		// update the x and y coordinates to values fitting at the grid!??
		// search activate the single parts in the grid --> so calcuöate a route.
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
