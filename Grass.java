import java.util.List;
import java.util.Random;

public class Grass extends Animal
{
    // Characteristics shared by all rabbits (class variables).
    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 8;
    // The age to which a rabbit can live.
    private static final int MAX_AGE = 20;

    // The likelihood of a rabbit breeding.
    private static final double BREEDING_PROBABILITY = 0.6;

    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    private int age;


    public Grass(Location location)
    {
        super(location);
        age = MAX_AGE;

    }

    /**
     * This is what the rabbit does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {

            if(isAlive()) {
                List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
                if (rand.nextDouble() <= BREEDING_PROBABILITY) {
                    // Grass only spreads, it doesn't move
                    giveBirth(nextFieldState, freeLocations);
                }

                // Ensure Grass stays in the same place
                nextFieldState.placeAnimal(this, getLocation());
            }

    }

    @Override
    public String toString() {
        return "Grass{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the rabbit's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Grass newgrass = new Grass(loc);
                nextFieldState.placeAnimal(newgrass, loc);
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births;
        births = rand.nextInt(MAX_LITTER_SIZE) + 1;

        return births;
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    @Override
    protected void setLocation(Location location) {
        // Grass cannot move, so do nothing
    }
}
