package sag.model.simulation.messages;

import sag.model.maze.Maze;

import java.util.List;

public class ReturnDecisionInPlace {
    public List<Maze.WallDirection> getDirections() {
        return directions;
    }

    private final List<Maze.WallDirection> directions;

    public ReturnDecisionInPlace(List<Maze.WallDirection> directions) {
        this.directions = directions;
    }

}
