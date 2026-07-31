//package CSE_214_Software.Practice.Structural.Adapter;

interface Duck {
    void quack();

    void fly();
}

interface Turkey {
    void gobble(); // Ducks don't gobble!

    void flyShortDistance(); // Ducks fly normally!
}

class MallardDuck implements Duck {
    @Override
    public void quack() {
        System.out.println("Quack!");
    }

    @Override
    public void fly() {
        System.out.println("I'm flying!");
    }
}

class WildTurkey implements Turkey {
    @Override
    public void gobble() {
        System.out.println("Gobble gobble");
    }

    @Override
    public void flyShortDistance() {
        System.out.println("I'm flying a short distance!");
    }
}

class TurkeyAdapter implements Duck {
    Turkey turkey;

    public TurkeyAdapter(Turkey turkey) {
        this.turkey = turkey;
    }

    @Override
    public void quack() {
        turkey.gobble();
    }

    @Override
    public void fly() {
        for (int i = 0; i < 5; i++) {
            turkey.flyShortDistance();
        }
    }
}

public class Adapter {
    public static void main(String[] args) {
        MallardDuck duck = new MallardDuck();
        WildTurkey turkey = new WildTurkey();
        Duck turkeyAdapter = new TurkeyAdapter(turkey);
        turkeyAdapter.fly();
        turkeyAdapter.quack();
        
    }
}
