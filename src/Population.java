import java.util.ArrayList;

public class Population {

	ArrayList<NeuralNet> pop = new ArrayList<>();

	public Population(int count) {
		initPopulation(count);
	}

	public void initPopulation(int count) {
		for (int i = 0; i < count; i++) {
			pop.add(new NeuralNet(true));
		}
	}

	public void test() {
		for (NeuralNet nn : pop) {
			nn.test();
		}
	}

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
	/*
	 * public void print() { NeuralNet i = getFittest();
	 * System.out.println("Maximum fittnes: "+i.getFitness()+"   -At X: "+i.getX()
	 * +" Y: "+i.getX()); }
	 */
}
