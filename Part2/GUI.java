import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GUI {
    private JFrame frame;
    private Race race;
    private JLabel resultLabel;
    private List<JTextField> nameFields;
    private List<JTextField> symbolFields;
    private List<JComboBox<String>> breedComboBoxes;
    private List<JComboBox<String>> coatColourComboBoxes;
    private List<JComboBox<String>> equipmentComboBoxes;
    private List<Horse> horses;
    private double playerMoney = 1000; // Default starting money
    private Horse bettedHorse = null; // Horse the player bets on
    private double betAmount = 0; // Amount the player bets

    private static final String SAVE_FILE = "HorseRaceSimulator/money_save.txt";

    // Define options for breed, coat colour, and equipment
    private static final String[] BREEDS = {
        "Arabian", "Thoroughbred", "Quarter Horse", "Appaloosa", 
        "Paint", "Clydesdale", "Mustang", "Hannoverian", 
        "Shetland Pony", "Tennessee Walker", "Percheron", 
        "Friesian", "Morgan", "Belgian", "Draft Horse"
    };

    private static final String[] COAT_COLOURS = {
        "Bay", "Chestnut", "Black", "Grey", "Palomino", 
        "Roan", "Dapple Grey", "Buckskin", "Liver Chestnut", 
        "Cremello", "Pinto", "Appaloosa", "Palomino", "White", "Sooty"
    };

    private static final String[] EQUIPMENT = {
        "Saddle", "Bridle", "Racing Silks", "None", "Horse Boots", 
        "Martingale", "Breastplate", "Girth", "Ear Bonnet", 
        "Fly Mask", "Saddle Pad", "Lunge Line", "Halters", 
        "Blanket", "Western Saddle"
    };

    public GUI() {
        // Initialise frame and components
        frame = new JFrame("Horse Race Customisation and Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLayout(new FlowLayout());

        resultLabel = new JLabel("Press 'Start Race' to begin!");
        JLabel moneyLabel = new JLabel("Money: $" + playerMoney);

        // Load money from save file, if available
        loadMoney();
        moneyLabel.setText("Money: $" + playerMoney);

        // Prompt the user to input the number of horses and the race length
        int numberOfHorses = getNumberOfHorses();
        int raceLength = getRaceLength();

        // Create a list of horses, name fields, symbol fields, and combo boxes
        horses = new ArrayList<>();
        nameFields = new ArrayList<>();
        symbolFields = new ArrayList<>();
        breedComboBoxes = new ArrayList<>();
        coatColourComboBoxes = new ArrayList<>();
        equipmentComboBoxes = new ArrayList<>();

        // Add horse customisation fields dynamically
        for (int i = 1; i <= numberOfHorses; i++) {
            Horse horse = new Horse("Horse " + i, '♘', 0.5);
            horses.add(horse);

            JTextField horseNameField = new JTextField(horse.getName(), 10);
            JTextField horseSymbolField = new JTextField(String.valueOf(horse.getSymbol()), 2);
            JComboBox<String> breedComboBox = new JComboBox<>(BREEDS);
            JComboBox<String> coatColourComboBox = new JComboBox<>(COAT_COLOURS);
            JComboBox<String> equipmentComboBox = new JComboBox<>(EQUIPMENT);

            frame.add(new JLabel("Horse " + i + " Name:"));
            frame.add(horseNameField);
            frame.add(new JLabel("Symbol:"));
            frame.add(horseSymbolField);
            frame.add(new JLabel("Breed:"));
            frame.add(breedComboBox);
            frame.add(new JLabel("Coat Colour:"));
            frame.add(coatColourComboBox);
            frame.add(new JLabel("Equipment:"));
            frame.add(equipmentComboBox);

            nameFields.add(horseNameField);
            symbolFields.add(horseSymbolField);
            breedComboBoxes.add(breedComboBox);
            coatColourComboBoxes.add(coatColourComboBox);
            equipmentComboBoxes.add(equipmentComboBox);
        }

        // Button to apply customisations
        JButton applyCustomisationsButton = new JButton("Apply Customisations");
        applyCustomisationsButton.addActionListener(_ -> applyCustomisations());

        // Button to start the race
        JButton startRaceButton = new JButton("Start Race");
        startRaceButton.addActionListener(_ -> startRace(raceLength, moneyLabel));

        // Betting button
        JButton betButton = new JButton("Place Bet");
        betButton.addActionListener(_ -> placeBet(moneyLabel));

        // Save money button
        JButton saveButton = new JButton("Save Money");
        saveButton.addActionListener(_ -> saveMoney());

        // Load money button
        JButton loadButton = new JButton("Load Money");
        loadButton.addActionListener(_ -> {
            loadMoney();
            moneyLabel.setText("Money: $" + playerMoney);
        });

        // Reset money button
        JButton resetButton = new JButton("Start New Game");
        resetButton.addActionListener(_ -> {
            int response = JOptionPane.showConfirmDialog(
                frame, "Start a new game with $1000?", 
                "Confirm New Game", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                playerMoney = 1000;
                saveMoney();
                moneyLabel.setText("Money: $" + playerMoney);
            }
        });

        // Add components to the frame
        frame.add(applyCustomisationsButton);
        frame.add(resultLabel);
        frame.add(moneyLabel);
        frame.add(betButton);
        frame.add(resetButton);
        frame.add(saveButton);
        frame.add(loadButton);
        frame.add(startRaceButton);

        // Display the GUI
        frame.setVisible(true);
    }

    private void placeBet(JLabel moneyLabel) {
        if (horses.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No horses to bet on.");
            return;
        }

        String horseName = JOptionPane.showInputDialog("Enter the horse name to bet on:");
        bettedHorse = horses.stream().filter(h -> h.getName().equalsIgnoreCase(horseName)).findFirst().orElse(null);

        if (bettedHorse == null) {
            JOptionPane.showMessageDialog(frame, "Horse not found.");
            return;
        }

        String betAmountStr = JOptionPane.showInputDialog("Enter your bet amount:");
        try {
            betAmount = Double.parseDouble(betAmountStr);
            if (betAmount > playerMoney) {
                JOptionPane.showMessageDialog(frame, "Insufficient funds for this bet.");
                bettedHorse = null;
            } else {
                playerMoney -= betAmount;
                JOptionPane.showMessageDialog(frame, "Bet placed on " + horseName + " for $" + betAmount + "!");
                moneyLabel.setText("Money: $" + playerMoney);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid bet amount.");
            bettedHorse = null;
        }
    }

    // Apply the customisations to the horses
    private void applyCustomisations() {
        for (int i = 0; i < horses.size(); i++) {
            Horse horse = horses.get(i);
            horse.setName(nameFields.get(i).getText());

            // Apply new symbol (ensuring it's a valid single character)
            if (!symbolFields.get(i).getText().isEmpty()) {
                horse.setSymbol(symbolFields.get(i).getText().charAt(0));
            }

            // Apply breed, coat colour, and equipment
            horse.setBreed((String) breedComboBoxes.get(i).getSelectedItem());
            horse.setCoatColour((String) coatColourComboBoxes.get(i).getSelectedItem());
            horse.setEquipment((String) equipmentComboBoxes.get(i).getSelectedItem());
        }
        JOptionPane.showMessageDialog(frame, "Customisations applied!");
    }

    // Start the race
    private void startRace(int raceLength, JLabel moneyLabel) {
        resultLabel.setText("The race is on!");
        race = new Race(raceLength, horses.size());

        // Add horses to the race
        for (int i = 0; i < horses.size(); i++) {
            race.addHorse(horses.get(i), i + 1);
        }

        // Start the race and handle the result
        race.startRace(winner -> {
            resultLabel.setText("The winner is: " + winner);
            JOptionPane.showMessageDialog(frame, "Congratulations! " + winner + " won the race.");

            if (bettedHorse != null && winner.equals(bettedHorse.getName())) {
                playerMoney += betAmount * 2; // Double the bet amount if the player wins
                JOptionPane.showMessageDialog(frame, "You won the bet! You earned $" + (betAmount * 2) + "!");
            } else if (bettedHorse != null) {
                JOptionPane.showMessageDialog(frame, "You lost the bet.");
            }

            // Reset bet for the next race
            bettedHorse = null;
            betAmount = 0;
            moneyLabel.setText("Money: $" + playerMoney);
        });
    }

    private int getNumberOfHorses() {
        int numberOfHorses = 0;
        while (numberOfHorses < 2 || numberOfHorses > 10) {
            String input = JOptionPane.showInputDialog("Enter the number of horses (between 2 and 10):");
            try {
                numberOfHorses = Integer.parseInt(input);
                if (numberOfHorses < 2 || numberOfHorses > 10) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number between 2 and 10.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            }
        }
        return numberOfHorses;
    }

    private int getRaceLength() {
        int raceLength = 0;
        while (raceLength < 10 || raceLength > 100) {
            String input = JOptionPane.showInputDialog("Enter the race length (between 10 and 100):");
            try {
                raceLength = Integer.parseInt(input);
                if (raceLength < 10 || raceLength > 100) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid length between 10 and 100.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            }
        }
        return raceLength;
    }

    // Save money to a file
    private void saveMoney() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            writer.println(playerMoney);
            JOptionPane.showMessageDialog(frame, "Money saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to save money.");
        }
    }

    // Load money from a file
    private void loadMoney() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            playerMoney = Double.parseDouble(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            playerMoney = 1000; // Default starting money if file not found or corrupted
        }
    }
}
