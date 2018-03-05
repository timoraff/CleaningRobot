import javax.swing.*;
import java.awt.*;

class Visualizer extends JPanel {
	
	private static final double BORDER = 100.0;
	private static final double SCALE = 15.0;
	private Maze maze;
        private int frameWidth;
        private int frameHeight;
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
		
		for (Edges edge : maze.getEnvironment()) {
			
			int x1 = (int)(((double)edge.getFrom().getX())*SCALE);
			int y2 = (int)(((double)edge.getFrom().getY())*SCALE);
			int x2 = (int)(((double)edge.getTo().getX())*SCALE);
			int y1 = (int)(((double)edge.getTo().getY())*SCALE);
			
			g.drawLine(x1+(int)BORDER, y1+(int)BORDER, x2+(int)BORDER, y2+(int)BORDER);
			
                }
	}
        
    private void getFrameDimensions() {
        
        int maxX = 0;
        int maxY = 0;
        int minX = 0;
        int minY = 0;

        for (Edges edge : maze.getEnvironment()) {

                if (Math.max(edge.getFrom().getX(), edge.getTo().getX()) > maxX) {
                        maxX = (int)Math.max(edge.getFrom().getX(), edge.getTo().getX());
                }
                if (Math.max(edge.getFrom().getY(), edge.getTo().getY()) > maxY) {
                        maxY = (int)Math.max(edge.getFrom().getY(), edge.getTo().getY());
                }
                if (Math.min(edge.getFrom().getX(), edge.getTo().getX()) > minX) {
                        minX = (int)Math.min(edge.getFrom().getX(), edge.getTo().getX());
                }
                if (Math.min(edge.getFrom().getY(), edge.getTo().getY()) > minY) {
                        minY = (int)Math.min(edge.getFrom().getY(), edge.getTo().getY());
                }
        }

        frameWidth = (int)(maxX*SCALE+2*BORDER);
        frameHeight = (int)(maxY*SCALE+2*BORDER);
        frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
        frame.pack();
    }
        
    private void drawRobot(Graphics g) {
            
        Coords roboCoords = maze.getCurrentRobotsPosition();
        g.setColor(Color.RED);
        g.fillRect((int)(BORDER+roboCoords.getX()*SCALE-SCALE), (int)(BORDER+roboCoords.getY()*SCALE-SCALE), (int)SCALE, (int)SCALE);
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
}