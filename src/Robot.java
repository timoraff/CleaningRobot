
public class Robot {
//contains movement methods for the Robot and i think the position ?
	int l =2; //distance between the 2 wheels
	double posX; //Coordinates in the maze
	double posY;
	Maze maze; 
	
	public void move() {
		double x=0;
		double y=0;
		maze.updatePosition(x,y);
	}
	
}
