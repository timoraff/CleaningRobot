import com.sun.webkit.dom.WheelEventImpl;

public class NeuralNet {
	double output[][];
	double weights[][][];
	double bias[][];

	public final int[] LAYERSIZES;
	public final int INPUTSIZE;
	public final int OUTPUTSIZE;
	public final int NETWORKSIZE;

	double fitness;
	int weightsCount;

	public NeuralNet(boolean initWeights/* int... LAYERSIZES */) {
		// fixed size: 15 in 2 out no hidden layer.
		int[] LAYERSIZES = new int[] { 15, 2 };
		// at least 2
		this.LAYERSIZES = LAYERSIZES;
		NETWORKSIZE = LAYERSIZES.length;
		INPUTSIZE = LAYERSIZES[0];
		OUTPUTSIZE = LAYERSIZES[NETWORKSIZE - 1];

		fitness = 1;// i think has to be set from the maze or robot

		this.output = new double[NETWORKSIZE][];
		this.weights = new double[NETWORKSIZE][][];
		this.bias = new double[NETWORKSIZE][];

		for (int i = 0; i < NETWORKSIZE; i++) {
			this.output[i] = new double[LAYERSIZES[i]];
			this.bias[i] = new double[LAYERSIZES[i]];
			this.weights[i] = new double[LAYERSIZES[i]][LAYERSIZES[i - 1]];
			if (i > 0 && initWeights) {
				// init weights randomly
				for (int j = 0; j < LAYERSIZES[i]; j++) {
					for (int k = 0; k < LAYERSIZES[i - 1]; k++) {
						this.weights[i][j][k] = Math.random();
					}
				}
			}
		}
		// TODO count nunmber of weights--> so other layouts are also possible
		this.weightsCount = 15 * 2;
		// count number of weights:
		// for(int i =0;i<Network)
	}

	public double[] calculate(double[] in) {
		if (in.length == INPUTSIZE) {
			this.output[0] = in;
			for (int l = 1; l < NETWORKSIZE; l++) {
				for (int n = 0; n < LAYERSIZES[l]; n++) {
					double sum = bias[l][n];
					for (int nPrev = 0; nPrev < LAYERSIZES[l - 1]; nPrev++) {
						sum += output[l - 1][nPrev] * weights[l][n][nPrev];
					}
					output[l][n] = sigmoid(sum);
				}
			}
			return output[NETWORKSIZE - 1];
		}
		// wrong input format
		return null;
	}

	private double sigmoid(double x) {
		return (1. / (1 + Math.exp(-x)));
	}

	public int getWeightCount() {
		return weightsCount;
	}

	public void setWeight(int i, double w) {
		int[] tmp = getIndeces(i);
		weights[tmp[0]][tmp[1]][tmp[2]] = w;
	}

	public double getWeight(int i) {
		int[] tmp = getIndeces(i);
		return weights[tmp[0]][tmp[1]][tmp[2]];
	}

	public int[] getIndeces(int idx) {
		int[] ret = new int[3];
		int counter = 0;
		double a = idx / LAYERSIZES[counter];
		while (idx > 0) {
			if (a > LAYERSIZES[counter + 1]) {
				idx -= LAYERSIZES[counter] * LAYERSIZES[counter + 1];
				counter++;
			} else {
				ret[0] = counter;
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
}
