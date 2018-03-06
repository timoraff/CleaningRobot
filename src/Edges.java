public class Edges {
    private Coords from;
    private Coords to;
    private double shift;
    Edges(Coords from, Coords to, double shift) {
        this.from = from;
        this.to = to;
        this.shift = shift;
    }

    Coords getFrom() {
        return from;
    }

    Coords getTo() {
        return to;
    }

    double getShift() {
        return shift;
    }
    
    @Override
    public String toString() {
        return "[" + from.toString() + ", " + to.toString() + "]";
    }
}