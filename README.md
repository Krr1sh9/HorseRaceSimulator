# HorseRaceSimulator

This is a graphical Horse Race Simulator. The program utilises Java and Java Swing to provide a range of features and a visualisation of the race in real-time. This program allows the players to customise their own personalised horses, with options such as their name and breed. The player can also save and load virtual money, which the player can bet on a horse before each race.

# Features

Real-Time Race Visualisation: Java Swing provides a real-time horse race simulation GUI.

Horse Customisation: Set each horse a name, and symbol that will appear in the race, breed, coat colour, and equipment.

Betting: Place a bet on a horse using virtual money, and if that horse wins the following race, the user earns double the money they had bet.

Save and Load: The player's balance is initially £1000 when starting a new game. The user has the option to save their balance and load it, allowing the player to progress.

User-friendly GUI: The GUI is simple with buttons and drop-down lists for horse customisation, betting, saving/loading, and starting the race.

# Classes
  ## Part1
  ### Horse.java:
  Contains instance variables
  name: Name of the horse.
  symbol: Icon representing the horse in the race.
  confidence: Likelihood of advancing without falling.
  breed, coatColour, and equipment: Customizable features.

  ### Race.java:
  The Race class contains methods for starting the race (startRace) and moving the horses through the race (moveHorse). The methods for visualising the race in real-time are also here, printRace, printLane, and addHorse.

  ### startRaceGUI.java:
  Contains the main method, which starts the GUI using "SwingUtilities.invokeLater".
  
  ## Part2
  ### GUI.java:
  Contains the user interface with buttons and drop-down lists for horse customisation, betting, saving/loading, and starting the race. The result of the race and whether the bet was won are also displayed.

# Installation

1) Download and install "Visual Studio Code" or any suitable IDE on your device.
2) Download and install "Java JDK" on your device.
3) Download the zip file from this GitHub Repository and unzip its contents in a folder of your choice.
4) Open its ".java" contents within both the "Part1" and "Part2" folders in the IDE and run each file.
5) Run the main method within "startRaceGUI.java" to play.

# How to Play

After running the main method, a pop-up window should appear asking for you to input the number of horses followed by another pop-up window asking to input the race length. Now a GUI window should open with all the horse customisation options, betting options, saving/loading buttons, and a start race button. Now you can personalise your horses, then start a race with or without placing a bet. With the balance you may win, you can save it so you can return to this Horse Race Simulation and load the same balance.

