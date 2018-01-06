package sag.model.simulation.agents;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import sag.model.maze.Maze;
import sag.model.maze.Point;
import sag.model.simulation.messages.*;

import java.util.*;

public class MazeAgent extends AbstractActor {
    private enum State {
        IN_MAZE,
        ON_BEST_PATH,
        FINISHED,
    }

    private State state;
    private final List<ActorRef> actors;
    private int couter;
    private Maze maze;
    private Point nextStep;
    private Stack<Point> previousPoints;
    private Map<Point, Map<Maze.WallDirection, Integer>> previousTurns;
    private int lastPointOnList;
    private Map<Maze.WallDirection, Integer> otherAgentsDirections;
    private ActorRef superSender;
    private Stack<Point> bestPath;
    private Stack<Point> pathToBest;

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
        this.otherAgentsDirections = new HashMap<>();
        this.couter = 0;
        this.state = State.IN_MAZE;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MakeDecision.class, md -> {
                    makeDecision();
                })
                .match(MazeFinished.class, mf -> {
                    handleFinished(mf);
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

    private void handleFinished(MazeFinished mf) {
        if (state == State.FINISHED) {
            return;
        }
        Stack<Point> wholeBestPath = mf.getPath();
        List<Point> myPathToBestPath = new ArrayList<>();


        Point last = null;
        while (true) {
            Point p = this.previousPoints.pop();
            if (wholeBestPath.contains(p)) {
                last = p;
                break;
            }
            myPathToBestPath.add(p);
        }
        Collections.reverse(myPathToBestPath);

        List<Point> tmp = new ArrayList<>();
        while (!wholeBestPath.empty()) {
            Point wbp = wholeBestPath.pop();
            if (wbp.equals(last)) {
                break;
            }
            tmp.add(wbp);
        }

        tmp.addAll(myPathToBestPath);
        this.bestPath = optimizePath(tmp);
        //c=A c=B C D A A A A x=B
        this.state = State.ON_BEST_PATH;
    }

    public static Stack<Point> optimizePath(List<Point> list) {
        Stack<Point> n = new Stack<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            Point curr = list.get(i);
            for (int j = 0; j < i; j++) {
                if (curr.equals(list.get(j))) {
                    List<Point> nl = new ArrayList<>();
                    nl = list.subList(0, j);
                    nl.addAll(list.subList(i, list.size()));
                    list = nl;
                    i -= (i - j);
                    break;
                }
            }
        }
        n.addAll(list);
        return n;
    }

    public static void main(String[] args) {
        List<Point> l = new ArrayList<>();
        l.add(new Point(0, 0));
        l.add(new Point(1, 1));
        l.add(new Point(2, 2));
        l.add(new Point(1, 1));
        l.add(new Point(0, 0));
        l.add(new Point(5, 5));
        l.add(new Point(3, 3));
        l.add(new Point(5, 5));
        Stack<Point> s = optimizePath(l);
        for (Point p : s) {
            System.out.println(p);
        }
    }

    private void updateDirections(ReturnDecisionInPlace directions) {
        this.couter--;
        for (Map.Entry<Maze.WallDirection, Integer> direction : directions.getDirections().entrySet()) {
            this.otherAgentsDirections.put(direction.getKey(), this.otherAgentsDirections.getOrDefault(direction.getKey(), 0) + direction.getValue());
        }
        if (this.couter == 0) {
            superSender.tell(new Object(), superSender);
        }
    }

    private void giveDecision(GetDecisionInPlace mm) {
        Point place = mm.getPoint();
        if (this.previousTurns.containsKey(place)) {
            Map<Maze.WallDirection, Integer> wallDirections = this.previousTurns.get(this.currentPosition);
            Map<Maze.WallDirection, Integer> copyOfWallDirections = new HashMap<>();
            for (Map.Entry<Maze.WallDirection, Integer> entry : wallDirections.entrySet()) {
                copyOfWallDirections.put(entry.getKey(), entry.getValue().intValue());
            }
            getSender().tell(new ReturnDecisionInPlace(copyOfWallDirections), getSelf());
        }
        getSender().tell(new ReturnDecisionInPlace(new HashMap<>()), getSelf());
    }

    private void prepareMove() {
        Maze.WallDirection directions[] = getDirections();

        directions = Arrays.stream(directions).filter(this::moveValid).toArray(Maze.WallDirection[]::new);
        if (directions.length == 0) {
            this.nextStep = this.previousPoints.pop();
            return;
        }
        Integer[] scores = Arrays.stream(directions).map(this::getMoveScore).toArray(Integer[]::new);

        Map<Maze.WallDirection, Integer> directionsToScores = new HashMap<>();

        int i = 0;
        for (Maze.WallDirection direction : directions) {
            directionsToScores.put(direction, scores[i]);
            i++;
        }

        Arrays.sort(directions, (a, b) -> directionsToScores.get(b) - directionsToScores.get(a));


        this.nextStep = getNew(directions[0]);
        if (this.previousTurns.containsKey(this.currentPosition)) {
            Map<Maze.WallDirection, Integer> wallDirections = this.previousTurns.get(this.currentPosition);
            wallDirections.put(directions[0], wallDirections.getOrDefault(directions[0], 0) + 1);
        } else {
            Map<Maze.WallDirection, Integer> wallDirections = new HashMap<>();
            wallDirections.put(directions[0], wallDirections.getOrDefault(directions[0], 0) + 1);
            this.previousTurns.put(this.currentPosition, wallDirections);
        }
    }

    private void makeMove() {
        if (this.state == State.FINISHED) {
            return;
        }

        if (this.state == State.ON_BEST_PATH) {
            this.nextStep = this.bestPath.pop();
            this.currentPosition.setX(this.nextStep.getX());
            this.currentPosition.setY(this.nextStep.getY());
            if (this.currentPosition.equals(this.maze.getFinish())) {
                this.state = State.FINISHED;
                return;
            }
            return;
        }

        this.prepareMove();
        this.previousPoints.push(new Point(this.currentPosition));
        this.currentPosition.setX(this.nextStep.getX());
        this.currentPosition.setY(this.nextStep.getY());
        if (this.currentPosition.equals(this.maze.getFinish())) {
            this.state = State.FINISHED;
            for (ActorRef actor : actors) {
                if (actor.equals(getSelf())) {
                    continue;
                }
                this.previousPoints.push(this.currentPosition);
                Stack<Point> cloneOfPath = new Stack<>();
                cloneOfPath.addAll(this.previousPoints);
                actor.tell(new MazeFinished(cloneOfPath), getSelf());
            }
        }
    }

    private void makeDecision() {
        if (this.state == State.FINISHED) {
            this.getSender().tell(new Object(), this.getSender());
            return;
        }
        if (this.state == State.ON_BEST_PATH) {
            this.getSender().tell(new Object(), this.getSender());
            return;
        }
        this.superSender = this.getSender();
        this.otherAgentsDirections.clear();
        this.otherAgentsDirections.put(Maze.WallDirection.N, 0);
        this.otherAgentsDirections.put(Maze.WallDirection.S, 0);
        this.otherAgentsDirections.put(Maze.WallDirection.W, 0);
        this.otherAgentsDirections.put(Maze.WallDirection.E, 0);
        this.otherAgentsDirections.put(Maze.WallDirection.X, 0);
        this.couter = this.actors.size() - 1;
        askForDecisionsInCurrentPlace();
    }

    private Maze.WallDirection[] getDirections() {
        Maze.WallDirection directions[] = {Maze.WallDirection.N, Maze.WallDirection.S, Maze.WallDirection.W, Maze.WallDirection.E};
        Collections.shuffle(Arrays.asList(directions));
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

    private int getMoveScore(Maze.WallDirection direction) {
        int score = 0;
        Point newPoint = getNew(direction);
        if (this.previousTurns.containsKey(this.currentPosition)) {
            Map<Maze.WallDirection, Integer> wallDirections = this.previousTurns.get(this.currentPosition);
            score -= wallDirections.getOrDefault(direction, 0);
        }
        score -= 10 * this.otherAgentsDirections.getOrDefault(direction, 0);
        //score += ThreadLocalRandom.current().nextInt(0, 50);
        score += computeDistanceFromStart(newPoint);
        return score;
    }

    private int computeDistanceFromStart(Point newPoint) {
        int x = this.maze.getWidth() - 1;
        int y = this.maze.getHeight() - 1;
        x -= newPoint.getX();
        y -= newPoint.getY();
        return x + y;
    }

    private boolean moveValid(Maze.WallDirection direction) {
        Point newPoint = getNew(direction);
        //return !this.previousPoints.contains(newPoint) && !((newPoint.getX() < 0) || (newPoint.getX() > this.maze.getWidth() - 1) || (newPoint.getY() < 0) || (newPoint.getY() > this.maze.getHeight() - 1)) && !this.maze.isWallAt(this.currentPosition, direction);
        return !((newPoint.getX() < 0) || (newPoint.getX() > this.maze.getWidth() - 1) || (newPoint.getY() < 0) || (newPoint.getY() > this.maze.getHeight() - 1)) && !this.maze.isWallAt(this.currentPosition, direction);
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
