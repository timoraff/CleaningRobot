
public class Maze {
    private double maxX;
    private double maxY;

    /**
     * contains the coordinate system, including the walls
     * also takes care about displaying the maze and robot movement
     */
    public Maze() {
        maxX = 30.0;
        maxY = 30.0;
    }
	public boolean updatePosition(double x, double y) {
        return !(x < 0) && !(x > maxX) && !(y < 0) && !(y > maxY);
    }

}
