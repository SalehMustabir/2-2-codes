// Abstract class
abstract class Game {

    // Template method
    public final void play() {
        initialize();
        start();
        end();
    }

    abstract void initialize();

    abstract void start();

    abstract void end();
}

// Concrete class 1
class Cricket extends Game {

    void initialize() {
        System.out.println("Cricket Game Initialized");
    }

    void start() {
        System.out.println("Cricket Game Started");
    }

    void end() {
        System.out.println("Cricket Game Finished");
    }
}

// Concrete class 2
class Football extends Game {

    void initialize() {
        System.out.println("Football Game Initialized");
    }

    void start() {
        System.out.println("Football Game Started");
    }

    void end() {
        System.out.println("Football Game Finished");
    }
}

// Main class
public class Main {
    public static void main(String[] args) {

        Game game = new Cricket();
        game.play();

        System.out.println();

        game = new Football();
        game.play();
    }
}