import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * The RobotGrid class ties together the grid, the robots, and the control panel
 * It also implements logic for generating collectibles and responding to control events
 */
public class RobotGrid extends JFrame implements ControlPanel.ControlListener, KeyListener, Robot.CollectibleListener {
    public static final int ROWS = 25;
    public static final int COLS = 25;

    private Cell[][] cells = new Cell[ROWS][COLS];
    private JPanel gridPanel;
    private ControlPanel controlPanel;
    private ArrayList<Robot> robots = new ArrayList<>();

    private int moveDelay = 500;

    private boolean manualMode = false;

    private Robot selectedRobot = null; /// Robot selected for manual control

    private int score = 0; // Global score for the collectible

    private Timer collectibleTimer; // Time that generate the collectible

    public RobotGrid() {
        setTitle("Interactive Robot Grid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create and initialize the grid panel
        gridPanel = new JPanel(new GridLayout(ROWS, COLS));
        initializeCells();
        add(gridPanel, BorderLayout.CENTER);

        // Create the control panel and pass 'this' as the listener
        controlPanel = new ControlPanel(this);
        add(controlPanel, BorderLayout.SOUTH);

        setSize(800, 800);
        setLocationRelativeTo(null);
        setVisible(true);

        // Add key listener for manual control
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        // Mouse listener for cell clicks
        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cellWidth = gridPanel.getWidth() / COLS;
                int cellHeight = gridPanel.getHeight() / ROWS;
                int col = e.getX() / cellWidth;
                int row = e.getY() / cellHeight;
                if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
                    Cell cell = cells[row][col];
                    if (manualMode) {
                        // In manual mode, if a robot is in the clicked cell, select it
                        synchronized (cell) {
                            if (cell.isOccupied()) {
                                Robot clickedRobot = getRobotAt(row, col);
                                if (clickedRobot != null) {
                                    if (selectedRobot != null) {
                                        selectedRobot.setManualControl(false);
                                        cells[selectedRobot.getRow()][selectedRobot.getCol()]
                                                .setBorder(BorderFactory.createLineBorder(Color.GRAY));
                                    }
                                    selectedRobot = clickedRobot;
                                    selectedRobot.setManualControl(true);
                                    cell.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                                }
                            }
                        }
                    } else {
                        // When not in manual mode, toggle an obstacle
                        synchronized (cell) {
                            if (!cell.isOccupied()) {
                                cell.toggleObstacle();
                            }
                        }
                    }
                    requestFocusInWindow();
                }
            }
        });

        // Ensure the frame regains focus when activated
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                requestFocusInWindow();
            }
        });

        // Create initial robots
        createInitialRobots();

        // Set up a timer to generate collectibles every 4  seconds
        collectibleTimer = new Timer(4000, e -> spawnCollectible());
        collectibleTimer.start();
    }

    // Initializes all grid cells
    private void initializeCells() {
        gridPanel.removeAll();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cells[i][j] = new Cell();
                gridPanel.add(cells[i][j]);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // Returns the robot at the specified position, or null if none.
    private Robot getRobotAt(int row, int col) {
        for (Robot robot : robots) {
            if (robot.getRow() == row && robot.getCol() == col) {
                return robot;
            }
        }
        return null;
    }

    // ------------------ Action : ControlPanel Callback Methods ------------------------------------------------------

    @Override
    public void onStart() {
        for (Robot robot : robots) {
            robot.setPaused(false);
        }
        requestFocusInWindow();
    }

    @Override
    public void onPause() {
        for (Robot robot : robots) {
            robot.setPaused(true);
        }
        requestFocusInWindow();
    }

    @Override
    public void onReset() {
        for (Robot robot : robots) {
            robot.stopRobot();
        }
        robots.clear();
        // Clear grid: remove robot colors, reset borders, remove obstacles and collectibles.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                synchronized(cells[i][j]) {
                    cells[i][j].clear();
                    cells[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    if (cells[i][j].isObstacle()) {
                        cells[i][j].setObstacle(false);
                    }
                    if (cells[i][j].isCollectible()) {
                        cells[i][j].clearCollectible();
                    }
                }
            }
        }
        score = 0;
        controlPanel.updateScore(score);
        createInitialRobots();
        requestFocusInWindow();
    }

    @Override
    public void onAddRobot() {
        Color newColor = new Color((int)(Math.random() * 0x1000000));
        // Pass 'this' as the CollectibleListener.
        Robot newRobot = new Robot(newColor, cells, ROWS, COLS, moveDelay, this);
        robots.add(newRobot);
        newRobot.start();
        requestFocusInWindow();
    }

    @Override
    public void onRemoveRobot() {
        Robot toRemove = (selectedRobot != null) ? selectedRobot : (robots.isEmpty() ? null : robots.get(robots.size() - 1));
        if (toRemove != null) {
            toRemove.stopRobot();
            int r = toRemove.getRow();
            int c = toRemove.getCol();
            synchronized(cells[r][c]) {
                cells[r][c].clear();
            }
            robots.remove(toRemove);
            if (toRemove == selectedRobot) {
                selectedRobot = null;
            }
        }
        requestFocusInWindow();
    }

    @Override
    public void onToggleManual(boolean manualMode) {
        this.manualMode = manualMode;
        if (!manualMode && selectedRobot != null) {
            selectedRobot.setManualControl(false);
            int r = selectedRobot.getRow();
            int c = selectedRobot.getCol();
            cells[r][c].setBorder(BorderFactory.createLineBorder(Color.GRAY));
            selectedRobot = null;
        }
        requestFocusInWindow();
    }

    @Override
    public void onSpeedChange(int delay) {
        moveDelay = delay;
        for (Robot robot : robots) {
            robot.setMoveDelay(moveDelay);
        }
        requestFocusInWindow();
    }

    @Override
    public void onGenerateMaze() {
        // Pause robot movement while generating the maze.
        for (Robot robot : robots) {
            robot.setPaused(true);
        }
        // Remove existing obstacles.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cells[i][j].setObstacle(false);
            }
        }
        // Fill every cell with an obstacle.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cells[i][j].setObstacle(true);
            }
        }
        // randomized Prim’s algorithm
        ArrayList<int[]> walls = new ArrayList<>();
        Random rand = new Random();
        // Choose a random odd-indexed starting cell.
        int startRow = rand.nextInt(ROWS);
        if (startRow % 2 == 0) startRow = (startRow == ROWS - 1) ? startRow - 1 : startRow + 1;
        int startCol = rand.nextInt(COLS);
        if (startCol % 2 == 0) startCol = (startCol == COLS - 1) ? startCol - 1 : startCol + 1;
        cells[startRow][startCol].setObstacle(false);
        addWalls(startRow, startCol, walls);
        while (!walls.isEmpty()) {
            int[] wall = walls.remove(rand.nextInt(walls.size()));
            int r = wall[0], c = wall[1];
            int[] opposite = getOppositeCell(r, c);
            if (opposite != null) {
                int orow = opposite[0], ocol = opposite[1];
                if (cells[orow][ocol].isObstacle()) {
                    cells[r][c].setObstacle(false);
                    cells[orow][ocol].setObstacle(false);
                    addWalls(orow, ocol, walls);
                }
            }
        }
        // Resume robot movement.
        for (Robot robot : robots) {
            robot.setPaused(false);
        }
        requestFocusInWindow();
    }

    /**
     * Helper method to add walls (neighbors two cells away) from the cell at (row, col).
     */
    private void addWalls(int row, int col, ArrayList<int[]> walls) {
        int[][] directions = { { -2, 0 }, { 2, 0 }, { 0, -2 }, { 0, 2 } };
        for (int[] d : directions) {
            int newRow = row + d[0];
            int newCol = col + d[1];
            if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS) {
                if (cells[newRow][newCol].isObstacle()) {
                    int wallRow = row + d[0] / 2;
                    int wallCol = col + d[1] / 2;
                    boolean duplicate = false;
                    for (int[] w : walls) {
                        if (w[0] == wallRow && w[1] == wallCol) {
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        walls.add(new int[]{wallRow, wallCol});
                    }
                }
            }
        }
    }

    /**
     * Given a wall cell at (wallRow, wallCol), returns the coordinates of the cell
     * on the opposite side of the wall relative to a passage.
     */
    private int[] getOppositeCell(int wallRow, int wallCol) {
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        for (int[] d : directions) {
            int passageRow = wallRow - d[0];
            int passageCol = wallCol - d[1];
            int oppositeRow = wallRow + d[0];
            int oppositeCol = wallCol + d[1];
            if (passageRow >= 0 && passageRow < ROWS && passageCol >= 0 && passageCol < COLS &&
                    oppositeRow >= 0 && oppositeRow < ROWS && oppositeCol >= 0 && oppositeCol < COLS) {
                if (!cells[passageRow][passageCol].isObstacle()) {
                    return new int[]{oppositeRow, oppositeCol};
                }
            }
        }
        return null;
    }

    /**
     * Spawns a collectible in a random free cell.
     */
    private void spawnCollectible() {
        Random rand = new Random();
        int r = rand.nextInt(ROWS);
        int c = rand.nextInt(COLS);
        synchronized(cells[r][c]) {
            // Only place a collectible if the cell is free, not an obstacle, and not already holding one.
            if (!cells[r][c].isOccupied() && !cells[r][c].isObstacle() && !cells[r][c].isCollectible()) {
                cells[r][c].setCollectible(true);
            }
        }
    }

    // ------------------ KeyListener Methods for Manual Control ------------------

    @Override
    public void keyPressed(KeyEvent e) {
        if (selectedRobot != null && selectedRobot.isManualControl()) {
            String direction = null;
            switch(e.getKeyCode()){
                case KeyEvent.VK_UP:    direction = "UP"; break;
                case KeyEvent.VK_DOWN:  direction = "DOWN"; break;
                case KeyEvent.VK_LEFT:  direction = "LEFT"; break;
                case KeyEvent.VK_RIGHT: direction = "RIGHT"; break;
            }
            if (direction != null) {
                selectedRobot.manualMove(direction);
                // Reset all cell borders.
                for (int i = 0; i < ROWS; i++){
                    for (int j = 0; j < COLS; j++){
                        cells[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    }
                }
                // Highlight the selected robot's cell.
                int r = selectedRobot.getRow();
                int c = selectedRobot.getCol();
                cells[r][c].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            }
        }
    }
    // No need for them but I must put them here so my events can work properly
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    // ------------------ Robot.CollectibleListener Implementation ------------------

    /**
     * Called by a robot when it collects a collectible.
     * Increments the score and updates the display.
     */
    @Override
    public void collectibleCollected(Robot r) {
        score++;
        SwingUtilities.invokeLater(() -> controlPanel.updateScore(score));
    }

    // ------------------ Helper Method to Create Initial Robots ------------------

    private void createInitialRobots() {
        // Create 5 robots with distinct colors.
        Robot robot1 = new Robot(Color.RED, cells, ROWS, COLS, moveDelay, this);
        Robot robot2 = new Robot(Color.BLUE, cells, ROWS, COLS, moveDelay, this);
        Robot robot3 = new Robot(Color.GREEN, cells, ROWS, COLS, moveDelay, this);
        Robot robot4 = new Robot(Color.ORANGE, cells, ROWS, COLS, moveDelay, this);
        Robot robot5 = new Robot(Color.MAGENTA, cells, ROWS, COLS, moveDelay, this);
        robots.add(robot1);
        robots.add(robot2);
        robots.add(robot3);
        robots.add(robot4);
        robots.add(robot5);

        // Start each robot's thread.
        for (Robot r : robots) {
            r.start();
        }
    }

    // ------------------ Main Method ------------------

    public static void main(String[] args) {
        /*
        SwingUtilities.invokeLater() is a best practice in Swing programming
        for deferring UI updates to the EDT ->Event Dispatch Thread,
        ensuring thread safety and a responsive user interface
         */
        SwingUtilities.invokeLater(() -> new RobotGrid());
    }
}
