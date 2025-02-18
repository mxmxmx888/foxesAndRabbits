import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Tiger extends Animal
{
    // age  at which tiger can start breeding
    private static final int BREEDING_AGE = 12;
    //maximum age of the tiger
    private static final int MAX_AGE = 65;

    // breeding probability
    private static final double BREEDING_PROBABILITY = 0.3;

    // maximum number of births
    private static final int MAX_LITTER_SIZE = 4;

    //food value of a single capybara
    private static final int CAPYBARA_FOOD_VALUE = 12;

    // food value of a single wolf
    private static final int WOLF_FOOD_VALUE = 20;

    // food value of a single heron
    private static final int HERON_FOOD_VALUE = 8;


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
        //increment age and hunger of the tiger
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

    public String toString() {
        return "Tiger{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    //increment age of the tiger
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    //increment hunger of the tiger
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
            else if (animal instanceof Heron heron)
            {
                if(heron.isAlive()){
                    heron.setDead();
                    foodLevel = foodLevel + HERON_FOOD_VALUE;
                }
            }
            else if (animal instanceof Grass grass)
            {
                if(grass.isAlive()){
                    grass.setDead();

                }
            }
        }
        return foodLocation;
    }


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

    //return true if tiger is male and false if not
    private boolean isMale()
    {
    return male;
    }
}
