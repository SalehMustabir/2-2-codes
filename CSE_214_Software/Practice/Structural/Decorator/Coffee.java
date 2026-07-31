//abstract Component
abstract class Beverage {
    String description = "Unknown Beverage";

    public String getDescription() {
        return description;
    }

    public abstract double getCost();
}

// Concrete Component
class Espresso extends Beverage {
    public Espresso() {
        description = "Espresso";
    }

    @Override
    public double getCost() {

        return 1.99;
    }

}

class DarkRoast extends Beverage
{
    public DarkRoast()
    {
        description = "DarkRoast";
    }

    @Override
    public double getCost()
    {
        return 0.99;
    }
}

// 3. The Abstract Decorator (Must mirror the Component type)
abstract class CondimentDecorator extends Beverage
{
    //Forcing all concrete condiments to re-implement description adjustments
    public abstract String getDescription();
}

class Mocha extends CondimentDecorator
{
    private Beverage beverage;
    public Mocha(Beverage beverage)
    {
        this.beverage = beverage;
    }
    @Override
    public String getDescription() {
        
        return beverage.getDescription() + "Mocha";
    }
    @Override
    public double getCost() {
        
        return .20 + beverage.getCost();
    }
    
}

class Whip extends CondimentDecorator
{
    private Beverage beverage;
    public Whip(Beverage beverage)
    {
        this.beverage = beverage;
    }

    @Override
    public String getDescription()
    {
        return beverage.getDescription() + "Whip";
    }
    @Override
    public double getCost()
    {
        return 0.10 + beverage.getCost();
    }
}

public class Coffee {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        Beverage espresso = new Espresso();


        System.out.println(espresso.getDescription());
        System.out.println(espresso.getCost());


        espresso = new Mocha(espresso);
        espresso = new Whip(espresso);


        System.out.println(espresso.getDescription());
        System.out.println(espresso.getCost());

    }
}
