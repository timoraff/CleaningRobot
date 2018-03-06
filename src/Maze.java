import com.sun.javafx.geom.Edge;

import java.util.HashSet;
import java.util.Set;

public class Maze {
	private double minX, minY;
	private double maxX, maxY;
	private static Set<Edges> environment;
	// private Set<Edges> obstacle;
	private Coords currentRobotsPosition;

	/**
	 * contains the coordinate system, including the walls also takes care about
	 * displaying the maze and robot movement
	 */
	Maze() {
		minX = minY = 0.0;
		maxX = maxY = 30.0;
		environment = new HashSet<>();
		// wall
		environment.add(new Edges(new Coords(minX, minY), new Coords(maxX, minY), 1)); // bottom line
		environment.add(new Edges(new Coords(maxX, minY), new Coords(maxX, maxY), -1)); // top line
		environment.add(new Edges(new Coords(maxX, maxY), new Coords(minX, maxY), -1)); // right line
		environment.add(new Edges(new Coords(minX, maxY), new Coords(minX, minY), 1)); // left line

		// obstacle
		environment.add(new Edges(new Coords(minX + 6, minY + 6), new Coords(maxX - 6, minY + 6), -1)); // bottom line
		environment.add(new Edges(new Coords(maxX - 6, minY + 6), new Coords(maxX - 6, maxY - 6), -1)); // left line
		environment.add(new Edges(new Coords(maxX - 6, maxY - 6), new Coords(minX + 6, maxY - 6), 1)); // top line
		environment.add(new Edges(new Coords(minX + 6, maxY - 6), new Coords(minX + 6, minY + 6), 1)); // right line

		currentRobotsPosition = new Coords(minX, minY);
	}

	public void updatePosition(double x, double y) {
		// check outside of environment
		currentRobotsPosition.setX(x);
		currentRobotsPosition.setY(y);
	}

	public Coords getCorrectPosition(Coords oldPos, Coords newPos) {
		Coords tmpPos = new Coords(newPos.getX(), newPos.getY());
		double angle = Math.atan2(newPos.getY() - oldPos.getY(), newPos.getX() - oldPos.getX());
		double degrees = Math.toDegrees(angle);
		if (degrees < 0)
			degrees += 360;
		else if (degrees > 360)
			degrees -= 360;
		newPos.setAngle(degrees);

		for (Edges wall : environment) {
			double wallFromX = wall.getFrom().getX() + wall.getShift();
			double wallFromY = wall.getFrom().getY() + wall.getShift();
			double wallToX = wall.getTo().getX() + wall.getShift();
			double wallToY = wall.getTo().getY() + wall.getShift();

			// ray
			double A1;
			double B1 = 0;
			if (oldPos.getX() == newPos.getX()) {
				A1 = Integer.MAX_VALUE;
			} else if (oldPos.getY() == newPos.getY()) {
				A1 = 0;
			} else {
				A1 = (newPos.getY() - oldPos.getY()) / (newPos.getX() - oldPos.getX());
				B1 = oldPos.getY() - oldPos.getX() * A1;
			}

			// edge
			double A2;
			double B2 = 0;
			if (wallToX == wallFromX) {
				A2 = Integer.MAX_VALUE;
			} else if (wallToY == wallFromY) {
				A2 = 0;
			} else {
				A2 = (wallToY - wallFromY) / (wallToX - wallFromX);
				B2 = wallFromY - wallFromX * A2;
			}

			// in case they are not parallel
			if (A1 != A2) {
				double x, y;
				if (A2 == Integer.MAX_VALUE && A1 == 0) {
					x = wallFromX;
					y = oldPos.getY();
				} else if (A1 == Integer.MAX_VALUE && A2 == 0) {
					x = oldPos.getX();
					y = wallFromY;
				} else if (A1 == 0) {
					y = oldPos.getY();
					x = (y - B2) / A2;
				} else if (A2 == 0) {
					y = wallFromY;
					x = (y - B1) / A1;
				} else if (A1 == Integer.MAX_VALUE) {
					x = oldPos.getX();
					y = x * A2 + B2;
				} else if (A2 == Integer.MAX_VALUE) {
					x = wallFromX;
					y = x * A1 + B1;
				} else {
					x = (B2 - B1) / (A1 - A2);
					y = A1 * x + B1;
				}

				double minX = Math.min(wallFromX, wallToX);
				double maxX = Math.max(wallFromX, wallToX);
				double minY = Math.min(wallFromY, wallToY);
				double maxY = Math.min(wallFromY, wallToY);
				if (minX <= x && x <= maxX && minY <= y && y <= maxY) {
					if (0.0 < degrees && 90.0 > degrees) {
						if (x > oldPos.getX() && y > oldPos.getY()) {
							tmpPos.setX(x);
							tmpPos.setY(y);
							break;
						}
					} else if (90.0 < degrees && 180.0 > degrees) {
						if (x < oldPos.getX() && y > oldPos.getY()) {
							tmpPos.setX(x);
							tmpPos.setY(y);
							break;
						}

					} else if (180.0 < degrees && 270.0 > degrees) {
						if (x < oldPos.getX() && y < oldPos.getY()) {
							tmpPos.setX(x);
							tmpPos.setY(y);
							break;
						}

					} else if (270.0 < degrees && 360.0 > degrees) {
						if (x > oldPos.getX() && y < oldPos.getY()) {
							tmpPos.setX(x);
							tmpPos.setY(y);
							break;
						}

					} else if (degrees == 0) {
						if (x > oldPos.getX() && y == oldPos.getY()) {
							tmpPos.setX(x);
							tmpPos.setY(y);
							break;
						}
					} else if (degrees == 90) {
						if (x == oldPos.getX() && y > oldPos.getY()) {
							tmpPos.setX(x);
							tmpPos.setY(y);
							break;
						}
					} else if (degrees == 180) {
						if (x < oldPos.getX() && y == oldPos.getY()) {
							tmpPos.setX(x);
							tmpPos.setY(y);
							break;
						}
					} else {
						if (x == oldPos.getX() && y < oldPos.getY()) {
							tmpPos.setX(x);
							tmpPos.setY(y);
							break;
						}
					}
				}
			}
		}
		return tmpPos;
	}

	public double[] calculateSensorValues() {
		double sensors[] = new double[15];
		double[] degrees = { 0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330 };
		for (int i = 0; i < degrees.length; i++) {
			sensors[i] = Double.MAX_VALUE;
		}

		double RX = currentRobotsPosition.getX();
		double RY = currentRobotsPosition.getY();

		for (int i = 0; i < degrees.length; i++) {
			for (Edges wall : environment) {

				// ray
				double A1;
				double B1 = 0;
				if (degrees[i] == 90 || degrees[i] == 270) {
					A1 = Integer.MAX_VALUE;
				} else if (degrees[i] == 180 || degrees[i] == 0) {
					A1 = 0;
				} else {
					A1 = Math.tan(Math.toRadians(degrees[i]));
					B1 = RY - RX * A1;
				}

				// edge
				double A2;
				double B2 = 0;
				if (wall.getTo().getX() == wall.getFrom().getX()) {
					A2 = Integer.MAX_VALUE;
				} else if (wall.getTo().getY() == wall.getFrom().getY()) {
					A2 = 0;
				} else {
					A2 = (wall.getTo().getY() - wall.getFrom().getY()) / (wall.getTo().getX() - wall.getFrom().getX());
					B2 = wall.getFrom().getY() - wall.getFrom().getX() * A2;
				}

				// in case they are not parralel
				if (A1 != A2) {
					double x, y;
					if (A2 == Integer.MAX_VALUE && A1 == 0) {
						x = wall.getFrom().getX();
						y = RY;
					} else if (A1 == Integer.MAX_VALUE && A2 == 0) {
						x = RY;
						y = wall.getFrom().getY();
					} else if (A1 == 0) {
						y = RY;
						x = (y - B2) / A2;
					} else if (A2 == 0) {
						y = wall.getFrom().getY();
						x = (y - B1) / A1;
					} else if (A1 == Integer.MAX_VALUE) {
						x = RX;
						y = x * A2 + B2;
					} else if (A2 == Integer.MAX_VALUE) {
						x = wall.getFrom().getX();
						y = x * A1 + B1;
					} else {
						x = (B2 - B1) / (A1 - A2);
						y = A1 * x + B1;
					}

					double minX = Math.min(wall.getFrom().getX(), wall.getTo().getX());
					double maxX = Math.max(wall.getFrom().getX(), wall.getTo().getX());
					double minY = Math.min(wall.getFrom().getY(), wall.getTo().getY());
					double maxY = Math.min(wall.getFrom().getY(), wall.getTo().getY());
					if (minX <= x && x <= maxX && minY <= y && y <= maxY) {
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

						} else if (degrees[i] == 0) {
							if (x > RX && y == RY) {
								sensors[i] = Math.min(sensors[i], distance);
							}
						} else if (degrees[i] == 90) {
							if (x == RX && y > RY) {
								sensors[i] = Math.min(sensors[i], distance);
							}
						} else if (degrees[i] == 180) {
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

	public Set<Edges> getEnvironment() {
		return environment;
	}

	// public Set<Edges> getObstacle() {
	// return obstacle;
	// }

	public Coords getCurrentRobotsPosition() {
		return currentRobotsPosition;
	}
}
