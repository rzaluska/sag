package sag.model.simulation.messages;

import sag.model.maze.Point;

public class GetDecisionInPlace {
    public Point getPoint() {
        return point;
    }

    private final Point point;

    public GetDecisionInPlace(Point p) {
        this.point = p;
    }
}
