/**
 * 
 * @author Timo Raff
 * selectino strategy interface for the selection of individuals for the EA
 */
public interface SelectionStrategy {
	
	public NeuralNet select(Population pop);
}
