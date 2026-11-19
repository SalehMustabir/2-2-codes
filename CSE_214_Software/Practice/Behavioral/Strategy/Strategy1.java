// ==========================================
// 1. BEHAVIOR INTERFACES (Strategy Interfaces)
// ==========================================

interface FlyBehavior {
    void fly();
}

interface QuackBehavior {
    void quack();
}

// ==========================================
// 2. CONCRETE FLY BEHAVIORS
// ==========================================

class FlyWithWings implements FlyBehavior {
    @Override
    public void fly() {
        System.out.println("I'm flying!!");
    }
}

class FlyNoWay implements FlyBehavior {
    @Override
    public void fly() {
        System.out.println("I can't fly");
    }
}

// ==========================================
// 3. CONCRETE QUACK BEHAVIORS
// ==========================================

class Quack implements QuackBehavior {
    @Override
    public void quack() {
        System.out.println("Quack");
    }
}

class Squeak implements QuackBehavior {
    @Override
    public void quack() {
        System.out.println("Squeak");
    }
}

class MuteQuack implements QuackBehavior {
    @Override
    public void quack() {
        System.out.println("<< Silence >>");
    }
}

// ==========================================
// 4. ABSTRACT CONTEXT (Duck)
// ==========================================

abstract class Duck {
    // Reference variables for the behavior interface types
    protected FlyBehavior flyBehavior;
    protected QuackBehavior quackBehavior;

    public Duck(FlyBehavior flyBehavior, QuackBehavior quackBehavior) {
        this.flyBehavior = flyBehavior;
        this.quackBehavior = quackBehavior;
    }

    public abstract void display();

    // Delegate to the behavior classes
    public void performFly() {
        flyBehavior.fly();
    }

    public void performQuack() {
        quackBehavior.quack();
    }

    public void swim() {
        System.out.println("All ducks float, even decoys!");
    }

    // Optional setters to change behavior dynamically at runtime
    public void setFlyBehavior(FlyBehavior fb) {
        this.flyBehavior = fb;
    }

    public void setQuackBehavior(QuackBehavior qb) {
        this.quackBehavior = qb;
    }
}

// ==========================================
// 5. CONCRETE DUCK SUBCLASSES
// ==========================================

class MallardDuck extends Duck {
    public MallardDuck(FlyBehavior flyBehavior, QuackBehavior quackBehavior) {
        super(flyBehavior, quackBehavior);
    }

    public MallardDuck() {
        super(new FlyWithWings(), new Quack());
    }

    @Override
    public void display() {
        System.out.println("I'm a real Mallard duck");
    }
}

class RubberDuck extends Duck {
    public RubberDuck(FlyBehavior flyBehavior, QuackBehavior quackBehavior) {
        super(flyBehavior, quackBehavior);
    }

    public RubberDuck() {
        super(new FlyNoWay(), new Squeak());
    }

    @Override
    public void display() {
        System.out.println("I'm a rubber duckie");
    }
}

class DecoyDuck extends Duck {
    public DecoyDuck(FlyBehavior flyBehavior, QuackBehavior quackBehavior) {
        super(flyBehavior, quackBehavior);
    }

    public DecoyDuck() {
        super(new FlyNoWay(), new MuteQuack());
    }

    @Override
    public void display() {
        System.out.println("I'm a duck decoy");
    }
}

// ==========================================
// 6. CLIENT / MAIN EXECUTION
// ==========================================

public class Strategy1 {
    public static void main(String[] args) {
        Duck mallard = new MallardDuck(new FlyWithWings(), new Quack());
        mallard.display();
        mallard.performQuack();
        mallard.performFly();
        mallard.swim();

        System.out.println();

        Duck rubberDuck = new RubberDuck(new FlyNoWay(), new Squeak());
        rubberDuck.display();
        rubberDuck.performQuack();
        rubberDuck.performFly();

        System.out.println();

        Duck decoy = new DecoyDuck(new FlyNoWay(), new MuteQuack());
        decoy.display();
        decoy.performQuack();
        decoy.performFly();

        // Dynamically changing behavior at runtime
        System.out.println("\n--- Giving Decoy Duck wings at runtime ---");
        decoy.setFlyBehavior(new FlyWithWings());
        decoy.performFly();
    }
}