import java.util.*;

interface SmartDevice {
    void activate();

    void deactivate();

    double getPowerUsage();

    String getStatus();

    // Allows decorators to unwrap and find the original base class SmartDevice
    // getBaseDevice();
    SmartDevice getBaseDevice();

}

interface CompositeDevice extends SmartDevice {
    List<SmartDevice> getChildren();

    String getName();
}

class SmartLight implements SmartDevice {
    private boolean on = false;

    @Override
    public void activate() {

        on = true;
    }

    @Override
    public void deactivate() {

        on = false;
    }

    @Override
    public SmartDevice getBaseDevice() {

        return this;

    }

    @Override
    public double getPowerUsage() {

        return on ? 10.0 : 0.0;
    }

    @Override
    public String getStatus() {

        return "Light: " + (on ? "ON" : "OFF");
    }

}

class SmartThermostat implements SmartDevice {
    private boolean on = false;

    @Override
    public void activate() {

        on = true;
    }

    @Override
    public void deactivate() {

        on = false;
    }

    @Override
    public SmartDevice getBaseDevice() {

        return this;

    }

    @Override
    public double getPowerUsage() {

        return on ? 150.0 : 0.0;
    }

    @Override
    public String getStatus() {

        return "Thermostat: " + (on ? "ON" : "OFF");
    }

}

class SmartSpeaker implements SmartDevice {
    private boolean on = false;

    @Override
    public void activate() {

        on = true;
    }

    @Override
    public void deactivate() {

        on = false;
    }

    @Override
    public SmartDevice getBaseDevice() {

        return this;

    }

    @Override
    public double getPowerUsage() {

        return on ? 5.0 : 0.0;
    }

    @Override
    public String getStatus() {

        return "Thermostat: " + (on ? "ON" : "OFF");
    }
}

class Room implements CompositeDevice {
    private String name;
    private List<SmartDevice> devices = new ArrayList<>();

    public Room(String name) {
        this.name = name;
    }

    public void addDevice(SmartDevice device) {
        devices.add(device);
    }

    @Override
    public List<SmartDevice> getChildren() {
        return devices;
        // return null;
    }

    @Override
    public String getName() {
        return name;

    }

    @Override
    public void activate() {
        for (SmartDevice d : devices)
            d.activate();
    }

    @Override
    public void deactivate() {
        for (SmartDevice d : devices)
            d.deactivate();
    }

    @Override
    public double getPowerUsage() {

        double total = 0;
        for (SmartDevice d : devices)
            total += d.getPowerUsage();
        return total;
    }

    @Override
    public SmartDevice getBaseDevice() {

        return this;
    }

    @Override
    public String getStatus() {

        StringBuilder sb = new StringBuilder("[" + name + "]");
        for (SmartDevice d : devices) {
            sb.append("\n  ").append(d.getStatus().replace("\n", "\n  "));
        }
        return sb.toString();
    }

}

class Home implements CompositeDevice {
    private String name;
    private List<SmartDevice> rooms = new ArrayList<>();

    public Home(String name) {
        this.name = name;
    }

    public void addRoom(SmartDevice room) {
        rooms.add(room);
    }

    public void activate() {
        for (SmartDevice r : rooms)
            r.activate();
    }

    public void deactivate() {
        for (SmartDevice r : rooms)
            r.deactivate();
    }

    public double getPowerUsage() {
        double total = 0;
        for (SmartDevice r : rooms)
            total += r.getPowerUsage();
        return total;
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder("=== " + name + " ===");
        for (SmartDevice r : rooms) {
            sb.append("\n").append(r.getStatus());
        }
        return sb.toString();
    }

    public SmartDevice getBaseDevice() {
        return this;
    }

    public List<SmartDevice> getChildren() {
        return rooms;
    }

    public String getName() {
        return name;
    }
}

abstract class DeviceDecorator implements SmartDevice {
    protected SmartDevice wrapped;

    public DeviceDecorator(SmartDevice wrapped) {
        this.wrapped = wrapped;
    }

    public void activate() { wrapped.activate(); }
    public void deactivate() { wrapped.deactivate(); }
    public double getPowerUsage() { return wrapped.getPowerUsage(); }
    public String getStatus() { return wrapped.getStatus(); }
    public SmartDevice getBaseDevice() { return wrapped.getBaseDevice(); }
}

class AccessRestricted extends DeviceDecorator {
    private int pin;
    private boolean locked = true;

    public AccessRestricted(SmartDevice wrapped, int pin) {
        super(wrapped);
        this.pin = pin;
    }

    public void unlock(int providedPin) {
        if (this.pin == providedPin) locked = false;
    }

    public void lock(int providedPin) {
        if (this.pin == providedPin) locked = true;
    }
    
    @Override
    public void activate() {
        if (!locked) super.activate();
    }

    @Override
    public void deactivate() {
        if (!locked) super.deactivate();
    }

    @Override
    public String getStatus() {
        return super.getStatus() + (locked ? " [LOCKED]" : "");
    }
}

class TimerControlled extends DeviceDecorator {
    private int seconds;
    private boolean timerRunning = false;

    public TimerControlled(SmartDevice wrapped, int seconds) {
        super(wrapped);
        this.seconds = seconds;
    }

    @Override
    public void activate() {
        super.activate();
        timerRunning = true;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        timerRunning = false;
    }

    public void simulateTimerExpiry() {
        if (timerRunning) {
            super.deactivate();
            timerRunning = false;
        }
    }

    @Override
    public String getStatus() {
        return super.getStatus() + (timerRunning ? " (auto-off in " + seconds + "s)" : "");
    }
}

class PowerThrottled extends DeviceDecorator {
    private double cap;

    public PowerThrottled(SmartDevice wrapped, double cap) {
        super(wrapped);
        this.cap = cap;
    }

    @Override
    public double getPowerUsage() {
        double currentPower = super.getPowerUsage();
        return currentPower > cap ? cap : currentPower;
    }

    @Override
    public String getStatus() {
        if (super.getPowerUsage() > cap) {
            return super.getStatus() + " [throttled to " + cap + "W]";
        }
        return super.getStatus();
    }
}


// COMPOSITE DECORATORS


class EcoMode implements CompositeDevice {
    private CompositeDevice wrapped;
    private double budget;

    // Compile-time check: requires a CompositeDevice, rejecting Leaf devices.
    public EcoMode(CompositeDevice wrapped, double budget) {
        this.wrapped = wrapped;
        this.budget = budget;
    }

    public void activate() {
        wrapped.activate();
        shedDevices();
    }

    private void shedDevices() {
        List<SmartDevice> children = wrapped.getChildren();
        // Deactivating in reverse order of installation
        for (int i = children.size() - 1; i >= 0; i--) {
            if (getPowerUsage() <= budget) break;
            children.get(i).deactivate();
        }
    }

    public void deactivate() { wrapped.deactivate(); }
    
    public double getPowerUsage() { return wrapped.getPowerUsage(); }
    
    public String getStatus() { return "[ECO: " + budget + "W budget]\n" + wrapped.getStatus(); }
    
    public SmartDevice getBaseDevice() { return wrapped.getBaseDevice(); }
    public List<SmartDevice> getChildren() { return wrapped.getChildren(); }
    public String getName() { return wrapped.getName(); }
}

class GuestMode implements CompositeDevice {
    private CompositeDevice wrapped;
    private Set<Class<?>> allowed;

    public GuestMode(CompositeDevice wrapped, Set<Class<?>> allowed) {
        this.wrapped = wrapped;
        this.allowed = allowed;
    }

    private boolean isAllowed(SmartDevice device) {
        return allowed.contains(device.getBaseDevice().getClass());
    }

    public void activate() {
        wrapped.activate();
        // skipping disallowed devices
        for (SmartDevice child : wrapped.getChildren()) {
            if (!isAllowed(child)) {
                child.deactivate();
            }
        }
    }

    public void deactivate() {
        wrapped.deactivate();
    }

    public double getPowerUsage() {
        double total = 0;
        for (SmartDevice child : wrapped.getChildren()) {
            if (isAllowed(child)) {
                total += child.getPowerUsage();
            }
        }
        return total;
    }

    public String getStatus() {        
        return "[GUEST MODE]\n" + wrapped.getStatus() + "\n  * Note: Disallowed devices are [guest-restricted]";
    }

    public SmartDevice getBaseDevice() { return wrapped.getBaseDevice(); }
    public List<SmartDevice> getChildren() { return wrapped.getChildren(); }
    public String getName() { return wrapped.getName(); }
}

public class SmartHome {
    
}

