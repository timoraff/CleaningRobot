/**
 * 
 * @author Timo Raff
 * 
 *         This class contains in general the evolutional algorithm. it
 *         contains: - the selection of individuals - the crossover (creation of
 *         new individuals) - the mutation of individuals
 */
public class Controller {
	private static final double mutationRate = 0.15;

	/**
	 * controls the evolution of a populatino
	 * 
	 * @param pop
	 *            the previous population
	 * @return returns a new (evolved) population with the same size like before.
	 */
	public static Population evolveEvolution(Population pop) {
		pop.test();
		System.out.println("max fitness: " + pop.getFittest().getFitness());
		Population newPop = new Population(0);
		SelectionStrategy selection = new TournamentSelection();

		newPop.add(pop.getFittest());

		// do crossover until population has the same size like before:
		while (newPop.getSize() < pop.getSize()) {
			NeuralNet indi1 = selection.select(pop);
			NeuralNet indi2 = selection.select(pop);
			NeuralNet newIndi = crossover(indi1, indi2);
			mutate(newIndi);
			newPop.add(newIndi);
		}
		return newPop;
	}

	/**
	 * the crossover just selects randomly the weights of one of the two parents
	 * 
	 * @param a
	 *            1st parent
	 * @param b
	 *            2nd parent
	 * @return returns a new individual (Neural net) as a combination from the the
	 *         two parents
	 */
	private static NeuralNet crossover(NeuralNet a, NeuralNet b) {
		NeuralNet indi = new NeuralNet(false);
		boolean isX = true;
		for (int i = 0; i < a.getWeightCount(); i++) {
			if (Math.random() < 0.5) {
				// System.out.println("weight a:" + a.getWeight(i));
				indi.setWeight(i, a.getWeight(i));
			} else {
				// System.out.println("weightb: " + b.getWeight(i));
				indi.setWeight(i, b.getWeight(i));
			}
		}
		return indi;
	}

	/**
	 * each weight of the individual is going to be replaced with a random weight
	 * with a certain probalility
	 * 
	 * @param indiv
	 *            the individual to be changed.
	 */
	private static void mutate(NeuralNet indiv) {
		for (int i = 0; i < indiv.getWeightCount(); i++) {
			if (Math.random() <= mutationRate) {
				// create a random weigth with value between -1 and 1
				double weight = (Math.random() - 0.5) * 2;
				indiv.setWeight(i, weight);
			}
		}
	}

}
