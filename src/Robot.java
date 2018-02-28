
public class Robot {
//contains movement methods for the Robot and i think the position ?
	int l =2; //distance between the 2 wheels
	double posX; //Coordinates in the maze
	double posY;
	Maze maze; 
	
	public void move() {
		//gets information from the NN Vr and Vl
		double Vr = 1;
		double Vl = 2;
		double x=0;
		double y=0;
		double theta = 0.2;
		double deltat = 0.1;
		double w;
		double R;
		double ICCx;
		double ICCy;
		if (Vr == Vl) {
				//just move forward;
		}
		else {
			R = (l/2)*((Vl+Vr)/(Vr-Vl));
			w = (Vr - Vl) / l;
			ICCx = x - R* Math.sin(theta);
			ICCy = y + R* Math.cos(theta);
			
		}
			
		
		maze.updatePosition(x,y);
	}
	
}
