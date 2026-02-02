import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GUI {
    // Main window
    private JFrame frame;

    // Race window (opens when you press Start Race)
    private Race race;

    private JLabel resultLabel;

    // We store horses and the UI inputs in lists so we can access them by index
    private List<JTextField> nameFields;
    private List<JTextField> symbolFields;
    private List<JComboBox<String>> breedComboBoxes;
    private List<JComboBox<String>> coatColourComboBoxes;
    private List<JComboBox<String>> equipmentComboBoxes;
    private List<Horse> horses;

    // Betting and money
    private double playerMoney = 1000;
    private Horse bettedHorse = null;
    private double betAmount = 0;

    private static final String SAVE_FILE = "money_save.txt";

    // Options for the drop-down menus
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

    // Constructor builds the entire GUI
    public GUI() {
        // Create the frame and give it a proper layout
        frame = new JFrame("Horse Race Customisation and Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // FIXED: stops the wrapping/clumping of the UI.
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(root);

        // Labels
        resultLabel = new JLabel("Press 'Start Race' to begin!");
        JLabel moneyLabel = new JLabel("Money: $" + playerMoney);

        // Load money from save file, if available
        loadMoney();
        moneyLabel.setText("Money: $" + playerMoney);

        // Prompt the user to input the number of horses and the race length
        int numberOfHorses = getNumberOfHorses();
        int raceLength = getRaceLength();

        // Lists
        horses = new ArrayList<Horse>();
        nameFields = new ArrayList<JTextField>();
        symbolFields = new ArrayList<JTextField>();
        breedComboBoxes = new ArrayList<JComboBox<String>>();
        coatColourComboBoxes = new ArrayList<JComboBox<String>>();
        equipmentComboBoxes = new ArrayList<JComboBox<String>>();

        // Top: status bar
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.add(moneyLabel, BorderLayout.WEST);
        statusPanel.add(resultLabel, BorderLayout.EAST);
        root.add(statusPanel, BorderLayout.NORTH);

        // Center: horse customisations in a grid
        JPanel horsesPanel = buildHorseCustomisationGrid(numberOfHorses);
        JScrollPane scrollPane = new JScrollPane(
                horsesPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Horse Customisations"));
        root.add(scrollPane, BorderLayout.CENTER);

        // Bottom: buttons
        JButton applyCustomisationsButton = new JButton("Apply Customisations");
        applyCustomisationsButton.addActionListener(e -> applyCustomisations());

        JButton betButton = new JButton("Place Bet");
        betButton.addActionListener(e -> placeBet(moneyLabel));

        JButton startRaceButton = new JButton("Start Race");
        startRaceButton.addActionListener(e -> startRace(raceLength, moneyLabel));

        JButton resetButton = new JButton("Start New Game");
        resetButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    frame, "Start a new game with $1000?",
                    "Confirm New Game", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                playerMoney = 1000;
                saveMoney();
                moneyLabel.setText("Money: $" + playerMoney);
            }
        });

        JButton saveButton = new JButton("Save Money");
        saveButton.addActionListener(e -> saveMoney());

        JButton loadButton = new JButton("Load Money");
        loadButton.addActionListener(e -> {
            loadMoney();
            moneyLabel.setText("Money: $" + playerMoney);
        });

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        buttonPanel.add(applyCustomisationsButton);
        buttonPanel.add(betButton);
        buttonPanel.add(startRaceButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(new JLabel()); // Filler
        buttonPanel.add(new JLabel()); // Filler

        root.add(buttonPanel, BorderLayout.SOUTH);

        // Window sizing
        frame.setSize(1200, 650);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel buildHorseCustomisationGrid(int numberOfHorses) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        // Header row
        addHeader(panel, gbc, 0, "Horse");
        addHeader(panel, gbc, 1, "Name");
        addHeader(panel, gbc, 2, "Symbol");
        addHeader(panel, gbc, 3, "Breed");
        addHeader(panel, gbc, 4, "Coat Colour");
        addHeader(panel, gbc, 5, "Equipment");

        // Data rows
        for (int i = 1; i <= numberOfHorses; i++) {
            gbc.gridy = i;

            Horse horse = new Horse("Horse " + i, '♘', 0.5);
            horses.add(horse);

            JTextField horseNameField = new JTextField(horse.getName(), 12);
            JTextField horseSymbolField = new JTextField(String.valueOf(horse.getSymbol()), 2);
            JComboBox<String> breedComboBox = new JComboBox<String>(BREEDS);
            JComboBox<String> coatColourComboBox = new JComboBox<String>(COAT_COLOURS);
            JComboBox<String> equipmentComboBox = new JComboBox<String>(EQUIPMENT);

            nameFields.add(horseNameField);
            symbolFields.add(horseSymbolField);
            breedComboBoxes.add(breedComboBox);
            coatColourComboBoxes.add(coatColourComboBox);
            equipmentComboBoxes.add(equipmentComboBox);

            // Horse label
            addCell(panel, gbc, 0, new JLabel("Horse " + i), 0.12);

            // Name
            addCell(panel, gbc, 1, horseNameField, 0.22);

            // Symbol
            addCell(panel, gbc, 2, horseSymbolField, 0.10);

            // Breed
            addCell(panel, gbc, 3, breedComboBox, 0.18);

            // Coat Colour
            addCell(panel, gbc, 4, coatColourComboBox, 0.18);

            // Equipment
            addCell(panel, gbc, 5, equipmentComboBox, 0.20);
        }

        gbc.gridy = numberOfHorses + 1;
        gbc.gridx = 0;
        gbc.gridwidth = 6;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    // Adds a header cell
    private void addHeader(JPanel panel, GridBagConstraints gbc, int col, String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));

        gbc.gridx = col;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(label, gbc);
    }

    // Adds one grid cell at (col,row)
    private void addCell(JPanel panel, GridBagConstraints gbc, int col, JComponent comp, double weightx) {
        gbc.gridx = col;
        gbc.gridwidth = 1;
        gbc.weightx = weightx;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comp, gbc);
    }

    // Lets the user bet money on a chosen horse
    private void placeBet(JLabel moneyLabel) {
        if (horses.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No horses to bet on.");
            return;
        }

        String horseName = JOptionPane.showInputDialog(frame, "Enter the horse name to bet on:");
        if (horseName == null)
            return;

        bettedHorse = null;
        for (Horse h : horses) {
            if (h.getName().equalsIgnoreCase(horseName.trim())) {
                bettedHorse = h;
                break;
            }
        }

        if (bettedHorse == null) {
            JOptionPane.showMessageDialog(frame, "Horse not found.");
            return;
        }

        String betAmountStr = JOptionPane.showInputDialog(frame, "Enter your bet amount:");
        if (betAmountStr == null)
            return;

        try {
            betAmount = Double.parseDouble(betAmountStr);
            if (betAmount > playerMoney) {
                JOptionPane.showMessageDialog(frame, "Insufficient funds for this bet.");
                bettedHorse = null;
            } else if (betAmount <= 0) {
                JOptionPane.showMessageDialog(frame, "Bet amount must be greater than 0.");
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

    // Reads values from the UI and stores them into the Horse objects
    private void applyCustomisations() {
        for (int i = 0; i < horses.size(); i++) {
            Horse horse = horses.get(i);
            horse.setName(nameFields.get(i).getText());

            if (!symbolFields.get(i).getText().isEmpty()) {
                horse.setSymbol(symbolFields.get(i).getText().charAt(0));
            }

            // Update the customisation dropdown values
            horse.setBreed((String) breedComboBoxes.get(i).getSelectedItem());
            horse.setCoatColour((String) coatColourComboBoxes.get(i).getSelectedItem());
            horse.setEquipment((String) equipmentComboBoxes.get(i).getSelectedItem());
        }
        JOptionPane.showMessageDialog(frame, "Customisations applied!");
    }

    // Starts the race and handles the result
    private void startRace(int raceLength, JLabel moneyLabel) {
        resultLabel.setText("The race is on!");

        // Create the race window and add all horses
        race = new Race(raceLength, horses.size());

        for (int i = 0; i < horses.size(); i++) {
            race.addHorse(horses.get(i), i + 1); // lanes are 1-indexed
        }

        race.startRace(winner -> {
            resultLabel.setText("The winner is: " + winner);
            JOptionPane.showMessageDialog(frame, "Congratulations! " + winner + " won the race.");

            // Check bet result
            if (bettedHorse != null && winner.equals(bettedHorse.getName())) {
                playerMoney += betAmount * 2;
                JOptionPane.showMessageDialog(frame, "You won the bet! You earned $" + (betAmount * 2) + "!");
            } else if (bettedHorse != null) {
                JOptionPane.showMessageDialog(frame, "You lost the bet.");
            }

            // Reset bet for next time
            bettedHorse = null;
            betAmount = 0;
            moneyLabel.setText("Money: $" + playerMoney);
        });
    }

    // Gets the number of horses from the user
    private int getNumberOfHorses() {
        int numberOfHorses = 0;
        while (numberOfHorses < 2 || numberOfHorses > 10) {
            String input = JOptionPane.showInputDialog(frame, "Enter the number of horses (between 2 and 10):");
            if (input == null)
                System.exit(0);
            try {
                numberOfHorses = Integer.parseInt(input);
                if (numberOfHorses < 2 || numberOfHorses > 10) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number between 2 and 10.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
            }
        }
        return numberOfHorses;
    }

    // Gets the race length from the user
    private int getRaceLength() {
        int raceLength = 0;
        while (raceLength < 10 || raceLength > 100) {
            String input = JOptionPane.showInputDialog(frame, "Enter the race length (between 10 and 100):");
            if (input == null)
                System.exit(0);
            try {
                raceLength = Integer.parseInt(input);
                if (raceLength < 10 || raceLength > 100) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid length between 10 and 100.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
            }
        }
        return raceLength;
    }

    // Save the player's money to a file
    private void saveMoney() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            writer.println(playerMoney);
            JOptionPane.showMessageDialog(frame, "Money saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to save money.");
        }
    }

    // Load the player's money from a file
    private void loadMoney() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            playerMoney = Double.parseDouble(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            playerMoney = 1000;
        }
    }
}
