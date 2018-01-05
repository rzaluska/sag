package sag.model.simulation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import sag.model.maze.Maze;
import sag.model.maze.Point;
import sag.model.simulation.agents.MazeAgent;
import sag.model.simulation.messages.MakeDecision;
import sag.model.simulation.messages.MakeMove;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AkkaAgentsSimulation implements Simulation {
    private ActorSystem actorSystem;
    private List<ActorRef> actors;
    private List<Point> agentsPositions;
    private Maze maze;
    private int numberOfAgents;

    public AkkaAgentsSimulation() {
        this.actors = new LinkedList<>();
        this.agentsPositions = new LinkedList<>();
    }

    @Override
    public void init(int numberOfAgents, Maze maze) {
        this.maze = maze;
        this.actorSystem = ActorSystem.create("sag");
        this.numberOfAgents = numberOfAgents;

        for (int i = 0; i < numberOfAgents; i++) {
            Point position = new Point(0, 0);
            this.agentsPositions.add(position);
            ActorRef actor = this.actorSystem.actorOf(MazeAgent.props(position, maze, this.actors), Integer.toString(i));
            this.actors.add(actor);
        }
    }

    @Override
    public void stop() {
        this.actorSystem.terminate();

    }

    @Override
    public void step() {
        try {
            final Timeout timeout = new Timeout(24, TimeUnit.HOURS);
            List<Future<Object>> futures = new LinkedList<>();
            for (ActorRef actor : actors) {
                Future<Object> future = Patterns.ask(actor, new MakeDecision(), timeout);
                futures.add(future);
            }
            for (Future<Object> future : futures) {
                Await.result(future, timeout.duration());
            }
            futures.clear();
            for (ActorRef actor : actors) {
                Future<Object> future = Patterns.ask(actor, new MakeMove(), timeout);
                futures.add(future);
            }
            for (Future<Object> future : futures) {
                Await.result(future, timeout.duration());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
