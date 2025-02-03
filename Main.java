public class Main {

    private Simulator simulator;

    public static void main(String[] args) {
        Main mainProgram = new Main();  // Create an instance of Main
        mainProgram.start();            // Start the program
    }

    private void start() {
        simulator = new Simulator();  // Initialize simulator
        simulator.runLongSimulation(); // Example usage
    }
}