package sag.model.simulation.messages;

import sag.model.maze.Point;

import java.util.Stack;

public class MazeFinished {
    public Stack<Point> getPath() {
        return path;
    }

    private Stack<Point> path;

    public MazeFinished(Stack<Point> path)  {
        this.path = path;
    }


}
