
public class Main {
	// starting position for the robot
	private final static double STARTINGX = 15;
	private final static double STARTINGY = 15;
	private final static double ROBOTDIAMETER = 2; 	// diamter of the robot
	private final static double BEACONRANGE = 14; 	// beacon range
        private final static double MAZEWIDTH = 40;     // widtth of the maze
        private final static double MAZEHEIGHT = 30;     // height of the maze
        
	// just a main to execute the programm
	public static void main(String[] args) {
		Maze maze = new Maze(0, 0, MAZEWIDTH, MAZEHEIGHT, ROBOTDIAMETER, BEACONRANGE);
		Robot robo = new Robot(STARTINGX, STARTINGY, maze, ROBOTDIAMETER, 1);
		Visualizer visualizer = new Visualizer(maze, robo, ROBOTDIAMETER, BEACONRANGE);
		maze.setRobotsCurrentPosition(new Coords(STARTINGX, STARTINGY));

		// let the fittest model play for some time --> for seeing a result..
		while(true) {
			robo.move();
			try {
                                // slow down program a little
				Thread.sleep(30);
				visualizer.update();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
