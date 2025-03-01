# Robot Grid Simulation

## Introduction
Welcome to the **Robot Grid Simulation**! This fun and interactive project brings a **25 × 25 grid** to life with **at least 5 robots** moving around randomly. Each robot is represented as a colorful square, navigating the grid without bumping into each other or stepping out of bounds.

This project is built using **Java Swing** for the graphical interface and leverages **multithreading and synchronization** to ensure smooth and conflict-free movement.

## Features
- 🎨 **Visual Representation**: Watch as colorful robots move around the grid in real time.
- 🎲 **Random Movement**: Each robot moves unpredictably in any of the four directions.
- 🚧 **Collision-Free Navigation**: Robots never occupy the same cell.
- ⚡ **Multithreading Magic**: Every robot operates independently in its own thread.
- 🔒 **Synchronization**: Keeps everything running smoothly without conflicts.

## Technologies Used
- **Java** (JDK 8 or later)
- **Swing** (for the graphical user interface)
- **Multithreading** (for independent robot movement)
- **Synchronization** (to prevent overlapping issues)

## How to Get Started
1. **Clone the Repository**
   ```sh
   git clone https://github.com/your-username/robot.git
   cd robot
   ```
2. **Compile the Code**
   ```sh
   javac *.java
   ```
3. **Run the Simulation**
   ```sh
   java RobotGrid
   ```

## How It Works
1. The program launches a **25 × 25 grid**.
2. At least **5 robots** appear in random positions.
3. Each robot moves freely within the grid, picking a new direction as needed.
4. Robots adjust their paths when they reach the grid's edge, ensuring they stay inside.
5. The system prevents robots from overlapping, maintaining an orderly simulation.

## What's Next?
- 🕹️ Add controls to start/stop robots manually.
- 🔲 Introduce obstacles for robots to navigate around.
- ⚡ Implement speed variations for an extra challenge.
- 👤 Let users decide how many robots to include in the simulation.

## License
This project is intended for educational purposes. Feel free to use, modify, and share it however you like.

## Author
👤 **Khalid Echchahid** - [GitHub](https://github.com/khalidEchchahid)
