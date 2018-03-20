import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class Main {
	// number of evolutino steps
	final static int ITERATIONS = 100;
	// size of the starting population
	final static int STARTINGPOP = 100;
	// starting position for the robot
	
	final static double STARTINGX = 2;
	final static double STARTINGY = 2;
	/**
	 * set to true if you just want to run the agent with a recently created NN
	 */
	static boolean JUSTLOAD = false;
	/**
	 * set to true if you want to continue on the recently saved dataset
	 */
	static boolean CONTINUETRAINING = false;

	// just a main to execute the programm
	public static void main(String[] args) {
		Maze maze = new Maze();
		Robot robo = new Robot(STARTINGX, STARTINGY, maze);
		Visualizer visualizer = new Visualizer(maze, robo);
		robo.setVisualizer(visualizer);

		// let the fittest model play for some time --> for seeing a result..
		for (int i = 0; i < 200000; i++) {
			double []tmp= new double[] {1,2,3};
			robo.move(tmp);
			try {
				Thread.sleep(18);
				visualizer.update();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println(robo.getCurrentPosition().toString());
		}
	}

}
