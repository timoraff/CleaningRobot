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
		/*
		 * init a new population with size STARTINGPOP evolve this population for a
		 * number of "ITERATIONS" iterations
		 */
		if (!JUSTLOAD && !CONTINUETRAINING) {
			Population pop = new Population(STARTINGPOP);
			for (int i = 0; i < ITERATIONS; i++) {
				System.out.println("Evo No: " + i);
				pop = Controller.evolveEvolution(pop);
			}

			try {
				FileOutputStream fileOut = new FileOutputStream("/tmp/population.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(pop);
				out.close();
				fileOut.close();
				System.out.printf("Serialized data is saved in /tmp/population.ser");
			} catch (IOException i) {
				i.printStackTrace();
			}
		}
		Population p;
		try {
			FileInputStream fileIn = new FileInputStream("/tmp/population.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			p = (Population) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("population class not found");
			c.printStackTrace();
			return;
		}

		if (CONTINUETRAINING && !JUSTLOAD) {
			for (int i = 0; i < ITERATIONS; i++) {
				System.out.println("Evo No: " + i);
				p = Controller.evolveEvolution(p);
			}

			try {
				FileOutputStream fileOut = new FileOutputStream("/tmp/population.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(p);
				out.close();
				fileOut.close();
				System.out.printf("Serialized data is saved in /tmp/population.ser");
			} catch (IOException i) {
				i.printStackTrace();
			}
		}

		// how to get the correct position for the robot
		Maze maze = new Maze();
		Robot robo = new Robot(STARTINGX, STARTINGY, maze);
		Visualizer visualizer = new Visualizer(maze, robo);
		robo.setVisualizer(visualizer);

		// let the fittest model play for some time --> for seeing a result..
		NeuralNet fittest = p.getFittest();
		for (int i = 0; i < 200000; i++) {
			double[] tmp = fittest.calculate(robo.getSensorValues());
			System.out.println("Move: " + Arrays.toString(tmp));
			robo.move(tmp/* fittest.calculate(robo.getSensorValues()) */);
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
