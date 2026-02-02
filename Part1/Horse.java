public class Horse {

    // Instance variables
    // Basic horse details
    private String name; // Horse name shown in the GUI and race output
    private char symbol; // This is the "piece" or representation of the horse on the track

    // These control how the horse performs in the race
    private double confidence; // Higher confidence means more likely to move forward
    private int distanceTravelled; // How far the horse has moved along the track
    private boolean fallen; // Whether the horse has fallen

    // Customisation options (don't affect performance)
    private String breed;
    private String coatColour;
    private String equipment;

    // Create a new horse with a name, a symbol, and a confidence value
    public Horse(String name, char symbol, double confidence) {
        this.name = name;
        this.symbol = symbol;
        this.confidence = confidence;

        // Set default values for the start of the race
        this.distanceTravelled = 0;
        this.fallen = false;

        // Set default values for the customisation options
        this.breed = "Unknown";
        this.coatColour = "Unknown";
        this.equipment = "None";
    }

    // Getter and Setter methods for various attributes

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public int getDistanceTravelled() {
        return distanceTravelled;
    }

    public boolean hasFallen() {
        return fallen;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getCoatColour() {
        return coatColour;
    }

    public void setCoatColour(String coatColour) {
        this.coatColour = coatColour;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    // Moves the horse forward by 1 unit
    public void moveForward() {
        distanceTravelled++;
    }

    // Make the horse fall (once fallen it can no longer move)
    public void fall() {
        fallen = true;
    }

    // Reset horse so a new race can start from the beginning
    public void goBackToStart() {
        distanceTravelled = 0;
        fallen = false;
    }
}
