
public class Main {
	final static int ITERATIONS = 100;
	final static int STARTINGPOP = 80;
	final static double STARTINGX = 2;
	final static double STARTINGY = 2;

	// just a main to execute the programm
	public static void main(String[] args) {
		// how to get the correct position for the robot
		Maze maze = new Maze();
		Robot robo = new Robot(STARTINGX, STARTINGY, maze);
//		Visualizer visualizer = new Visualizer(maze, robo);
//                robo.setVisualizer(visualizer);

		/*
		 * init a new population with size STARTINGPOP evolve this population for a
		 * number of "ITERATIONS" iterations
		 */
		/*
		Population pop = new Population(STARTINGPOP);
		for (int i = 0; i < ITERATIONS; i++) {
			System.out.println("Evo No: " + i + 1);
			pop = Controller.evolveEvolution(pop);
		}

		// let the fittest model play for some time --> for seeing a result..
		NeuralNet fittest = pop.getFittest();
		for (int i = 0; i < 200; i++) {
			robo.move(fittest.calculate(robo.getSensorValues()));
		}
*/
		Coords from = new Coords(2.0, 2.0);
		Coords to = new Coords(4.0, 4.0);
		boolean pos = maze.getCorrectPosition(from, to);
        System.out.println(pos);

		from.setX(4.0);
		from.setY(4.0);
		to.setX(2.0);
		to.setY(2.0);
        pos = maze.getCorrectPosition(from, to);
        System.out.println(pos);

        maze.calculateSensorValues(to);

		from.setX(2.0);
		from.setY(2.0);
		to.setX(4.0);
		to.setY(7.0);
        pos = maze.getCorrectPosition(from, to);
        System.out.println(pos);
	}

}
