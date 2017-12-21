package sag.model.maze.generators;

import javafx.util.Pair;
import sag.model.maze.Maze;
import sag.model.maze.MazeGenerator;
import sag.model.maze.structures.ArrayMaze;
import sag.model.maze.structures.MazeStructure;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import static sag.model.maze.Maze.*;
import static sag.model.maze.Maze.WallDirection.*;

public class RecursiveBacktracking implements MazeGenerator {
    private class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private int getNewX(int x, WallDirection direction) {
        if (direction == WallDirection.E) {
            x++;
        }
        if (direction == WallDirection.W) {
            x--;
        }
        return x;
    }

    private int getNewY(int y, WallDirection direction) {
        if (direction == WallDirection.N) {
            y--;
        }
        if (direction == WallDirection.S) {
            y++;
        }
        return y;
    }

    private void carveFrom(MazeStructure structure, int x, int y) {
        int currx = x;
        int curry = y;

        WallDirection directions[] = {N, S, W, E};

        boolean canContinue = true;

        Stack<Point> stack = new Stack<>();

        do {
            //System.out.println("x = " + currx + " y = " + curry);
            Collections.shuffle(Arrays.asList(directions));
            boolean found = false;
            for (int directionIndex = 0; directionIndex < 4; directionIndex++) {
                if (moveValid(structure, directions[directionIndex], currx, curry)) {
                    int newx = getNewX(currx, directions[directionIndex]);
                    int newy = getNewY(curry, directions[directionIndex]);
                    structure.removeWall(currx, curry, directions[directionIndex]);
                    structure.removeWall(newx, newy, opposite(directions[directionIndex]));
                    stack.push(new Point(currx, curry));
                    currx = newx;
                    curry = newy;
                    found = true;
                    break;
                }
            }
            if (!found) {
                Point p = stack.pop();
                currx = p.x;
                curry = p.y;
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

    private boolean moveValid(MazeStructure structure, WallDirection direction, int x, int y) {
        int newx = getNewX(x, direction);
        int newy = getNewY(y, direction);
        return !((newx < 0) || (newx > structure.getWidth() - 1) || (newy < 0) || (newy > structure.getHeight() - 1)) && structure.notVisited(newx, newy);
    }

    @Override
    public Maze generate(int width, int height) {
        MazeStructure mazeStructure = new ArrayMaze(width, height, 0, 0);
        carveFrom(mazeStructure, 0, 0);
        return mazeStructure;
    }
}
