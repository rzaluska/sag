package sag.model.maze.simulation;

import akka.actor.AbstractActor;
import akka.actor.Props;
import sag.model.maze.Maze;
import sag.model.maze.Point;
import sag.model.maze.simulation.messages.MakeDecision;
import sag.model.maze.simulation.messages.MakeMove;

import java.util.*;

public class MazeAgent extends AbstractActor {
    private Maze maze;
    private Point nextStep;
    private Stack<Point> previousPoints;
    private Map<Point, List<Maze.WallDirection>> previousTurns;
    private int lastPointOnList;

    static public Props props(Point startPoint, Maze maze) {
        return Props.create(MazeAgent.class, () -> new MazeAgent(startPoint, maze));
    }

    private Point currentPosition;
    private List<Point> path;

    private MazeAgent(Point startPoint, Maze maze) {
        this.currentPosition = startPoint;
        this.maze = maze;
        this.previousPoints = new Stack<>();
        this.previousTurns = new HashMap<>();
        previousPoints.push(this.currentPosition);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MakeDecision.class, md -> {
                    makeDecision();
                })
                .match(MakeMove.class, mm -> {
                    makeMove();
                })
                .build();
    }

    private void makeMove() {
        this.previousPoints.push(new Point(this.currentPosition));
        this.currentPosition.setX(this.nextStep.getX());
        this.currentPosition.setY(this.nextStep.getY());
    }

    private void makeDecision() {
        Maze.WallDirection directions[] = {Maze.WallDirection.N, Maze.WallDirection.S, Maze.WallDirection.W, Maze.WallDirection.E};

        Collections.shuffle(Arrays.asList(directions));


        while (true) {
            for (int directionIndex = 0; directionIndex < 4; directionIndex++) {
                if (moveValid(directions[directionIndex])) {
                    this.nextStep = getNew(directions[directionIndex]);
                    if (this.previousTurns.containsKey(this.currentPosition)) {
                        List <Maze.WallDirection> wallDirections = this.previousTurns.get(this.currentPosition);
                        wallDirections.add(directions[directionIndex]);
                    }
                    else {
                        List <Maze.WallDirection> wallDirections = new LinkedList<>();
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

    private boolean moveValid(Maze.WallDirection direction) {
        if (this.previousTurns.containsKey(this.currentPosition)) {
            List <Maze.WallDirection> wallDirections = this.previousTurns.get(this.currentPosition);
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
