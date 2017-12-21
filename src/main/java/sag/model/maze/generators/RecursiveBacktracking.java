package sag.model.maze.generators;

import javafx.util.Pair;
import sag.model.maze.Maze;
import sag.model.maze.MazeGenerator;
import sag.model.maze.Point;
import sag.model.maze.structures.ArrayMaze;
import sag.model.maze.structures.MazeStructure;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import static sag.model.maze.Maze.*;
import static sag.model.maze.Maze.WallDirection.*;

public class RecursiveBacktracking implements MazeGenerator {
    public Point getNew(Point point, WallDirection direction) {
        int x = point.getX();
        int y = point.getY();
        if (direction == WallDirection.E) {
            x++;
        }
        if (direction == WallDirection.W) {
            x--;
        }
        if (direction == WallDirection.N) {
            y--;
        }
        if (direction == WallDirection.S) {
            y++;
        }
        return new Point(x, y);
    }

    private void carveFrom(MazeStructure structure, Point point) {
        Point curr = point;

        WallDirection directions[] = {N, S, W, E};

        boolean canContinue = true;

        Stack<Point> stack = new Stack<>();

        do {
            //System.out.println("x = " + currx + " y = " + curry);
            Collections.shuffle(Arrays.asList(directions));
            boolean found = false;
            for (int directionIndex = 0; directionIndex < 4; directionIndex++) {
                if (moveValid(structure, directions[directionIndex], curr)) {
                    Point newPoint = getNew(curr, directions[directionIndex]);
                    structure.removeWall(curr, directions[directionIndex]);
                    structure.removeWall(newPoint, opposite(directions[directionIndex]));
                    stack.push(curr);
                    curr = newPoint;
                    found = true;
                    break;
                }
            }
            if (!found) {
                curr = stack.pop();
            }
        } while (!stack.empty());
    }

    private WallDirection opposite(WallDirection direction) {
        switch (direction) {
            case N:
                return S;
            case E:
                return W;
            case S:
                return N;
            case W:
                return E;
            default:
                return X;
        }
    }

    private boolean moveValid(MazeStructure structure, WallDirection direction, Point point) {
        Point newPoint = getNew(point, direction);
        return !((newPoint.getX() < 0) || (newPoint.getX() > structure.getWidth() - 1) || (newPoint.getY() < 0) || (newPoint.getY() > structure.getHeight() - 1)) && structure.notVisited(newPoint);
    }

    @Override
    public Maze generate(int width, int height) {
        MazeStructure mazeStructure = new ArrayMaze(width, height, new Point(0, 0));
        carveFrom(mazeStructure, new Point(0, 0));
        return mazeStructure;
    }
}
