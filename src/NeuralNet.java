import java.io.Serializable;
import java.util.Arrays;

import com.sun.webkit.dom.WheelEventImpl;

/**
 * 
 * @author Timo Raff
 *
 *         This class implements a simple feed forward neural network. The
 *         layout is set in the variable -- LAYERSIZES --. For this use case the
 *         initial layer has to have 15 nodes and the output layer has to have 2
 *         nodes.
 */
public class NeuralNet implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double output[][];
	double weights[][][];
	double bias[][];

	public final int[] LAYERSIZES = new int[] { 15, 10, 10, 2 };
	public final int INPUTSIZE;
	public final int OUTPUTSIZE;
	public final int NETWORKSIZE;

	double fitness;
	int weightsCount;

	/**
	 * initializes a neural network with the given Layout and either random weights
	 * or zero for all weights.
	 * 
	 * @param initWeights
	 *            if true, initialize weights randomly. If false initialize the
	 *            weigths with 0.
	 */
	public NeuralNet(boolean initWeights/* int... LAYERSIZES */) {
		NETWORKSIZE = LAYERSIZES.length;
		INPUTSIZE = LAYERSIZES[0];
		OUTPUTSIZE = LAYERSIZES[NETWORKSIZE - 1];

		fitness = 1;

		this.output = new double[NETWORKSIZE][];
		this.weights = new double[NETWORKSIZE][][];
		this.bias = new double[NETWORKSIZE][];

		for (int i = 0; i < NETWORKSIZE; i++) {
			this.output[i] = new double[LAYERSIZES[i]];
			this.bias[i] = new double[LAYERSIZES[i]];
			if (i > 0) {
				this.weights[i] = new double[LAYERSIZES[i]][LAYERSIZES[i - 1]];
				// init weights randomly
				if (initWeights) {
					for (int j = 0; j < LAYERSIZES[i]; j++) {
						for (int k = 0; k < LAYERSIZES[i - 1]; k++) {
							this.weights[i][j][k] = (Math.random() - 0.5) * 2;
						}
					}
				}
			}
		}
		//number of weights is necessary for the translation of the indices.
		this.weightsCount = 0;
		for (int j = 0; j < NETWORKSIZE - 1; j++) {
			weightsCount += LAYERSIZES[j] * LAYERSIZES[j + 1];
		}
	}

	/**
	 * calculates the output for a given input.
	 * @param in input: sensor values and the previous velocity and orientation.
	 * @return the velocity for the two wheels in the next step.
	 */
	public double[] calculate(double[] in) {
		// System.out.println(Arrays.toString(in));
		if (in.length == INPUTSIZE) {
			this.output[0] = in;
			for (int l = 1; l < NETWORKSIZE; l++) {
				for (int n = 0; n < LAYERSIZES[l]; n++) {
					double sum = bias[l][n];
					for (int nPrev = 0; nPrev < LAYERSIZES[l - 1]; nPrev++) {
						// System.out.println("l: " + l + " n: " + n + " nPrev: " + nPrev);
						// System.out.println(weights[l][n][nPrev]);
						sum += output[l - 1][nPrev] * weights[l][n][nPrev];
						// System.out.println("sum: "+sum);
					}
					output[l][n] = sigmoid(sum);
				}
			}
			return output[NETWORKSIZE - 1];
		}
		// if it is reached the format of the input is wrong
		return null;
	}

	/**
	 * sigmoid function
	 * @param x input
	 * @return solutino between 0 and 1
	 */
	private double sigmoid(double x) {
		return (1. / (1 + Math.exp(-x)));
	}

	public int getWeightCount() {
		return weightsCount;
	}

	/**
	 * replaces one specific weight
	 * @param i index
	 * @param w weight-value
	 */
	public void setWeight(int i, double w) {
		int[] tmp = getIndeces(i);
		//if (tmp[0] != 0)
			weights[tmp[0]][tmp[1]][tmp[2]] = w;
	}

	/**
	 * returns the value of the weight at an specific index
	 * @param i index
	 * @return weight-value
	 */
	public double getWeight(int i) {
		int[] tmp = getIndeces(i);
		//if (tmp[0] != 0) {
			return weights[tmp[0]][tmp[1]][tmp[2]];
		//} else {
		//	return 0;
		//}
	}

	/**
	 * translates a specific index to the indeces of the weights array.
	 * @param idx
	 * @return
	 */
	public int[] getIndeces(int idx) {
		int[] ret = new int[3];
		int counter = 0;
		while (idx >= 0) {
			double a = idx / LAYERSIZES[counter];
			if (a >= LAYERSIZES[counter + 1]) {
				idx -= LAYERSIZES[counter] * LAYERSIZES[counter + 1];
				counter++;
			} else {
				ret[0] = counter + 1;
				ret[1] = idx / LAYERSIZES[counter];
				ret[2] = idx % LAYERSIZES[counter];
				return ret;
			}
		}
		// should never be reached.
		return ret;
	}

	public double getFitness() {
		return fitness;
	}

	/**
	 * tests a robot on this neural network. 
	 * starting from 4 different position (currently fixed)
	 */
	public void test() {
		// TODO change starting postions of the robots
		Robot r = new Robot(2,2, new Maze());
		// play for some steps
		for (int i = 0; i < 2000; i++) {
			// System.out.println("acc: "+ Arrays.toString(calculate(r.getSensorValues())));
			r.move(calculate(r.getSensorValues()));
		}
		r.setPosition(98,2);
		for (int i = 0; i < 2000; i++) {
			// System.out.println("acc: "+ Arrays.toString(calculate(r.getSensorValues())));
			r.move(calculate(r.getSensorValues()));
		}
		r.setPosition(2,98);
		for (int i = 0; i < 2000; i++) {
			// System.out.println("acc: "+ Arrays.toString(calculate(r.getSensorValues())));
			r.move(calculate(r.getSensorValues()));
		}
		r.setPosition(98,98);
		for (int i = 0; i < 2000; i++) {
			// System.out.println("acc: "+ Arrays.toString(calculate(r.getSensorValues())));
			r.move(calculate(r.getSensorValues()));
		}
		// System.out.println("fitness: "+ fitness);
		fitness = r.getFitness();
	}
}
