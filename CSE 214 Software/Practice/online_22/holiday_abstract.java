// Product
class HolidayPackage {
    private String flight;
    private String hotel;
    private String dailyActivity;

    public HolidayPackage(String flight, String hotel, String dailyActivity) {
        this.flight = flight;
        this.hotel = hotel;
        this.dailyActivity = dailyActivity;
    }

    @Override
    public String toString() {
        return "HolidayPackage [flight=" + flight +
                ", hotel=" + hotel +
                ", dailyActivity=" + dailyActivity + "]";
    }
}

// Abstract Factory
interface HolidayPackageFactory {
    HolidayPackage createHolidayPackage();
}

// Concrete Factory 1
class RelaxationPackageFactory implements HolidayPackageFactory {

    @Override
    public HolidayPackage createHolidayPackage() {
        return new HolidayPackage(
                "Business Class Flight",
                "5-Star Resort",
                "Spa Treatment"
        );
    }
}

// Concrete Factory 2
class AdventurePackageFactory implements HolidayPackageFactory {

    @Override
    public HolidayPackage createHolidayPackage() {
        return new HolidayPackage(
                "Economy Class Flight",
                "Mountain Cabin",
                "Hiking Tour"
        );
    }
}

// Client
public class AbstractFactoryDemo {

    public static void main(String[] args) {

        HolidayPackageFactory factory1 = new RelaxationPackageFactory();
        HolidayPackage relaxation = factory1.createHolidayPackage();
        System.out.println(relaxation);

        HolidayPackageFactory factory2 = new AdventurePackageFactory();
        HolidayPackage adventure = factory2.createHolidayPackage();
        System.out.println(adventure);
    }
}