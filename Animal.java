import java.util.List;
import java.util.Random;

/**
 * Common elements of the acting species.
 *
 * @author Maksym Byelko and Tajim Hasan
 * @version 7.0
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;
    // Whether the animal is infected or not.
    private boolean infected;
    // Used for random generator for disease
    private static Random rand = new Random();

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
        this.infected = false;
    }

    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }

    /**
     * checks whether the animal is infected or not.
     */
    public boolean isInfected(){
        return infected;
    }

    /**
     * infects the animal
     */
    public void infect() {
        infected = true;
    }

    /**
     * this is risk of death from disease
     * there's a 10% chance that the animal will die
     */
    public void riskDisease(){
        if (infected) {
            if (rand.nextDouble() < 0.1) {

                setDead();
            }
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }

    /**
     *this is used to spread disease to nearby animals
     */
    public void spreadDisease(Field currentField){
        if (infected) {
            List<Location> adjacentLocations = currentField.getAdjacentLocations(getLocation());
            for (Location loc : adjacentLocations) {
                Animal nearby = currentField.getAnimalAt(loc);
                if (nearby != null && !nearby.isInfected() && rand.nextDouble() < 0.2) { // 20% chance to spread
                    nearby.infect();
                }
            }
        }
    }
}
