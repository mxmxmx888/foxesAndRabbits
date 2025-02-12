import java.util.*;

/**
 * A simple predator-prey simulator, based on a rectangular field containing 
 * rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    private static final double WOLF_CREATION_PROBABILITY = 0.04;
    // The probability that a rabbit will be created in any given position.
    private static final double CAPYBARA_CREATION_PROBABILITY = 0.1;
    // probability that tiger will be created
    private static final double TIGER_CREATION_PROBABILITY = 0.06;

    private static final double ANACONDA_CREATION_PROBABILITY = 0.03;

    private static final double HERON_CREATION_PROBABILITY = 0.05;

    private static final double GRASS_CREATION_PROBABILITY = 0.2;

    private static int hour;

    private boolean isNight;

    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);

        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(700);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(50);         // adjust this to change execution speed
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Animal> animals = field.getAnimals();
        for (Animal anAnimal : animals) {
            anAnimal.act(field, nextFieldState);
        }
        hour = (hour + 1) % 24;

        if (hour>= 18 || hour < 6) {
            view.setNight(true);
            isNight = true;
        }
        else {
            view.setNight(false);
            isNight = false;
        }

        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field);
    }

    public static boolean isNight() {
        return hour >= 18 || hour < 6;
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++)
            {
                if (col%2==0) {
                    if (rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
                        Location location = new Location(row, col);
                        Wolf wolf = new Wolf(true, location);
                        field.placeAnimal(wolf, location);
                    } else if (rand.nextDouble() <= TIGER_CREATION_PROBABILITY) {
                        Location location = new Location(row, col);
                        int random = rand.nextInt(2);
                        Tiger tiger = new Tiger(true,location);
                        field.placeAnimal(tiger, location);
                    }
                    else if (rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                        Location location = new Location(row, col);
                        int random = rand.nextInt(2);
                        Grass grass = new Grass(location);
                        field.placeAnimal(grass, location);
                    }

                }
                else{
                    if (rand.nextDouble() <= CAPYBARA_CREATION_PROBABILITY) {
                        Location location = new Location(row, col);
                        Capybara capybara = new Capybara(true, location);
                        field.placeAnimal(capybara, location);
                    }

                    else if(rand.nextDouble() <= ANACONDA_CREATION_PROBABILITY) {
                        Location location = new Location(row, col);
                        Anaconda anaconda = new Anaconda(true, location);
                        field.placeAnimal(anaconda, location);
                    }
                    else if(rand.nextDouble() <= HERON_CREATION_PROBABILITY) {
                        Location location = new Location(row, col);
                        Heron heron = new Heron(true, location);
                        field.placeAnimal(heron, location);
                    }
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Report on the number of each type of animal in the field.
     */
    public void reportStats()
    {
        //System.out.print("Step: " + step + " ");
        field.fieldStats();
    }
    
    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }
}
