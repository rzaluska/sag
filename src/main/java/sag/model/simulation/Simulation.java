package sag.model.simulation;

import sag.model.maze.Maze;
import sag.model.maze.Point;

import java.util.List;

public interface Simulation {
    void init(int numberOfAgents, Maze maze);
    void stop();
    void step();
    List<Point> getAgentsPositions();
    Maze getMaze();
    boolean isFinished();
}
