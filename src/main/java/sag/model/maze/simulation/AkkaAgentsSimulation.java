package sag.model.maze.simulation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import sag.model.maze.Maze;
import sag.model.maze.Point;
import sag.model.maze.simulation.messages.MakeDecision;
import sag.model.maze.simulation.messages.MakeMove;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class AkkaAgentsSimulation implements Simulation {
    private ActorSystem actorSystem;
    private List<ActorRef> actors;
    private List<Point> agentsPositions;
    private Maze maze;

    public AkkaAgentsSimulation() {
        this.actors = new LinkedList<>();
        this.agentsPositions = new LinkedList<>();
    }

    @Override
    public void init(int numberOfAgents, Maze maze) {
        this.maze = maze;
        this.actorSystem = ActorSystem.create("sag");

        for (int i = 0; i < numberOfAgents; i++) {
            Point position = new Point(0, 0);
            this.agentsPositions.add(position);
            ActorRef actor = this.actorSystem.actorOf(MazeAgent.props(position, maze), Integer.toString(i));
            this.actors.add(actor);
        }
    }

    @Override
    public void stop() {
        this.actorSystem.terminate();

    }

    @Override
    public void step() {
        for (ActorRef actor : this.actors) {
            actor.tell(new MakeDecision(), ActorRef.noSender());
        }
        for (ActorRef actor : this.actors) {
            actor.tell(new MakeMove(), ActorRef.noSender());
        }
    }

    @Override
    public List<Point> getAgentsPositions() {
        return this.agentsPositions;
    }

    @Override
    public Maze getMaze() {
        return this.maze;
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
