import javax.swing.*;
import java.awt.*;

class Visualizer extends JPanel {
	
	private static final double BORDER = 100.0;
	private static final double SCALE = 15.0;
	private int frameWidth;
	private int frameHeight;
	private Maze maze;
	private JFrame frame;
	
	public Visualizer(Maze maze) {
		
		this.maze = maze;
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
		
		int maxX = 0;
		int maxY = 0;

		
		for (Edges edge : maze.getEnvironment()) {
			
			int x1 = (int)(((double)edge.getFrom().getX())*SCALE+BORDER);
			int y1 = (int)(((double)edge.getFrom().getY())*SCALE+BORDER);
			int x2 = (int)(((double)edge.getTo().getX())*SCALE+BORDER);
			int y2 = (int)(((double)edge.getTo().getY())*SCALE+BORDER);
			
			g.drawLine(x1, y1, x2, y2);
			
			if (Math.max(x1, x2) > maxX) {
				maxX = Math.max(x1, x2);
				frameWidth = maxX + (int)BORDER;

			}
			if (Math.max(y1, y2) > maxY) {
				maxY = Math.max(y1, y2);
				frameHeight = maxY + (int)BORDER;
			}
		}
                
        frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
		frame.pack();
	}
        
    private void drawRobot(Graphics g) {
            
    }
	
	private void drawObstacles(Graphics g) {
		
		for (Edges edge : maze.getObstacle()) {
			
			int x1 = (int)(((double)edge.getFrom().getX())*SCALE+BORDER);
			int y1 = (int)(((double)edge.getFrom().getY())*SCALE+BORDER);
			int x2 = (int)(((double)edge.getTo().getX())*SCALE+BORDER);
			int y2 = (int)(((double)edge.getTo().getY())*SCALE+BORDER);
			
			g.drawLine(x1, y1, x2, y2);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		drawEnvironment(g);
		drawObstacles(g);
        drawRobot(g);
	}
}