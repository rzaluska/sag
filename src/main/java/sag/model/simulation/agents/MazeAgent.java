package sag.model.simulation.agents;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import sag.model.maze.Maze;
import sag.model.maze.Point;
import sag.model.simulation.messages.GetDecisionInPlace;
import sag.model.simulation.messages.MakeDecision;
import sag.model.simulation.messages.MakeMove;
import sag.model.simulation.messages.ReturnDecisionInPlace;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MazeAgent extends AbstractActor {
    private final List<ActorRef> actors;
    private int couter;
    private Maze maze;
    private Point nextStep;
    private Stack<Point> previousPoints;
    private Map<Point, List<Maze.WallDirection>> previousTurns;
    private int lastPointOnList;
    private Map<Maze.WallDirection, Integer> directionMap;
    private ActorRef superSender;

    static public Props props(Point startPoint, Maze maze, List<ActorRef> actors) {
        return Props.create(MazeAgent.class, () -> new MazeAgent(startPoint, maze, actors));
    }

    private Point currentPosition;
    private List<Point> path;

    private MazeAgent(Point startPoint, Maze maze, List<ActorRef> actors) {
        this.actors = actors;
        this.currentPosition = startPoint;
        this.maze = maze;
        this.previousPoints = new Stack<>();
        this.previousTurns = new HashMap<>();
        previousPoints.push(this.currentPosition);
        this.directionMap = new HashMap<>();
        this.couter = 0;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MakeDecision.class, md -> {
                    makeDecision();
                })
                .match(GetDecisionInPlace.class, mm -> {
                    giveDecision(mm);
                })
                .match(ReturnDecisionInPlace.class, rd -> {
                    updateDirections(rd);
                })
                .match(MakeMove.class, mm -> {
                    makeMove();
                    getSender().tell(new Object(), getSender());
                })
                .build();
    }

    private void updateDirections(ReturnDecisionInPlace directions) {
        this.couter--;
        for (Maze.WallDirection direction : directions.getDirections()) {
            this.directionMap.put(direction, this.directionMap.get(direction) + 1);
        }
        if (this.couter == 0) {
            superSender.tell(new Object(), superSender);
        }
    }

    private void giveDecision(GetDecisionInPlace mm) {
        Point place = mm.getPoint();
        if (this.previousTurns.containsKey(place)) {
            List<Maze.WallDirection> wallDirections = this.previousTurns.get(this.currentPosition);
            getSender().tell(new ReturnDecisionInPlace(wallDirections), getSelf());
        }
        getSender().tell(new ReturnDecisionInPlace(new LinkedList<>()), getSelf());
    }

    private void prepareMove() {
        Maze.WallDirection[] orderedDirections = getDirectionsInOrder();
        //Maze.WallDirection directions[] = {Maze.WallDirection.N, Maze.WallDirection.S, Maze.WallDirection.W, Maze.WallDirection.E};
        Maze.WallDirection directions[] = orderedDirections;

        //Collections.shuffle(Arrays.asList(directions));


        while (true) {
            for (int directionIndex = 0; directionIndex < 4; directionIndex++) {
                if (moveValid(directions[directionIndex])) {
                    this.nextStep = getNew(directions[directionIndex]);
                    if (this.previousTurns.containsKey(this.currentPosition)) {
                        List<Maze.WallDirection> wallDirections = this.previousTurns.get(this.currentPosition);
                        wallDirections.add(directions[directionIndex]);
                    } else {
                        List<Maze.WallDirection> wallDirections = new LinkedList<>();
                        wallDirections.add(directions[directionIndex]);
                        this.previousTurns.put(this.currentPosition, wallDirections);
                    }
                    return;
                }
            }
            if (this.previousPoints.empty()) {
                this.previousTurns.clear();
                return;
            }
            this.nextStep = this.previousPoints.pop();
        }
    }

    private void makeMove() {
        this.prepareMove();
        this.previousPoints.push(new Point(this.currentPosition));
        this.currentPosition.setX(this.nextStep.getX());
        this.currentPosition.setY(this.nextStep.getY());
    }

    private void makeDecision() {
        this.superSender = this.getSender();
        this.directionMap.clear();
        this.directionMap.put(Maze.WallDirection.N, 0);
        this.directionMap.put(Maze.WallDirection.S, 0);
        this.directionMap.put(Maze.WallDirection.W, 0);
        this.directionMap.put(Maze.WallDirection.E, 0);
        this.directionMap.put(Maze.WallDirection.X, 0);
        this.couter = this.actors.size() - 1;
        askForDecisionsInCurrentPlace();
    }

    private Maze.WallDirection[] getDirectionsInOrder() {
        Maze.WallDirection directions[] = {Maze.WallDirection.N, Maze.WallDirection.S, Maze.WallDirection.W, Maze.WallDirection.E};
        boolean found = false;
        for (Map.Entry<Maze.WallDirection, Integer> entry : this.directionMap.entrySet()) {
            if (entry.getValue() != 0) {
                found = true;
                break;
            }
        }
        if (!found) {
            Collections.shuffle(Arrays.asList(directions));
            return directions;
        }
        Arrays.sort(directions, (a, b) -> directionMap.get(b) - directionMap.get(a));
        return directions;
    }

    private void askForDecisionsInCurrentPlace() {
        for (ActorRef actor : actors) {
            if (actor.equals(getSelf())) {
                continue;
            }
            actor.tell(new GetDecisionInPlace(this.currentPosition), getSelf());
        }
    }

    private boolean moveValid(Maze.WallDirection direction) {
        if (this.previousTurns.containsKey(this.currentPosition)) {
            List<Maze.WallDirection> wallDirections = this.previousTurns.get(this.currentPosition);
            if (wallDirections.contains(direction)) {
                return false;
            }
        }
        Point newPoint = getNew(direction);
        return !this.previousPoints.contains(newPoint) && !((newPoint.getX() < 0) || (newPoint.getX() > this.maze.getWidth() - 1) || (newPoint.getY() < 0) || (newPoint.getY() > this.maze.getHeight() - 1)) && !this.maze.isWallAt(this.currentPosition, direction);
    }

    public Point getNew(Maze.WallDirection direction) {
        int x = this.currentPosition.getX();
        int y = this.currentPosition.getY();
        if (direction == Maze.WallDirection.E) {
            x++;
        }
        if (direction == Maze.WallDirection.W) {
            x--;
        }
        if (direction == Maze.WallDirection.N) {
            y--;
        }
        if (direction == Maze.WallDirection.S) {
            y++;
        }
        return new Point(x, y);
    }

}
