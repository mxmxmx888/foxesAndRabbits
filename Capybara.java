import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class Capybara extends Animal
{
    // characteristics shared by all capybaras (class variables).
    // the age at which a capybaras can start to breed.
    private static final int BREEDING_AGE = 3;
    
    // the age to which a capybara can live.
    private static final int MAX_AGE = 40;
    
    // the likelihood of a capybara breeding.
    private static final double BREEDING_PROBABILITY = 0.44;
    
    // the maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;

    //food value of a single grass location
    private static final int GRASS_FOOD_VALUE = 20;
    
    //a shared random number generator to control breeding
    private static final Random rand = Randomizer.getRandom();
    

    
    // The capybara's age.
    private int age;

    //capybara's food level
    private int foodLevel;


    public Capybara(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = rand.nextInt(GRASS_FOOD_VALUE);
    }
    
    /**
     * This is what the capybara does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        //increment age and hunger of capybara
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
                giveBirth(nextFieldState, freeLocations);
            }
            // Move towards a source of food if found
            Location nextLocation = findFood(currentField);
            if(nextLocation == null && ! freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }
            // See if it was possible to move
            if(nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
                // Overcrowding
                setDead();
            }
        }
    }

    @Override
    public String toString() {
        return "Rabbit{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    //increasing
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this capybara is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New capybaras are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Capybara young = new Capybara(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
        

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

    // checks whether capybara can breed or not
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    //increments hunger
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof Grass grass) {
                if(grass.isAlive()) {
                    grass.setDead();
                    foodLevel = foodLevel + GRASS_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
}
