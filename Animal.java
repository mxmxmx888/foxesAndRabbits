import java.util.List;
import java.util.Random;

/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;

    private boolean infected;
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

    public boolean isInfected(){
        return infected;
    }

    public void infect() {
        infected = true;
    }

    public void riskDisease(){
        if (infected) {
            if (rand.nextDouble() < 0.25) {

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
