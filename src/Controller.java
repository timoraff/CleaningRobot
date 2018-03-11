
public class Controller {
	private static final double mutationRate = 0.15;

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

	// prob of 50% to select one chromosom
	private static NeuralNet crossover(NeuralNet a, NeuralNet b) {
		NeuralNet indi = new NeuralNet(false);
		boolean isX = true;
		for (int i = 0; i < a.getWeightCount(); i++) {// weightscount =15*2
			if (Math.random() < 0.5) {
				//System.out.println("weight a:" + a.getWeight(i));
				indi.setWeight(i, a.getWeight(i));
			} else {
				//System.out.println("weightb: " + b.getWeight(i));
				indi.setWeight(i, b.getWeight(i));
			}
		}
		return indi;
	}

	private static void mutate(NeuralNet indiv) { // Loop through genes boolean
		for (int i = 0; i < indiv.getWeightCount(); i++) {
			if (Math.random() <= mutationRate) { // Create random weight
				double weight = Math.random(); //
				indiv.setWeight(i, weight);
			}
		}
	}

}
