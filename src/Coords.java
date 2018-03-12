public class Coords {
    private double x;
    private double y;
    private double angle;

    Coords(double x, double y) {
        this.setX(x);
        this.setY(y);
        this.angle = 0;
    }
    Coords(double x, double y, double a) {
        this.setX(x);
        this.setY(y);
        this.angle = a;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    void setX(double x) {
        this.x = x;
    }

    void setY(double y) {
        this.y = y;
    }

    void setAngle(double angle) {
        this.angle = angle;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + angle + "°)";
    }
}