import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class Main {
	// number of evolutino steps
	final static int ITERATIONS = 20;
	// size of the starting population
	final static int STARTINGPOP = 140;
	// starting position for the robot
	final static double STARTINGX = 2;
	final static double STARTINGY = 2;
	static boolean JUSTLOAD = true;
	static boolean CONTINUETRAINING = true;

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
				FileOutputStream fileOut = new FileOutputStream("/tmp/employee.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(pop);
				out.close();
				fileOut.close();
				System.out.printf("Serialized data is saved in /tmp/employee.ser");
			} catch (IOException i) {
				i.printStackTrace();
			}
		}
		Population p;
		try {
			FileInputStream fileIn = new FileInputStream("/tmp/employee.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			p = (Population) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("Employee class not found");
			c.printStackTrace();
			return;
		}

		if (CONTINUETRAINING && !JUSTLOAD) {
			for (int i = 0; i < ITERATIONS; i++) {
				System.out.println("Evo No: " + i);
				p = Controller.evolveEvolution(p);
			}

			try {
				FileOutputStream fileOut = new FileOutputStream("/tmp/employee.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(p);
				out.close();
				fileOut.close();
				System.out.printf("Serialized data is saved in /tmp/employee.ser");
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
				Thread.sleep(13);
				visualizer.update();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println(robo.getCurrentPosition().toString());
		}
	}

}
