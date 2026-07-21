/**
 * InnerBuilder
 */
interface BicycleBuilder {    
    void buildFrame();
    void addGears();
    void addTyres();
    Bicycle getBicycle();
}

class Bicycle
{
    private String frame;
    private String gears;
    private String tyres;
    public void setFrame(String frame) {
        this.frame = frame;
    }
    public void setGears(String gears) {
        this.gears = gears;
    }
    public void setTyres(String tyres) {
        this.tyres = tyres;
    }
    @Override
    public String toString() {
        return "Bicycle [frame=" + frame + ", gears=" + gears + ", tyres=" + tyres + "]";
    }
    
}

class CommuterBuilder implements BicycleBuilder
{
    private Bicycle bike = new Bicycle();

    public void buildFrame()
    {
        bike.setFrame("aluminium");
    }
    public void addGears()
    {
        bike.setGears("single speed gear");
    }
    public void addTyres()
    {
        bike.setTyres("Road tiers");
    }
    public Bicycle getBicycle()
    {
        return bike;
    }
}

class MountainBeast implements BicycleBuilder
{
private Bicycle bike = new Bicycle();

    public void buildFrame()
    {
        bike.setFrame("Carbon Fiber");
    }
    public void addGears()
    {
        bike.setGears("12-speed Gear");
    }
    public void addTyres()
    {
        bike.setTyres("Off road grip tires");
    }
    public Bicycle getBicycle()
    {
        return bike;
    }
}

class Director
{
    public void build(BicycleBuilder builder)
    {
        builder.buildFrame();
        builder.addGears();
        builder.addTyres();
        
    }
}

public class Builder {    
    public static void main(String[] args) {
        //BicycleBuilder c_bike = new CommuterBuilder();
        BicycleBuilder m_bike = new MountainBeast();
        Director director = new Director();
        director.build(m_bike);
        Bicycle mountain_bike = m_bike.getBicycle();
        System.out.println(mountain_bike);
    }
}
