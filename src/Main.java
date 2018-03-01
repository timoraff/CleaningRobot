
public class Main {
    //just a main to execute the programm
    public static void main(String[] args) {
        //how to get the correct position for the robot
        Maze maze = new Maze();
        Visualizer visualizer = new Visualizer(maze);
        Coords from = new Coords(2.0,2.0);
        Coords to = new Coords(2.0,-2.0);
        Coords pos = maze.getCorrectPosition(from, to);
        System.out.println("Pos before check: " + to.getX() + "/" + to.getY() + " Pos after check: " + pos.getX() + "/" + pos.getY());

        from.setX(2.0);
        from.setY(2.0);
        to.setX(2.0);
        to.setY(4.0);
        pos = maze.getCorrectPosition(from, to);
        System.out.println("Pos before check: " + to.getX() + "/" + to.getY() + " Pos after check: " + pos.getX() + "/" + pos.getY());


        from.setX(2.0);
        from.setY(2.0);
        to.setX(4.0);
        to.setY(7.0);
        pos = maze.getCorrectPosition(from, to);
        System.out.println("Pos before check: " + to.getX() + "/" + to.getY() + " Pos after check: " + pos.getX() + "/" + pos.getY());
	}

}
