package sag.view;

import sag.model.maze.Maze;
import sag.model.maze.generators.RecursiveBacktracking;

import javax.swing.*;
import java.awt.*;

public class View {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("Akka Maze");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RecursiveBacktracking recursiveBacktracking = new RecursiveBacktracking();
        Maze maze = recursiveBacktracking.generate(100, 100);
        MazePanel mazePanel = new MazePanel(maze, 10);
        JScrollPane jScrollPane = new JScrollPane(mazePanel);
        jScrollPane.setMaximumSize(new Dimension(100, 100));
        f.add(jScrollPane);
        f.pack();
        f.setVisible(true);
    }
}

