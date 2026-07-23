interface Transport {
    void deliver();

}

class Ship implements Transport {
    public void deliver() {
        System.out.println("Delivered by Sea");
    }
}

class Truck implements Transport {
    public void deliver() {
        System.out.println("Delivered by road");
    }
}

class Transport_Factory {
    public static Transport geTransport(String mode) {

        if (mode.equalsIgnoreCase("sea")) {
            return new Ship();
        } else if (mode.equalsIgnoreCase("road")) {
            return new Truck();
        } else {
            return null;
        }
    }
}

public class Factory {
    public static void main(String[] args) {
        System.out.println("gg");
        String mode1 = "sea";
        String mode2 = "road";        
        Transport t1 = Transport_Factory.geTransport(mode1);
        Transport t2 = Transport_Factory.geTransport(mode2);
        t1.deliver();        
        t2.deliver();


    }
}
