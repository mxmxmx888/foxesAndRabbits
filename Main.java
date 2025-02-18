public class Main {

    private Simulator simulator;

    public static void main(String[] args) {
        Main mainProgram = new Main();
        mainProgram.start();
    }

    private void start() {
        simulator = new Simulator();
        simulator.runLongSimulation();
    }
}