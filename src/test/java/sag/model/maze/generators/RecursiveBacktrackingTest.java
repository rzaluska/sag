package sag.model.maze.generators;

import sag.model.maze.Maze;
import sag.model.maze.Point;

import static org.junit.jupiter.api.Assertions.*;

class RecursiveBacktrackingTest {

    @org.junit.jupiter.api.Test
    void generate() {
        RecursiveBacktracking recursiveBacktracking = new RecursiveBacktracking();
        int width = 100;
        int height = 100;
        Maze maze = recursiveBacktracking.generate(width,height);

        for (int i = 0; i < width; i++) {
            System.out.print(" _");
        }
        System.out.println();
        for (int i = 0; i < height; i++) {
            System.out.print("|");
            for (int j = 0; j < width; j++) {
                if (maze.isWallAt(new Point(j,i), Maze.WallDirection.S)) {
                    System.out.print("_");
                }
                else {
                    System.out.print(" ");
                }
                if (maze.isWallAt(new Point(j,i), Maze.WallDirection.E)) {
                    System.out.print("|");
                }
                else {
                    System.out.print(" ");
                }

            }
            System.out.println();
        }
    }
}