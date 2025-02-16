import javax.swing.*;
import java.awt.*;

/**
 * The Cell class represents a single cell in the grid
 */
public class Cell extends JPanel {

    private Color occupant = null;
    private boolean isObstacle = false;
    private boolean collectible = false;     // Flag indicating whether this cell contains a collectible


    public Cell() {
        // Set a gray border
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        // Update background color based on current state
        updateColor();
    }

    // Returns true if the cell is occupied by a robot
    public synchronized boolean isOccupied() {
        return occupant != null;
    }

    // Marks the cell as occupied by a robot (with the given color)
    public synchronized void setOccupied(Color color) {
        occupant = color;
        updateColor();
    }

    // Clears the cell (removes any occupant)
    public synchronized void clear() {
        occupant = null;
        updateColor();
    }

    // Returns the robot color occupying the cell
    public synchronized Color getOccupant() {
        return occupant;
    }

    // Returns true if the cell is an obstacle
    public synchronized boolean isObstacle() {
        return isObstacle;
    }

    // Toggles the obstacle flag only if the cell is not occupied
    public synchronized void toggleObstacle() {
        if (!isOccupied()) {
            isObstacle = !isObstacle;
            updateColor();
        }
    }

    //sets or clears the obstacle flag
    public synchronized void setObstacle(boolean obstacle) {
        isObstacle = obstacle;
        updateColor();
    }

    // Clears the obstacle flag.
    public synchronized void clearObstacle() {
        isObstacle = false;
        updateColor();
    }

    // Returns true if the cell currently holds a collectible
    public synchronized boolean isCollectible() {
        return collectible;
    }

    // Sets the cell's collectible state
    public synchronized void setCollectible(boolean collectible) {
        // Only set a collectible if the cell is not occupied and not an obstacle
        if (!isOccupied() && !isObstacle) {
            this.collectible = collectible;
            updateColor();
        }
    }

    // Clears the collectible flag
    public synchronized void clearCollectible() {
        collectible = false;
        updateColor();
    }

    /**
     * Updates the background color based on the cell's state
     * Priority: obstacle --> robot occupant --> collectible -> empty
     */
    private void updateColor() {
        if (isObstacle) {
            setBackground(Color.DARK_GRAY);
        } else if (occupant != null) {
            setBackground(occupant);
        } else if (collectible) {
            // Gold color.
            setBackground(new Color(212, 175, 55));
        } else {
            setBackground(Color.WHITE);
        }
        repaint();
    }
}
