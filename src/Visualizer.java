import javax.swing.*;
import java.awt.*;

class Visualizer extends JPanel {

	private static final double BORDER = 100.0;
	private static final double SCALE = 6.0;
	private Maze maze;
	private Robot robot;
	private int frameWidth;
	private int frameHeight;
	private JFrame frame;
	private Coords oldRoboCoords = new Coords(0, 0);

	public Visualizer(Maze maze, Robot robot) {

		this.maze = maze;
		this.robot = robot;
		createFrame();
	}

	private void createFrame() {

		frame = new JFrame("CleaningRobot");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	private void drawEnvironment(Graphics g) {

		for (Edges edge : maze.getEnvironment()) {

			int x1 = (int) (((double) edge.getFrom().getX()) * SCALE);
			int y2 = (int) (((double) edge.getFrom().getY()) * SCALE);
			int x2 = (int) (((double) edge.getTo().getX()) * SCALE);
			int y1 = (int) (((double) edge.getTo().getY()) * SCALE);

			g.drawLine(x1 + (int) BORDER, y1 + (int) BORDER, x2 + (int) BORDER, y2 + (int) BORDER);

		}
	}

	private void getFrameDimensions() {

		int maxX = 0;
		int maxY = 0;
		int minX = 0;
		int minY = 0;

		for (Edges edge : maze.getEnvironment()) {

			if (Math.max(edge.getFrom().getX(), edge.getTo().getX()) > maxX) {
				maxX = (int) Math.max(edge.getFrom().getX(), edge.getTo().getX());
			}
			if (Math.max(edge.getFrom().getY(), edge.getTo().getY()) > maxY) {
				maxY = (int) Math.max(edge.getFrom().getY(), edge.getTo().getY());
			}
			if (Math.min(edge.getFrom().getX(), edge.getTo().getX()) > minX) {
				minX = (int) Math.min(edge.getFrom().getX(), edge.getTo().getX());
			}
			if (Math.min(edge.getFrom().getY(), edge.getTo().getY()) > minY) {
				minY = (int) Math.min(edge.getFrom().getY(), edge.getTo().getY());
			}
		}

		frameWidth = (int) (maxX * SCALE + 2 * BORDER);
		frameHeight = (int) (maxY * SCALE + 2 * BORDER);
		frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
		frame.pack();
	}

	private void drawRobot(Graphics g) {

		Coords roboCoords = robot.getCurrentPosition();
		g.setColor(Color.RED);
		int l = robot.getL();
		int finalX = (int) (BORDER + (roboCoords.getX() - l/2.0) * SCALE);
		int finalY = (int) (BORDER + (roboCoords.getY() - l/2.0) * SCALE);
		int finalWidth = (int) (l * SCALE);
		g.fillOval(finalX, finalY, finalWidth, finalWidth);
		if (oldRoboCoords != null) {
			double degrees = getDegrees(oldRoboCoords, roboCoords);
			int x = (int)(finalX+finalWidth/2.0 + (finalWidth/2.0)*Math.cos(Math.toRadians(degrees)));
			int y = (int)(finalY+finalWidth/2.0 + (finalWidth/2.0)*Math.sin(Math.toRadians(degrees)));
			g.setColor(Color.WHITE);
			int size = (int)(l/4.0 * SCALE);
			g.fillOval((int)(x-size/2.0), (int)(y-size/2.0), size, size);
		}
		oldRoboCoords.setX(roboCoords.getX());
		oldRoboCoords.setY(roboCoords.getY());
	}
	
	private double getDegrees(Coords oldC, Coords newC) {
		
		double dx = oldC.getX() - newC.getX();
		double dy = oldC.getY() - newC.getY();
				
		if (dx == 0 && dy == 0) {
			return 0;
		} else if (dx == 0 && dy < 0) {
			return 90;
		} else if (dx == 0 && dy > 0) {
			return 270;
		} else if (dy == 0 && dx < 0) {
			return 0;
		} else if (dy == 0 && dx > 0) {
			return 180;
		} else if (dy < 0 && dx < 0) {
			return 90-Math.atan(Math.abs(dx) / Math.abs(dy)) * 180 / Math.PI;
		} else if (dy > 0 && dx < 0) {
			return 270 + Math.atan(Math.abs(dx) / Math.abs(dy)) * 180 / Math.PI;
		} else if (dy > 0 && dx > 0) {
			return 270 - Math.atan(Math.abs(dx) / Math.abs(dy)) * 180 / Math.PI;
		} else {
			return 90 + Math.atan(Math.abs(dx) / Math.abs(dy)) * 180 / Math.PI;
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		getFrameDimensions();

		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(0, frameHeight);
		g2d.scale(1.0, -1.0);

		drawEnvironment(g);
		drawRobot(g);
	}

	public void update() {
		repaint();
	}
}