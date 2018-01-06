package sag.model.simulation.messages;

import sag.model.maze.Maze;

import java.util.Map;

public class ReturnDecisionInPlace {
    public Map<Maze.WallDirection, Integer> getDirections() {
        return directions;
    }

    private final Map<Maze.WallDirection, Integer> directions;

    public ReturnDecisionInPlace(Map<Maze.WallDirection, Integer> directions) {
        this.directions = directions;
    }

}
