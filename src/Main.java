
public class Main {
	// number of evolutino steps
	final static int ITERATIONS = 100;
	// size of the starting population
	final static int STARTINGPOP = 100;
	// starting position for the robot
	private final static double STARTINGX = 15;
	private final static double STARTINGY = 15;
	// diamter of the robot
	private final static double ROBOTDIAMETER = 2;
	// beacon range
	private final static double BEACONRANGE = 15;
	// just a main to execute the programm
	public static void main(String[] args) {
		Maze maze = new Maze(0, 0, 40, 30, ROBOTDIAMETER, BEACONRANGE);
		Robot robo = new Robot(STARTINGX, STARTINGY, maze, ROBOTDIAMETER);
		Visualizer visualizer = new Visualizer(maze, robo, ROBOTDIAMETER, BEACONRANGE);
		robo.setVisualizer(visualizer);

		// let the fittest model play for some time --> for seeing a result..
		while(true) {
			// double []tmp= new double[] {1,2,3};
			robo.move();
			try {
				visualizer.update();
                Thread.sleep(30);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println(robo.getCurrentPosition().toString());
		}
	}

}
