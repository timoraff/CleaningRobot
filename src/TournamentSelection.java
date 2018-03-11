
public class TournamentSelection implements SelectionStrategy {
final static int GroupCount=10;
	@Override
	public NeuralNet select(Population pop) {
		Population tmp = new Population(0);
		for(int i =0; i< GroupCount;i++) {
			tmp.add(pop.get((int)(Math.random()*pop.getSize())));
		}
		return tmp.getFittest();
	}

}
