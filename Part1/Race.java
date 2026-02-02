import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Race extends JFrame {
    private int raceLength; // How long the track is (how many steps to reach the finish)
    private Horse[] lanes; // Each array position is a lane
    private JTextArea raceDisplay; // Where we print the track (text-based race display)
    private static final double FALL_BASE_RATE = 0.01; // Base probability of falling

    // Create the race window and setup the lanes
    public Race(int distance, int numberOfHorses) {
        raceLength = distance;
        lanes = new Horse[numberOfHorses];

        setTitle("Horse Race Simulation");
        setSize(1900, 475);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        raceDisplay = new JTextArea();
        raceDisplay.setEditable(false);

        Font font = new Font("Monospaced", Font.PLAIN, 18);
        raceDisplay.setFont(font);

        JScrollPane scrollPane = new JScrollPane(raceDisplay);
        add(scrollPane, BorderLayout.CENTER);

        // Set window to the bottom-left corner
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameHeight = this.getHeight();
        setLocation(0, screenSize.height - frameHeight); // Bottom-left corner

        setVisible(true);
        toFront();
        requestFocus();
    }

    // Add a horse to a specific lane
    public void addHorse(Horse theHorse, int laneNumber) {
        if (laneNumber >= 1 && laneNumber <= lanes.length) {
            lanes[laneNumber - 1] = theHorse;
        } else {
            System.out.println("Cannot add horse to lane " + laneNumber + " because there is no such lane");
        }
    }

    // Start the race using a SwingWorker
    public void startRace(RaceCompletionHandler handler) {
        SwingWorker<Void, Void> raceWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                boolean finished = false;

                // Reset all horses at the start
                for (Horse horse : lanes) {
                    horse.goBackToStart();
                }

                // Loop until a horse reaches the finish line
                while (!finished) {
                    for (Horse horse : lanes) {
                        moveHorse(horse);
                    }

                    SwingUtilities.invokeLater(Race.this::printRace);
                    for (Horse horse : lanes) {
                        if (raceWonBy(horse)) {
                            finished = true;
                            break;
                        }
                    }

                    TimeUnit.MILLISECONDS.sleep(100);
                }

                // After finishing, show winner text and call the callback
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        for (Horse horse : lanes) {
                            if (raceWonBy(horse)) {
                                raceDisplay.append("\nAnd the winner is " + horse.getName());
                                updateConfidence(horse, lanes);
                                handler.onRaceCompleted(getWinner());
                            }
                        }
                    }
                });

                return null;
            }
        };

        raceWorker.execute();
    }

    // Move a horse based on confidence (and sometimes make it fall)
    private void moveHorse(Horse theHorse) {
        if (!theHorse.hasFallen()) {
            double c = theHorse.getConfidence();

            // Move forward with probability = confidence
            if (Math.random() < c) {
                theHorse.moveForward();
            }

            // Fall probability per tick
            if (Math.random() < (FALL_BASE_RATE * c * c)) {
                theHorse.fall();
            }
        }
    }

    // Check if the horse has reached the finish line
    private boolean raceWonBy(Horse theHorse) {
        return theHorse.getDistanceTravelled() >= raceLength;
    }

    // Draw the whole track for every horse
    private void printRace() {
        raceDisplay.setText(""); // Clear the display before printing each update

        // Border line
        String trackBorder = repeatChar('=', raceLength + 2); // +2 for the boundaries
        raceDisplay.append(trackBorder + "\n");

        // Print each lane underneath
        for (Horse horse : lanes) {
            printLane(horse);
            raceDisplay.append("\n");
        }

        raceDisplay.append(trackBorder);
    }

    // Print one lane
    private void printLane(Horse theHorse) {
        int spacesBefore = theHorse.getDistanceTravelled(); // Spaces before the horse
        int spacesAfter = raceLength - theHorse.getDistanceTravelled(); // Remaining spaces to the right

        raceDisplay.append("|");
        raceDisplay.append(repeatChar(' ', Math.max(0, spacesBefore)));

        if (theHorse.hasFallen()) {
            raceDisplay.append("❌"); // If the horse has fallen
        } else {
            raceDisplay.append(String.valueOf(theHorse.getSymbol())); // Print the horse's symbol
        }

        // Ensure we have exactly raceLength spaces in total for the race line
        raceDisplay.append(repeatChar(' ', Math.max(0, spacesAfter - 1))); // Adjust for symbol width
        raceDisplay.append("| " + theHorse.getName() + " (Current confidence " + theHorse.getConfidence() + ")");
    }

    // Winner gets more confidence, others lose a bit
    private void updateConfidence(Horse winner, Horse[] losers) {
        winner.setConfidence(Math.min(1.0, Math.round((winner.getConfidence() + 0.1) * 10.0) / 10.0));
        for (Horse loser : losers) {
            if (loser != winner) {
                loser.setConfidence(Math.max(0.1, Math.round((loser.getConfidence() - 0.1) * 10.0) / 10.0));
            }
        }
    }

    // Return the winner's name
    public String getWinner() {
        for (Horse horse : lanes) {
            if (raceWonBy(horse)) {
                return horse.getName();
            }
        }
        return "";
    }

    // Helper method to repeat a character
    private static String repeatChar(char c, int count) {
        if (count <= 0)
            return "";
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++)
            sb.append(c);
        return sb.toString();
    }

    // Small interface so GUI can receive the winner result
    public interface RaceCompletionHandler {
        void onRaceCompleted(String winner);
    }
}
