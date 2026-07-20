interface PackageBuilder {
    void buildFlight();

    void buildHotel();

    void buildDailyActivity();

    HolidayPackage getHolidayPackage();
}

class HolidayPackage {
    private String flight;
    private String hotel;
    private String dailyActity;

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public void setDailyActity(String dailyActity) {
        this.dailyActity = dailyActity;
    }

    @Override
    public String toString() {
        return "HolidayPackage [flight=" + flight + ", hotel=" + hotel + ", dailyActity=" + dailyActity + "]";
    }

}

class RelaxationPackage implements PackageBuilder {
    private HolidayPackage pack = new HolidayPackage();

    @Override
    public void buildDailyActivity() {

        pack.setDailyActity("spa treatment");

    }

    @Override
    public void buildFlight() {

        pack.setFlight("Business class flight");

    }

    @Override
    public void buildHotel() {

        pack.setHotel("5-Star Resort");

    }

    @Override
    public HolidayPackage getHolidayPackage() {

        return pack;
    }

}
class AdventurePackage implements PackageBuilder {
    private HolidayPackage pack = new HolidayPackage();

    @Override
    public void buildDailyActivity() {

        pack.setDailyActity("Hiking Tour");

    }

    @Override
    public void buildFlight() {

        pack.setFlight("Economy class flight");

    }

    @Override
    public void buildHotel() {

        pack.setHotel("Mountain Cabin");

    }

    @Override
    public HolidayPackage getHolidayPackage() {

        return pack;
    }

}
class Director
{
    static void build(PackageBuilder builder)
    {
        builder.buildFlight();
        builder.buildDailyActivity();
        builder.buildHotel();
    }
}

public class Builder {
    public static void main(String[] args) {
        PackageBuilder p1 = new RelaxationPackage();
        Director.build(p1);
        HolidayPackage r_pack = p1.getHolidayPackage();
        System.out.println(r_pack);


    }
}
/* 
A
B
A obj = new B
 */
