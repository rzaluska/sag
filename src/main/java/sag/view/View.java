package sag.view;

import sag.model.maze.Maze;
import sag.model.maze.Point;
import sag.model.maze.generators.EmptyMazeGenerator;
import sag.model.maze.generators.RecursiveBacktracking;
import sag.model.simulation.AkkaAgentsSimulation;
import sag.model.simulation.Simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

public class View {

    Simulation simulation;
    private MazePanel mazePanel;
    TimerSim currentTimerSim;

    public Simulation getSimulation() {
        return simulation;
    }

    class TimerSim {
        java.util.Timer t = null;
        public TimerSim(final Simulation simulation, int period) {
            t = new java.util.Timer();
            t.schedule(new TimerTask() {
                @Override
                public synchronized void run() {
                    simulation.step();
                    mazePanel.invalidate();
                    mazePanel.validate();
                    mazePanel.repaint();
                }
            }, 0, period);
        }

        public void cancel() {
            if(this.t != null)
                this.t.cancel();
        }
    }

    public static void main(String[] args) {

        View view = new View();
        view.createAndShowGUI();

    }

    private void createAndShowGUI() {

        JFrame frame = new JFrame("SAG - Akka Maze");
        SetupSettingsPanel(frame);

    }

    private void SetupSettingsPanel(JFrame frame) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(13, 1));

        JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(JLabel.CENTER);
        label1.setText("Wymiary labiryntu");
        JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(JLabel.CENTER);
        label2.setText("Punkt wyjścia z labiryntu");
        JLabel label3 = new JLabel();
        label3.setHorizontalAlignment(JLabel.CENTER);
        label3.setText("Liczba agentów");
        JLabel label4 = new JLabel();
        label4.setHorizontalAlignment(JLabel.CENTER);
        label4.setText("Punkt startu");
        JLabel label5 = new JLabel();
        label5.setHorizontalAlignment(JLabel.CENTER);
        label5.setText("Pusty labirynt ?");
        JLabel label6 = new JLabel();
        label6.setHorizontalAlignment(JLabel.CENTER);
        label6.setText("Rozmiar komórki labiryntu");

        panel.add(label1);
        JPanel mazeDimPanel = new JPanel();
        mazeDimPanel.setLayout(new GridLayout(1, 2));
        JTextField mazeX = new JTextField("100");
        mazeX.setHorizontalAlignment(JTextField.CENTER);
        JTextField mazeY = new JTextField("100");
        mazeY.setHorizontalAlignment(JTextField.CENTER);
        mazeDimPanel.add(mazeX);
        mazeDimPanel.add(mazeY);
        panel.add(mazeDimPanel);

        panel.add(label2);
        JPanel exitDimPanel = new JPanel();
        exitDimPanel.setLayout(new GridLayout(1, 2));
        JTextField mazeExitX = new JTextField("99");
        mazeExitX.setHorizontalAlignment(JTextField.CENTER);
        JTextField mazeExitY = new JTextField("99");
        mazeExitY.setHorizontalAlignment(JTextField.CENTER);
        exitDimPanel.add(mazeExitX);
        exitDimPanel.add(mazeExitY);
        panel.add(exitDimPanel);

        panel.add(label3);
        JTextField agentsCount = new JTextField("100");
        agentsCount.setHorizontalAlignment(JTextField.CENTER);
        panel.add(agentsCount);

        panel.add(label4);
        JPanel startDimPanel = new JPanel();
        startDimPanel.setLayout(new GridLayout(1, 2));
        JTextField startX = new JTextField("0");
        startX.setHorizontalAlignment(JTextField.CENTER);
        JTextField startY = new JTextField("0");
        startY.setHorizontalAlignment(JTextField.CENTER);
        startDimPanel.add(startX);
        startDimPanel.add(startY);
        panel.add(startDimPanel);

        panel.add(label5);
        JCheckBox emptyMaze = new JCheckBox();
        emptyMaze.setHorizontalAlignment(JCheckBox.CENTER);
        emptyMaze.setSelected(false);
        panel.add(emptyMaze);

        panel.add(label6);
        JTextField cellSize = new JTextField("10");
        cellSize.setHorizontalAlignment(JTextField.CENTER);
        panel.add(cellSize);

        JButton startButton = new JButton("Generuj");
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                int mazeXValue, mazeYValue, exitXValue, exitYValue, agentsCountValue, startXValue, startYValue, cellSizeValue;
                boolean emptyMazeValue;
                try {
                    mazeXValue = Integer.parseInt(mazeX.getText());
                    mazeYValue = Integer.parseInt(mazeY.getText());
                    exitXValue = Integer.parseInt(mazeExitX.getText());
                    exitYValue = Integer.parseInt(mazeExitY.getText());
                    agentsCountValue = Integer.parseInt(agentsCount.getText());
                    startXValue = Integer.parseInt(startX.getText());
                    startYValue = Integer.parseInt(startY.getText());
                    emptyMazeValue = emptyMaze.isSelected();
                    cellSizeValue = Integer.parseInt(cellSize.getText());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Podaj same liczby", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(mazeXValue < 0 || mazeYValue < 0) {
                    JOptionPane.showMessageDialog(frame, "Niepoprawny rozmiar labiryntu", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(exitXValue < 0 || exitXValue > mazeXValue || exitYValue < 0 || exitYValue > mazeYValue) {
                    JOptionPane.showMessageDialog(frame, "Niepoprawny punkt wyjścia z labiryntu", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(startXValue < 0 || startXValue > mazeXValue || startYValue < 0 || startYValue > mazeYValue) {
                    JOptionPane.showMessageDialog(frame, "Niepoprawny punkt startu agentów", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(agentsCountValue < 2) {
                    JOptionPane.showMessageDialog(frame, "Niepoprawna ilość agentów", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(cellSizeValue < 1) {
                    JOptionPane.showMessageDialog(frame, "Niepoprawny rozmiar komórki", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SetupSimulationPanel(
                        frame,
                        mazeXValue,
                        mazeYValue,
                        exitXValue,
                        exitYValue,
                        agentsCountValue,
                        startXValue,
                        startYValue,
                        emptyMazeValue,
                        cellSizeValue
                );
            }

        });
        panel.add(startButton);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().removeAll();
        frame.setSize(new Dimension(400, 600));
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.invalidate();
        frame.validate();
        frame.repaint();
        frame.setVisible(true);

    }

    private void SetupSimulationPanel(JFrame frame, int mazeX, int mazeY, int exitX, int exitY, int agentsCount, int startX, int startY, boolean emptyMaze, int cellSize) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        Maze maze;
        if(emptyMaze) {
            EmptyMazeGenerator emptyMazeGenerator= new EmptyMazeGenerator();
            maze = emptyMazeGenerator.generate(mazeX, mazeY, new Point(exitX, exitY));
        } else {
            RecursiveBacktracking recursiveBacktracking = new RecursiveBacktracking();
            maze = recursiveBacktracking.generate(mazeX, mazeY, new Point(exitX, exitY));
        }
        if(simulation != null)
            simulation.stop();
        simulation = new AkkaAgentsSimulation();
        simulation.init(agentsCount, new Point(startX, startY), maze);
        mazePanel = new MazePanel(simulation, cellSize);
        JScrollPane scrollPane = new JScrollPane(mazePanel);
        c.weighty = 1;
        c.weightx = 0.85;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(scrollPane, c);

        JPanel steeringPanel = new JPanel();
        steeringPanel.setLayout(new GridLayout(10, 1));

        steeringPanel.add(new JPanel());

        JLabel label = new JLabel("Czas kroku symulacji (ms)");
        label.setHorizontalAlignment(JLabel.CENTER);
        steeringPanel.add(label);

        JTextField stepDuration = new JTextField("10");
        stepDuration.setHorizontalAlignment(JTextField.CENTER);
        steeringPanel.add(stepDuration);

        JButton toggleButton = new JButton("WZNÓW");
        steeringPanel.add(toggleButton);

        steeringPanel.add(new JPanel());

        JButton stepButton = new JButton("KROK");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulation.step();
                mazePanel.invalidate();
                mazePanel.validate();
                mazePanel.repaint();
            }
        });
        steeringPanel.add(stepButton);

        steeringPanel.add(new JPanel());

        JButton genButton = new JButton("RESTART");
        genButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentTimerSim != null)
                    currentTimerSim.cancel();
                SetupSimulationPanel(frame, mazeX, mazeY, exitX, exitY, agentsCount, startX, startY, emptyMaze, cellSize);
            }
        });
        steeringPanel.add(genButton);

        JButton settingsButton = new JButton("USTAWIENIA");
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentTimerSim != null)
                    currentTimerSim.cancel();
                SetupSettingsPanel(frame);
            }
        });
        steeringPanel.add(settingsButton);

        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(toggleButton.getText().equals("WZNÓW")) {
                    int period;
                    try {
                        period = Integer.parseInt(stepDuration.getText());
                        if(period < 1)
                            throw new Exception();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Niepoprawny czas kroku symulacji", "Błąd", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    currentTimerSim = new TimerSim(getSimulation(), period);
                    toggleButton.setText("WSTRZYMAJ");
                    stepDuration.setEnabled(false);
                    stepButton.setEnabled(false);
                    genButton.setEnabled(false);
                    settingsButton.setEnabled(false);
                } else {
                    currentTimerSim.cancel();
                    toggleButton.setText("WZNÓW");
                    stepDuration.setEnabled(true);
                    stepButton.setEnabled(true);
                    genButton.setEnabled(true);
                    settingsButton.setEnabled(true);
                }
            }
        });

        steeringPanel.add(new JPanel());

        c.weighty = 1;
        c.weightx = 0.15;
        c.gridx = 1;
        c.gridy = 0;
        panel.add(steeringPanel, c);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().removeAll();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.invalidate();
        frame.validate();
        frame.repaint();
        frame.setVisible(true);

    }
}

