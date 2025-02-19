import java.util.List;
import java.util.Iterator;
import java.util.Random;


public class Wolf extends Animal
{

    // The age at which a wolf can start to breed
    private static final int BREEDING_AGE = 12;

    // The age to which a wolf can live
    private static final int MAX_AGE = 75;

    // the likelihood of a wolf breeding
    private static final double BREEDING_PROBABILITY = 0.27;

    // the maximum number of births
    private static final int MAX_LITTER_SIZE = 3;

    // the food value of a single capybara
    private static final int CAPYBARA_FOOD_VALUE = 12;

    //food value of a single heron
    private static final int HERON_FOOD_VALUE = 8;

    // a shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields)

    // wolf's age
    private int age;
    // The fox's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = rand.nextInt(CAPYBARA_FOOD_VALUE);
    }
    
    /**
     * This is what the wolf does most of the time: it hunts for
     * capybaras and herons. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        //increment hunger and age of the wolf as it grows
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
            // Move towards a source of food if found.]
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
                // Overcrowding.
                setDead();
            }
        }
    }



    @Override
    public String toString() {
        return "Wolf{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }


    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    

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
            //if capybara is found by wolf increase its food level by CAPYBARA_FOOD_VALUE
            if(animal instanceof Capybara capybara) {
                if(capybara.isAlive()) {
                    capybara.setDead();
                    foodLevel = foodLevel + CAPYBARA_FOOD_VALUE;
                    foodLocation = loc;
                }
                //if heron is found by wolf increase its food evel by HERON_FOOD_VALUE
            }else if (animal instanceof Heron heron){
                if(heron.isAlive()) {
                    heron.setDead();
                    foodLevel = foodLevel + HERON_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            /**
             * wolf can step on grass and destroy it but not it
             * it was done so that animals dont get trapped by
             * grass when it surrounds them
             */
            else if (animal instanceof Grass grass)
            {
                if(grass.isAlive()){
                    grass.setDead();

                }
            }
        }
        return foodLocation;
    }
    

    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // new wolfs are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Wolf young = new Wolf(false, loc);
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


    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
