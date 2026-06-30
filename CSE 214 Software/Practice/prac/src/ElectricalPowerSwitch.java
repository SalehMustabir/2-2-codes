public class ElectricalPowerSwitch implements Switch{
    public Switchable client;
    public boolean on;

    public ElectricalPowerSwitch(Switchable client) {
        this.client = client;
        this.on = false;
    }

    @Override
    public boolean isOn()
    {
        return this.on;
    }

    @Override
    public void press() {
        boolean checkOn = isOn();
        if (checkOn) {
            client.turnOff();
            this.on = false;
        } else {
            client.turnOn();
            this.on = true;
        }
    }
}
