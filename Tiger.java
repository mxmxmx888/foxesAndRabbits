import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Tiger extends Animal
{
    private static final int BREEDING_AGE = 18;
    private static final int MAX_AGE = 85;
    private static final double BREEDING_PROBABILITY = 0.65;
    private static final int MAX_LITTER_SIZE = 4;
    private static final int CAPYBARA_FOOD_VALUE = 4;
    private static final int WOLF_FOOD_VALUE = 10;
    private static final Random rand = Randomizer.getRandom();
    private int age, foodLevel;
    private final boolean male;

    public Tiger(boolean randomAge, Location location) {
        super(location);
        int random = rand.nextInt(2);
        male = random != 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = rand.nextInt(WOLF_FOOD_VALUE);
    }

    @Override
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        riskDisease();
        if (!isAlive()) {
            return;
        }
        spreadDisease(currentField);
        if(isAlive()) {
            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty()) {
                giveBirth(currentField , nextFieldState, freeLocations);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField);
            if(nextLocation == null && ! freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }
            // See if it was possible to move.
            if(nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    public String toString() {
        return "Tiger{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    /**
     * Increase the age. This could result in the fox's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof Capybara capybara) {
                if(capybara.isAlive()) {
                    capybara.setDead();
                    foodLevel = foodLevel + CAPYBARA_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            else if (animal instanceof Wolf wolf)
            {
                if(wolf.isAlive()){
                    wolf.setDead();
                    foodLevel = foodLevel + WOLF_FOOD_VALUE;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check whether this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        List<Animal> tigers = new ArrayList<>();
        tigers = currentField.getAdjacentAnimals(this.getLocation());
        for (int i=0; i< tigers.size(); i++)
        {
            if(tigers.get(i) instanceof Tiger tiger && tiger.isMale() && !this.isMale())
            {
                int births = breed();
                if(births > 0) {
                    for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                        Location loc = freeLocations.remove(0);
                        Tiger young = new Tiger(false, loc);
                        nextFieldState.placeAnimal(young, loc);


                    }
                }

            }
        }
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.

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
     * A fox can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    private boolean isMale() {
    return male;
    }
}
