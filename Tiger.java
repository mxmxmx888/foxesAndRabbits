import java.util.Random;

public class Tiger extends Animal
{
    private static final int BREEDING_AGE = 15;
    private static final int MAX_AGE = 200;
    private static final double BREEDING_PROBABILITY = 0.08;
    private static final int MAX_LITTER_SIZE = 2;
    private static final int RABBIT_FOOD_VALUE = 9;
    private static final Random rand = Randomizer.getRandom();

    public Tiger(boolean randomAge, Location location) {
        super(location);
    }

    @Override
    public void act(Field currentField, Field nextFieldState) {

    }
}
