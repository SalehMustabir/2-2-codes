interface State {

    void insertCoin();

    void pressButton();

}

class NoCoinState implements State {

    private VendingMachine machine;

    public NoCoinState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin() {

        System.out.println("Coin inserted");

        machine.setState(machine.getHasCoinState());

    }

    @Override
    public void pressButton() {

        System.out.println("Insert coin first");

    }

}

class HasCoinState implements State {

    private VendingMachine machine;

    public HasCoinState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin() {

        System.out.println("Coin already inserted");

    }

    @Override
    public void pressButton() {

        System.out.println("Dispensing item...");

        machine.setState(machine.getDispensingState());

    }

}

class DispensingState implements State {

    private VendingMachine machine;

    public DispensingState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin() {

        System.out.println("Please wait...");

    }

    @Override
    public void pressButton() {

        System.out.println("Already dispensing");

    }

    public void dispense() {

        System.out.println("Item delivered");

        machine.setState(machine.getNoCoinState());

    }

}

class VendingMachine {

    private State state;

    private NoCoinState noCoinState;
    private HasCoinState hasCoinState;
    private DispensingState dispensingState;

    public VendingMachine() {

        noCoinState = new NoCoinState(this);
        hasCoinState = new HasCoinState(this);
        dispensingState = new DispensingState(this);

        state = noCoinState;

    }

    public void insertCoin() {
        state.insertCoin();
    }

    public void pressButton() {

        state.pressButton();

        if(state == dispensingState) {

            dispensingState.dispense();

        }

    }

    public void setState(State state) {
        this.state = state;
    }

    public NoCoinState getNoCoinState() {
        return noCoinState;
    }

    public HasCoinState getHasCoinState() {
        return hasCoinState;
    }

    public DispensingState getDispensingState() {
        return dispensingState;
    }

}

public class StatePattern {

    public static void main(String[] args) {

        VendingMachine machine = new VendingMachine();

        machine.pressButton();

        System.out.println();

        machine.insertCoin();

        System.out.println();

        machine.pressButton();

    }

}