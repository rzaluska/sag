package sag.model.maze;

public interface Maze {
    public enum WallDirection {
        X, N, S, W, E;

        public int getDirection() {
            if (this.ordinal() == 0) {
                return 0;
            }
            return 1 << this.ordinal();
        }

    }

    public boolean isWallAt(int x, int y, WallDirection direction);
    public int getFinishX();
    public int getFinishY();

    public int getWidth();

    public int getHeight();
}
