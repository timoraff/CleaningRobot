import java.util.Arrays;

import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;

public class Robot {
	// contains movement methods for the Robot and i think the position ?
	int l = 2; // distance between the 2 wheels
	double posX; // Coordinates in the maze
	double posY;
	double direction;
	Maze maze;

	Robot(double x, double y, Maze maze) {
		this.posX = x;
		this.posY = y;
		this.direction = 0;
		this.maze = maze;
		maze.updatePosition(x, y);
	}

	public void move(double[] velocity) {
		// gets information from the NN Vr and Vl
		double vL = velocity[0];
		double vR = velocity[1];
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

		maze.updatePosition(x, y);
	}

	public double[] getSensorValues() {
//		double[] sensors = new double[15];
		// length of array is 15 not 12 because last 3 inputs are posX, posY and
		// direction.
//		double[] tmp = maze.calculateSensorValues();
//		for (int i = 0; i < tmp.length; i++) {
//			sensors[i] = tmp[i];
//		}
//		sensors[12] = posX;
//		sensors[13] = posY;
//		sensors[14] = direction;
//		return sensors;
		return maze.calculateSensorValues();
	}

}
