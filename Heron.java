import java.util.List;
import java.util.Random;

public class Heron extends Animal{
    // Characteristics shared by all herons (class variables).
    // The age at which a heron can start to breed.
    private static final int BREEDING_AGE = 8;

    // The age to which a heron can live.
    private static final int MAX_AGE = 22;

    // The likelihood of a heron breeding.
    private static final double BREEDING_PROBABILITY = 0.17;

    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The heron's age.
    private int age;

    /**
     * Create a new heron. A heron may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the heron will have a random age.
     * @param location The location within the field.
     */
    public Heron(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }

    /**
     * This is what the heron does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * can also have disease and risk dying or infect to other animals
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        riskDisease();
        if (!isAlive()) {
            return;
        }
        spreadDisease(currentField);

        boolean isNight = Simulator.isNight();
        // At night, only act with a 10% chance.
        if (isNight && rand.nextDouble() >= 0.1) {
            // 90% of the time at night do nothing
            nextFieldState.placeAnimal(this, getLocation());
            return;
        }

        // Normal behavior (daytime or the 10% chance at night)
        List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());

        // Attempt to breed if there is free space.
        if (!freeLocations.isEmpty()) {
            giveBirth(nextFieldState, freeLocations);
        }

        // Try to move into a free location.
        if (!freeLocations.isEmpty()) {
            Location nextLocation = freeLocations.get(0);
            setLocation(nextLocation);
            nextFieldState.placeAnimal(this, nextLocation);
        }
        else {
            // Overcrowding: if no free location, mark as dead.
            setDead();
        }
        }


    @Override
    public String toString() {
        return "Heron{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the heron's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this heron is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New herons are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Heron young = new Heron(false, loc);
                nextFieldState.placeAnimal(young, loc);
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
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A heron can breed if it has reached the breeding age.
     * @return true if the heron can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
