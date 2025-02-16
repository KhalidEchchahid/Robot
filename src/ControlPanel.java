import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;

/**
 * The ControlPanel class creates a panel with controls for the simulation.
 * It includes buttons to start, pause, reset, add/remove robots, generate maze,
 * and a slider to adjust robot speed. A score label shows the current collectible score.
 */
public class ControlPanel extends JPanel {
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JButton addRobotButton;
    private JButton removeRobotButton;
    private JButton generateMazeButton;
    private JToggleButton manualToggleButton;
    private JSlider speedSlider;
    private JLabel scoreLabel;  // Displays the current score.

    /**
     * Interface for handling control events.
     */
    public interface ControlListener {
        void onStart();
        void onPause();
        void onReset();
        void onAddRobot();
        void onRemoveRobot();
        void onToggleManual(boolean manualMode);
        void onSpeedChange(int delay);
        void onGenerateMaze();
    }

    /**
     * Constructs the ControlPanel.
     * listener to handle control events
     */
    public ControlPanel(ControlListener listener) {
        //BoxLayout for vertical stacking
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");
        addRobotButton = new JButton("Add Robot");
        removeRobotButton = new JButton("Remove Robot");
        generateMazeButton = new JButton("Generate Maze");
        manualToggleButton = new JToggleButton("Manual Control");

        // Add buttons to the panel
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(addRobotButton);
        buttonPanel.add(removeRobotButton);
        buttonPanel.add(generateMazeButton);
        buttonPanel.add(manualToggleButton);

        // panel for the slider and score
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        speedSlider = new JSlider(50, 1000, 500);  // Delay from 50ms to 1000ms.
        sliderPanel.add(new JLabel("Speed:"));
        sliderPanel.add(speedSlider);

        scoreLabel = new JLabel("Score: 0");
        sliderPanel.add(scoreLabel);

        // Add both panels to the main control panel
        add(buttonPanel);
        add(sliderPanel);

        // Set up action listeners for each control
        startButton.addActionListener(e -> listener.onStart());
        pauseButton.addActionListener(e -> listener.onPause());
        resetButton.addActionListener(e -> listener.onReset());
        addRobotButton.addActionListener(e -> listener.onAddRobot());
        removeRobotButton.addActionListener(e -> listener.onRemoveRobot());
        generateMazeButton.addActionListener(e -> listener.onGenerateMaze());
        manualToggleButton.addActionListener(e -> listener.onToggleManual(manualToggleButton.isSelected()));
        speedSlider.addChangeListener(e -> listener.onSpeedChange(speedSlider.getValue()));
    }

     //Updates the displayed score
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }
}
