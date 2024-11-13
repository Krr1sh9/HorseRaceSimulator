/**
 * The Horse class represents a horse in the race, containing properties 
 * such as its name, symbol, confidence level, and distance traveled. 
 * It also includes behaviors for moving, falling, and other horse-related actions.
 * 
 * @author Krrish Shah
 * @version 1.0
 */
public class Horse {
    private String name;
    private char symbol;  // This is the "piece" or representation of the horse on the track
    private double confidence;  // The horse's confidence level that determines its performance
    private int distanceTravelled;  // The distance the horse has covered in the race
    private boolean fallen;  // Whether the horse has fallen
    private String breed;  // The breed of the horse
    private String coatColour;  // The coat colour of the horse
    private String equipment;  // The equipment (e.g., saddle, bridle) the horse is wearing

    /**
     * Constructor for the Horse class. 
     * Initializes a new horse with a name, symbol, and confidence.
     * Sets the breed, coat colour, and equipment to default values.
     *
     * @param name the name of the horse
     * @param symbol the symbol representing the horse on the race track
     * @param confidence the initial confidence level of the horse
     */
    public Horse(String name, char symbol, double confidence) {
        this.name = name;
        this.symbol = symbol;
        this.confidence = confidence;
        this.distanceTravelled = 0;  // Horses start at the beginning
        this.fallen = false;  // Horses are not fallen at the start
        this.breed = "Unknown";  // Default breed value
        this.coatColour = "Unknown";  // Default coat colour
        this.equipment = "None";  // Default equipment
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

    /**
     * Moves the horse forward by 1 unit.
     */
    public void moveForward() {
        distanceTravelled++;
    }

    /**
     * Simulates the horse falling. Once fallen, the horse cannot move forward.
     */
    public void fall() {
        fallen = true;
    }

    /**
     * Resets the horse's position and state to the start line (distance = 0).
     */
    public void goBackToStart() {
        distanceTravelled = 0;
        fallen = false;
    }
}
