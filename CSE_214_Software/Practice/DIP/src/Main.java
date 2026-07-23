public class Main {
    public static void main(String[] args) {
        Switchable fan = new Fan();
        Switch fan_switch = new ElectricalPowerSwitch(fan);
        fan_switch.press();
        fan_switch.press();

    }
}