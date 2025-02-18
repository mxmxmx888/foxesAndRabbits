import java.util.*;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal/object.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The dimensions of the field.
    private final int depth, width;
    // Animals mapped by location.
    private final Map<Location, Animal> field = new HashMap<>();
    // The animals.
    private final List<Animal> animals = new ArrayList<>();

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param anAnimal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void placeAnimal(Animal anAnimal, Location location)
    {
        assert location != null;
        Object other = field.get(location);
        if(other != null) {
            animals.remove(other);
        }
        field.put(location, anAnimal);
        animals.add(anAnimal);
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Animal getAnimalAt(Location location)
    {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location);
        for(Location next : adjacent) {
            Animal anAnimal = field.get(next);
            if(anAnimal == null) {
                free.add(next);
            }
            else if(!anAnimal.isAlive()) {
                free.add(next);
            }
        }
        return free;
    }
    //get list of animals in adjacent cells
    public List<Animal> getAdjacentAnimals(Location location){
        List<Animal> animals = new ArrayList<>();
        List<Location> locations = new ArrayList<>();
        locations = getAdjacentLocations(location);
        for (int i = 0; i<locations.size(); i++){
            if (getAnimalAt(locations.get(i)) instanceof Animal animal){
                animals.add(animal);
            }
        }
        return animals;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> getAdjacentLocations(Location location)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * prints out number of each type of the acting species
     */
    public void fieldStats()
    {
        int numWolfs= 0, numCapybaras = 0, numTigers = 0, numAnacondas = 0, numGrass = 0, numHerons = 0;
        for(Animal anAnimal : field.values()) {
            if(anAnimal instanceof Wolf wolf) {
                if(wolf.isAlive()) {
                    numWolfs++;
                }
            }
            else if(anAnimal instanceof Capybara capybara) {
                if(capybara.isAlive()) {
                    numCapybaras++;
                }
            }
            else if(anAnimal instanceof Tiger tiger) {
                if(tiger.isAlive()) {
                    numTigers++;
                }
            }
            else if(anAnimal instanceof Anaconda anaconda) {
                if(anaconda.isAlive()) {
                    numAnacondas++;
                }
            }
            else if(anAnimal instanceof Grass grass) {
                if (grass.isAlive()) {
                    numGrass++;
                }
            }
            else if(anAnimal instanceof Heron heron) {
                if (heron.isAlive()) {
                    numHerons++;
                }
            }
        }
        System.out.println(":Capybaras: " + numCapybaras + " Wolfs: " + numWolfs + " Anacondas: " + numAnacondas + " Tigers: " + numTigers + " Grass: " + numGrass + " Herons: " + numHerons )  ;
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Return whether there is at least one rabbit and one fox in the field.
     * @return true if there is at least one rabbit and one fox in the field.
     */
    public boolean isViable()
    {
        boolean capybaraFound = false;
        boolean wolfFound = false;
        boolean condaFound = false;
        boolean heronFound = false;
        boolean grassFound = false;
        boolean tigerFound = false;
        boolean bool = true;
        Iterator<Animal> it = animals.iterator();
        while(it.hasNext() && ! (capybaraFound && wolfFound && condaFound && heronFound && grassFound && tigerFound)) {
            Animal anAnimal = it.next();
            if(anAnimal instanceof Capybara capybara) {
                if(capybara.isAlive()) {
                    capybaraFound = true;
                }
            }
            else if(anAnimal instanceof Wolf wolf) {
                if(wolf.isAlive()) {
                    wolfFound = true;
                }
            }
            else if(anAnimal instanceof Anaconda anaconda) {
                if(anaconda.isAlive()) {
                    condaFound = true;
                }
            }
            else if(anAnimal instanceof Heron heron) {
                if(heron.isAlive()) {
                    heronFound = true;
                }
            }
            else if(anAnimal instanceof Grass grass) {
                if(grass.isAlive()) {
                    grassFound = true;
                }
            }
            else if(anAnimal instanceof Tiger tiger) {
                if(tiger.isAlive()) {
                    tigerFound = true;
                }
            }
        }
        return capybaraFound && wolfFound && condaFound && heronFound && tigerFound;
    }
    
    /**
     * Get the list of animals.
     */
    public List<Animal> getAnimals()
    {
        return animals;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
