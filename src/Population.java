import java.util.ArrayList;

/**
 * 
 * @author Timo Raff
 *
 *         contains a population of neural networks
 */
public class Population {

	ArrayList<NeuralNet> pop = new ArrayList<>();

	public Population(int count) {
		initPopulation(count);
	}

	/**
	 * initializes a random population with "count" individuals (Neural Networks)
	 * 
	 * @param count
	 */
	public void initPopulation(int count) {
		for (int i = 0; i < count; i++) {
			pop.add(new NeuralNet(true));
		}
	}

	/**
	 * tests each neural network - calculates also he fitness function
	 */
	public void test() {
		for (NeuralNet nn : pop) {
			nn.test();
		}
	}

	/**
	 * searches for the fittest model
	 * 
	 * @return the fttest model
	 */
	public NeuralNet getFittest() {
		NeuralNet fittest = null;
		for (NeuralNet indi : pop) {
			// searching for max fitness
			if (fittest == null || indi.getFitness() > fittest.getFitness()) {
				fittest = indi;
			}
		}
		return fittest;
	}

	public void add(NeuralNet i) {
		pop.add(i);
	}

	public NeuralNet get(int i) {
		return pop.get(i);
	}

	public int getSize() {
		return pop.size();
	}
}
