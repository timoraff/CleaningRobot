import java.util.Arrays;

public class Main {
	final static int ITERATIONS = 200;
	final static int STARTINGPOP = 150;
	final static double STARTINGX = 2;
	final static double STARTINGY = 2;

	// just a main to execute the programm
	public static void main(String[] args) {
		/*
		 * init a new population with size STARTINGPOP evolve this population for a
		 * number of "ITERATIONS" iterations
		 */
		Population pop = new Population(STARTINGPOP);
		for (int i = 0; i < ITERATIONS; i++) {
			System.out.println("Evo No: " + i );
			pop = Controller.evolveEvolution(pop);
		}
		
		// how to get the correct position for the robot
		Maze maze = new Maze();
		Robot robo = new Robot(STARTINGX, STARTINGY, maze);
		Visualizer visualizer = new Visualizer(maze, robo);
		robo.setVisualizer(visualizer);

		// let the fittest model play for some time --> for seeing a result..
		NeuralNet fittest = pop.getFittest();
		for (int i = 0; i < 200; i++) {
			double[] tmp = fittest.calculate(robo.getSensorValues());
			System.out.println("Move: "+Arrays.toString(tmp));
			robo.move(tmp/*fittest.calculate(robo.getSensorValues())*/);
			try {
				Thread.sleep(100);
				visualizer.update();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println(robo.getCurrentPosition().toString());
		}

//		Coords from = new Coords(2.0, 2.0);
//		Coords to = new Coords(2.0, -2.0);
//		boolean pos = maze.checkForCollision(from, to);
//		System.out.println(pos);
//
//		from.setX(2.0);
//		from.setY(2.0);
//		to.setX(10.0);
//		to.setY(10.0);
//		pos = maze.checkForCollision(from, to);
//		System.out.println(pos);
//
//		from.setX(2.0);
//		from.setY(2.0);
//		to.setX(2.1);
//		to.setY(1.99);
//		pos = maze.checkForCollision(from, to);
//		System.out.println(pos);
	}

}
