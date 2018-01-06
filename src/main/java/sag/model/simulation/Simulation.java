package sag.model.simulation;

import sag.model.maze.Maze;
import sag.model.maze.Point;

import java.util.List;

public interface Simulation {
    void init(int numberOfAgents, Point start, Maze maze);
    void stop();
    void step();
    List<Point> getAgentsPositions();
    List<Boolean[][]> getAgentsPaths();
    Maze getMaze();
    boolean isFinished();
}
