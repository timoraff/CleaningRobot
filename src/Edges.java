public class Edges {
    private Coords from;
    private Coords to;
    Edges(Coords from, Coords to) {
        this.from = from;
        this.to = to;
    }

    Coords getFrom() {
        return from;
    }

    Coords getTo() {
        return to;
    }
}