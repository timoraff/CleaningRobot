public class Edges {
    private Coords from;
    private Coords to;
    private String direction;
    Edges(Coords from, Coords to, String direction) {
        this.from = from;
        this.to = to;
        this.direction = direction;
    }

    Coords getFrom() {
        return from;
    }

    Coords getTo() {
        return to;
    }

    String getDirection() {
        return direction;
    }
    
    @Override
    public String toString() {
        return "[" + from.toString() + ", " + to.toString() + "]";
    }
}