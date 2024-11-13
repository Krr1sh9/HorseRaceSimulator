import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McFarewell
 * @version 1.0
 */
public class Race extends JFrame {
    private int raceLength;
    private Horse[] lanes;
    private JTextArea raceDisplay;

    /**
     * Constructor for objects of class Race
     * Initially there are no horses in the lanes
     * 
     * @param distance the length of the racetrack (in metres/yards...)
     */
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

        // Bring the race window to the front
        toFront();
        requestFocus();
    }

    /**
     * Adds a horse to the race in a given lane
     * 
     * @param theHorse the horse to be added to the race
     * @param laneNumber the lane that the horse will be added to
     */
    public void addHorse(Horse theHorse, int laneNumber) {
        if (laneNumber >= 1 && laneNumber <= lanes.length) {
            lanes[laneNumber - 1] = theHorse;
        } else {
            System.out.println("Cannot add horse to lane " + laneNumber + " because there is no such lane");
        }
    }

    /**
     * Start the race
     * The horse are brought to the start and
     * then repeatedly moved forward until the 
     * race is finished
     */
    public void startRace(RaceCompletionHandler handler) {
        SwingWorker<Void, Void> raceWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                boolean finished = false;

                for (Horse horse : lanes) {
                    horse.goBackToStart();
                }

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

                SwingUtilities.invokeLater(() -> {
                    for (Horse horse : lanes) {
                        if (raceWonBy(horse)) {
                            raceDisplay.append("\nAnd the winner is " + horse.getName());
                            updateConfidence(horse, lanes);
                            handler.onRaceCompleted(getWinner());
                        }
                    }
                });

                return null;
            }
        };

        raceWorker.execute();
    }

    /**
     * Randomly make a horse move forward or fall depending
     * on its confidence rating
     * A fallen horse cannot move
     * 
     * @param theHorse the horse to be moved
     */
    private void moveHorse(Horse theHorse) {
        if (!theHorse.hasFallen()) {
            if (Math.random() < theHorse.getConfidence()) {
                theHorse.moveForward();
            }
            if (Math.random() < (0.1 * theHorse.getConfidence() * theHorse.getConfidence())) {
                theHorse.fall();
            }
        }
    }

    /** 
     * Determines if a horse has won the race
     *
     * @param theHorse The horse we are testing
     * @return true if the horse has won, false otherwise.
     */
    private boolean raceWonBy(Horse theHorse) {
        return theHorse.getDistanceTravelled() >= raceLength;
    }

    /***
     * Print the race on the race GUI
     */
    private void printRace() {
        raceDisplay.setText(""); // Clear the display before printing each update
        String trackBorder = "=".repeat(raceLength + 2); // Make the border equal to the race length (+2 for the boundaries)
        raceDisplay.append(trackBorder + "\n");

        for (Horse horse : lanes) {
            printLane(horse);
            raceDisplay.append("\n");
        }

        raceDisplay.append(trackBorder);
    }

    /**
     * print a horse's lane during the race
     * for example
     * |           X                      |
     * to show how far the horse has run
     */
    private void printLane(Horse theHorse) {
        int spacesBefore = theHorse.getDistanceTravelled(); // Spaces before the horse
        int spacesAfter = raceLength - theHorse.getDistanceTravelled(); // Remaining spaces to the right

        raceDisplay.append("|");
        raceDisplay.append(" ".repeat(Math.max(0, spacesBefore))); // Print spaces before the horse

        if (theHorse.hasFallen()) {
            raceDisplay.append("❌"); // If the horse has fallen, display '❌'
        } else {
            raceDisplay.append(String.valueOf(theHorse.getSymbol())); // Print the horse's symbol
        }

        // Ensure we have exactly raceLength spaces in total for the race line
        raceDisplay.append(" ".repeat(Math.max(0, spacesAfter - 1))); // Adjust for the symbol's width
        raceDisplay.append("| " + theHorse.getName() + " (Current confidence " + theHorse.getConfidence() + ")");
    }

    private void updateConfidence(Horse winner, Horse[] losers) {
        winner.setConfidence(Math.min(1.0, Math.round((winner.getConfidence() + 0.1) * 10.0) / 10.0));
        for (Horse loser : losers) {
            if (loser != winner) {
                loser.setConfidence(Math.max(0.1, Math.round((loser.getConfidence() - 0.1) * 10.0) / 10.0));
            }
        }
    }

    public String getWinner() {
        for (Horse horse : lanes) {
            if (raceWonBy(horse)) {
                return horse.getName();
            }
        }
        return "";
    }

    public interface RaceCompletionHandler {
        void onRaceCompleted(String winner);
    }
}
