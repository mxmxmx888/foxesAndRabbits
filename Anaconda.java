import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Anaconda extends Animal
{
    // Characteristics shared by all foxes (class variables).c
    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a fox can live.
    private static final int MAX_AGE = 55;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int CAPYBARA_FOOD_VALUE = 10;

    private static final int HERON_FOOD_VALUE = 8;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The fox's age.
    private int age;
    // The fox's food level, which is increased by eating rabbits.
    private int foodLevel;

    public Anaconda(boolean randomAge, Location location)
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


    public void act(Field currentField, Field nextFieldState)
    {
        //increment hunger and age of ananconda
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
        return "Anaconda{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    //increment age of anaconda
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    //increment hunger of anaconda
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }


    private Location findFood(Field field)
    {
        //tries to find animals to eat in adjacent locations
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            //if animal that was found is capybara then update food level of anaconda with CAPYBARA_FOOD_VALUE and kill capybara
            if(animal instanceof Capybara capybara) {
                if(capybara.isAlive()) {
                    capybara.setDead();
                    foodLevel = foodLevel + CAPYBARA_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            //if animal that was found is heron then update food level of anaconda with HERON_FOOD_VALUE and kill heron
            else if (animal instanceof Heron heron) {
                if (heron.isAlive()){
                    heron.setDead();
                    foodLevel = foodLevel + HERON_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            /**
             * anaconda can "step" on grass and destroy it but not eat
             * it was done so that anaconda doesnt get trapped in the grass
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

        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Anaconda young = new Anaconda(false, loc);
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
