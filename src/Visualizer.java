import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.Polygon;

/**
 * @author Pit Schneider i6173083
 */

class Visualizer extends JPanel {

	private static final double BORDER = 100.0; // border between start of the window and maze (all 4 sides)
	private static final double SCALE = 15.0; // everything is scaled according to this constant
	private Maze maze;
	private Robot robot;
	private int frameWidth;
	private int frameHeight;
	private double l;
	private double beaconRange;
	private JFrame frame;
	private Coords currentRoboCoords = new Coords(0, 0);
	private Coords oldRoboCoords = new Coords(0, 0);
	private ArrayList<Coords> covered = new ArrayList<>(); // saves all visited positions of the robot

	public Visualizer(Maze maze, Robot robot, double l, double beaconRange) {

		this.l = l;
		this.beaconRange = beaconRange;
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

	// draws maze with all edges
	private void drawEnvironment(Graphics g) {

		for (Edges edge : maze.getEnvironment()) {

			int x1 = (int) (((double) edge.getFrom().getX()) * SCALE);
			int y2 = (int) (((double) edge.getFrom().getY()) * SCALE);
			int x2 = (int) (((double) edge.getTo().getX()) * SCALE);
			int y1 = (int) (((double) edge.getTo().getY()) * SCALE);

			g.drawLine(x1 + (int) BORDER, y1 + (int) BORDER, x2 + (int) BORDER, y2 + (int) BORDER);

		}
	}

	// determine the dimensions of the window
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

	// draws robot and the current moving direction
	private void drawRobot(Graphics g) {

		// draws actual robot as a circle
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.RED);
		int finalX = (int) (BORDER + (currentRoboCoords.getX() - l/2.0) * SCALE);
		int finalY = (int) (BORDER + (currentRoboCoords.getY() - l/2.0) * SCALE);
		int finalWidth = (int) (l * SCALE);
		g2.fillOval(finalX, finalY, finalWidth, finalWidth);

		// in case we have a moving direction
		if (oldRoboCoords != null) {
			// moving direction is obtained by comparing old and current coordinates
			double degrees = getDegrees(oldRoboCoords, currentRoboCoords);
			int x = (int)(finalX+finalWidth/2.0 + (finalWidth/2.0-(l/8.0)*SCALE)*Math.cos(Math.toRadians(degrees)));
			int y = (int)(finalY+finalWidth/2.0 + (finalWidth/2.0-(l/8.0)*SCALE)*Math.sin(Math.toRadians(degrees)));
			g2.setColor(Color.WHITE);
			int size = (int)(l/4.0 * SCALE);
			g2.fillOval((int)(x-size/2.0), (int)(y-size/2.0), size, size);
		}
		oldRoboCoords.setX(currentRoboCoords.getX());
		oldRoboCoords.setY(currentRoboCoords.getY());
	}
	
	// returns the angle of a line between two points
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
	
	public void drawBeacons(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		for (Coords beacon : robot.beaconsInRange()) {
			int finalX = (int) (BORDER + (beacon.getX() - beaconRange) * SCALE);
			int finalY = (int) (BORDER + (beacon.getY() - beaconRange) * SCALE);
			int finalWidth = (int) (beaconRange*2 * SCALE);
			g2.setComposite(AlphaComposite.SrcOver.derive(0.2f));
			g2.setColor(Color.BLUE);
			g2.fillOval(finalX, finalY, finalWidth, finalWidth);
			g2.setComposite(AlphaComposite.SrcOver.derive(1.0f));
			double beaconSize = 0.5;
			int x = (int) (BORDER + (beacon.getX() - beaconSize/2.0) * SCALE);
			int y = (int) (BORDER + (beacon.getY() - beaconSize/2.0) * SCALE);
			g2.setColor(Color.BLUE);
			g2.fillOval(x, y, (int)(beaconSize*SCALE), (int)(beaconSize*SCALE));

		}
	}
	
	// trace of robot
	public void drawCovered(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.YELLOW);
		Coords oldCoords = new Coords(Integer.MIN_VALUE, Integer.MIN_VALUE);
		
		// for every past robot position
		for (Coords coords : covered) {
			
			int finalX = (int) (BORDER + (coords.getX() - l/2.0) * SCALE);
			int finalY = (int) (BORDER + (coords.getY() - l/2.0) * SCALE);
			int finalWidth = (int) (l * SCALE);
			
			// draw area covered by robot on position
			g2.drawOval(finalX, finalY, finalWidth, finalWidth);
			
			if(oldCoords.getX()!=Integer.MIN_VALUE) {
				
				double degrees = getDegrees(oldCoords, coords);
				double endX1 = coords.getX() + (l/2.0)*Math.cos(Math.toRadians(degrees+270));
				double endY1 = coords.getY() + (l/2.0)*Math.sin(Math.toRadians(degrees+270));
				
				double endX2 = coords.getX() + (l/2.0)*Math.cos(Math.toRadians(degrees+90));
				double endY2 = coords.getY() + (l/2.0)*Math.sin(Math.toRadians(degrees+90));
				
				double startX1 = oldCoords.getX() + (l/2.0)*Math.cos(Math.toRadians(degrees+90));
				double startY1 = oldCoords.getY() + (l/2.0)*Math.sin(Math.toRadians(degrees+90));
				
				double startX2 = oldCoords.getX() + (l/2.0)*Math.cos(Math.toRadians(degrees-90));
				double startY2 = oldCoords.getY() + (l/2.0)*Math.sin(Math.toRadians(degrees-90));
					
				// rectangle fills area between consecutive robot positions to avoid gaps in the trace	
				Polygon p = new Polygon();
				p.addPoint((int)endX1, (int)endY1);
				p.addPoint((int)endX2, (int)endY2);
				p.addPoint((int)startX1, (int)startY1);
				p.addPoint((int)startX2, (int)startY2);
				g.fillPolygon(p);
			}
			oldCoords.setX(coords.getX());
			oldCoords.setY(coords.getY());
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		// determine the needed size of the window
		getFrameDimensions();

		// translates the java swing coordinate system origin, which is top left, to top right (mathematically easier)
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(0, frameHeight);
		g2d.scale(1.0, -1.0);

		// draw everything
		drawEnvironment(g);
		drawBeacons(g);
		drawCovered(g);
		drawRobot(g);
	}

	// robot has a new position
	public void update() {
		currentRoboCoords = maze.getRobotsCurrentPosition();
		covered.add(new Coords(robot.getBelief().getX(), robot.getBelief().getY()));
		if (covered.size() > 80) {
			covered.remove(0);
		}
		repaint();
	}
}