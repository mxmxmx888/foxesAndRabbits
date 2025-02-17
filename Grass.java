import java.util.List;
import java.util.Random;

public class Grass extends Animal
{
    // characteristics shared by all instances of Grass.

    // the age at which a grass can start to spread
    private static final int BREEDING_AGE = 8;

    // the likelihood of grass spreading
    private static final double BREEDING_PROBABILITY = 0.6;

    // the maximum number of "births" grass can give
    private static final int MAX_LITTER_SIZE = 2;

    // a shared random number generator to control breeding
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields)
    private int age;


    public Grass(Location location)
    {
        super(location);


    }

    /**
     * method is responsible for the way grass act
     * basically, it just grows and spreads
     */
    public void act(Field currentField, Field nextFieldState)
    {
            //
            if(isAlive()) {
                List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
                if (rand.nextDouble() <= BREEDING_PROBABILITY) {
                    // grass only spreads, it doesn't move
                    giveBirth(nextFieldState, freeLocations);
                }

                // ensure that grass stays in the same place
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
     * Check whether or not this rabbit is to give birth at this step
     * New births will be made into free adjacent locations
     * @param freeLocations The locations that are free in the current field
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // new grass grows in adjacent locations
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
     * Generate a number representing the number of births
     * if it can breed
     * @return The number of births (may be zero)
     */
    private int breed()
    {
        int births;
        births = rand.nextInt(MAX_LITTER_SIZE) + 1;

        return births;
    }

    /**
     * a grass can spread if it has reached the breeding age
     * @return true if the grass can spread, false otherwise
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * grass cant move, so do nothing
     */
    @Override
    protected void setLocation(Location location) {

    }
}
