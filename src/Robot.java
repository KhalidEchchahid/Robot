import java.awt.*;
import java.util.Random;

/**
 * The Robot class represents a robot that moves on the grid
 * It runs on its own thread
 */
public class Robot extends Thread {

    private int row, col; // Current grid position

    private final Color color;

    private final Cell[][] cells; // The grid cells

    private final int rows, cols; // Grid dimensions

    private volatile boolean manualControl = false;

    private volatile boolean running = true; // Flag indicate the Thread running

    private volatile boolean paused = false;

    private int moveDelay;

    private final Random random = new Random(); // Random generator for movement.

    private final CollectibleListener listener; // Listener to report when a collectible is collected


    /**
     * Interface for reporting collectible events
     * this must need more explanation
     */
    public interface CollectibleListener {
        void collectibleCollected(Robot r);
    }

    /**
     * cells : the grid cells.
     * rows : number of rows.
     * cols : number of columns.
     * listener : listener to report collectible collection
     */
    public Robot(Color color, Cell[][] cells, int rows, int cols, int moveDelay, CollectibleListener listener) {
        this.color = color;
        this.cells = cells;
        this.rows = rows;
        this.cols = cols;
        this.moveDelay = moveDelay;
        this.listener = listener;
        // Place robot in a random free and non-obstacle cell
        boolean placed = false;
        while (!placed) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            synchronized (cells[r][c]) {
                if (!cells[r][c].isOccupied() && !cells[r][c].isObstacle()) {
                    cells[r][c].setOccupied(color);
                    row = r;
                    col = c;
                    placed = true;
                }
            }
        }
    }

    // Getters for current position and color.
    public int getRow() { return row; }
    public int getCol() { return col; }
    public Color getColor() { return color; }

    // Sets a new move delay.
    public void setMoveDelay(int delay) {
        moveDelay = delay;
    }

    // Enables or disables manual control.
    public void setManualControl(boolean manual) {
        manualControl = manual;
    }

    // Returns whether the robot is in manual control mode
    public boolean isManualControl() {
        return manualControl;
    }

    // Stops the robot thread
    public void stopRobot() {
        running = false;
        this.interrupt();
    }

    /**
     * Attempts to move the robot to (newRow, newCol)
     * If successful, also checks for collectible collection
     */
    public boolean attemptMove(int newRow, int newCol) {
        // Check grid boundaries
        if(newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) return false;
        synchronized (cells[newRow][newCol]) {
            // Move only if destination is free and not an obstacle
            if (!cells[newRow][newCol].isOccupied() && !cells[newRow][newCol].isObstacle()) {
                // Clear the current cell
                synchronized (cells[row][col]) {
                    cells[row][col].clear();
                }
                // Occupy the new cell
                cells[newRow][newCol].setOccupied(color);
                // Update internal position
                row = newRow;
                col = newCol;
                // If the new cell has a collectible, remove it and notify listener
                if (cells[newRow][newCol].isCollectible()) {
                    cells[newRow][newCol].clearCollectible();
                    if(listener != null)
                        listener.collectibleCollected(this);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Moves the robot manually in the specified direction.
     * direction one of "UP", "DOWN", "LEFT", "RIGHT"
     */
    public boolean manualMove(String direction) {
        int newRow = row;
        int newCol = col;
        switch(direction) {
            case "UP":    newRow--; break;
            case "DOWN":  newRow++; break;
            case "LEFT":  newCol--; break;
            case "RIGHT": newCol++; break;
            default: return false;
        }
        return attemptMove(newRow, newCol);
    }

    /**
     * The main loop for the robot.
     * Moves automatically unless paused or in manual control.
     */
    @Override
    public void run() {
        while(running) {
            try {
                Thread.sleep(moveDelay);
            } catch (InterruptedException e) {
                if (!running) break;
            }
            // Skip automatic movement if paused or manually controlled.
            if(paused || manualControl) continue;

            // Choose a random direction.
            int dir = random.nextInt(4);
            int newRow = row;
            int newCol = col;
            switch (dir) {
                case 0: newRow--; break;
                case 1: newRow++; break;
                case 2: newCol--; break;
                case 3: newCol++; break;
            }
            attemptMove(newRow, newCol);
        }
    }

    // Sets the paused state.
    public void setPaused(boolean pause) {
        paused = pause;
    }
}
