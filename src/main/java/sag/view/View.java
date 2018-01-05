package sag.view;

import sag.model.maze.Maze;
import sag.model.maze.generators.RecursiveBacktracking;
import sag.model.simulation.AkkaAgentsSimulation;
import sag.model.simulation.Simulation;

import javax.swing.*;
import java.awt.*;
import java.util.TimerTask;

public class View {
    Simulation simulation;
    private MazePanel mazePanel;

    public Simulation getSimulation() {
        return simulation;
    }

    class TimerSim {
        public TimerSim(final Simulation simulation) {
            java.util.Timer t = new java.util.Timer();
            t.schedule(new TimerTask() {
                @Override
                public synchronized void run() {
                    simulation.step();
                    mazePanel.invalidate();
                    mazePanel.validate();
                    mazePanel.repaint();
                }
            }, 0, 10);
        }
    }

    public static void main(String[] args) {
        View view = new View();
        view.createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame f = new JFrame("Akka Maze");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RecursiveBacktracking recursiveBacktracking = new RecursiveBacktracking();
        Maze maze = recursiveBacktracking.generate(100, 100);
        this.simulation = new AkkaAgentsSimulation();
        this.simulation.init(100, maze);
        this.mazePanel = new MazePanel(simulation, 10);
        JScrollPane jScrollPane = new JScrollPane(this.mazePanel);
        jScrollPane.setSize(new Dimension(100, 100));
        f.add(jScrollPane);
        f.setSize(100, 100);
        f.setVisible(true);
        new TimerSim(this.getSimulation());
    }
}

