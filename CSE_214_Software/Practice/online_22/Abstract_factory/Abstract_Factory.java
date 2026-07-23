// Product Interfaces
interface Button {
    void render();
}

interface TextField {
    void render();
}

interface Dialog {
    void render();
}

// ==================== Light Theme Products ====================

class LightButton implements Button {
    @Override
    public void render() {
        System.out.println("Light Button");
    }
}

class LightTextField implements TextField {
    @Override
    public void render() {
        System.out.println("Light TextField");
    }
}

class LightDialog implements Dialog {
    @Override
    public void render() {
        System.out.println("Light Dialog");
    }
}

// ==================== Dark Theme Products ====================

class DarkButton implements Button {
    @Override
    public void render() {
        System.out.println("Dark Button");
    }
}

class DarkTextField implements TextField {
    @Override
    public void render() {
        System.out.println("Dark TextField");
    }
}

class DarkDialog implements Dialog {
    @Override
    public void render() {
        System.out.println("Dark Dialog");
    }
}

// ==================== Abstract Factory ====================

interface GUIFactory {
    Button createButton();
    TextField createTextField();
    Dialog createDialog();
}

// ==================== Concrete Factories ====================

class LightThemeFactory implements GUIFactory {

    @Override
    public Button createButton() {
        return new LightButton();
    }

    @Override
    public TextField createTextField() {
        return new LightTextField();
    }

    @Override
    public Dialog createDialog() {
        return new LightDialog();
    }
}

class DarkThemeFactory implements GUIFactory {

    @Override
    public Button createButton() {
        return new DarkButton();
    }

    @Override
    public TextField createTextField() {
        return new DarkTextField();
    }

    @Override
    public Dialog createDialog() {
        return new DarkDialog();
    }
}

// ==================== Factory Producer ====================

class FactoryProducer {

    public static GUIFactory getFactory(String theme) {

        if (theme.equalsIgnoreCase("Light")) {
            return new LightThemeFactory();
        } else if (theme.equalsIgnoreCase("Dark")) {
            return new DarkThemeFactory();
        }

        throw new IllegalArgumentException("Invalid Theme");
    }
}

// ==================== Client ====================

class Application {

    private Button button;
    private TextField textField;
    private Dialog dialog;

    public Application(String theme) {

        GUIFactory factory = FactoryProducer.getFactory(theme);

        button = factory.createButton();
        textField = factory.createTextField();
        dialog = factory.createDialog();
    }

    public void displayUI() {
        button.render();
        textField.render();
        dialog.render();
    }
}

// ==================== Main ====================

public class Abstract_Factory {

    public static void main(String[] args) {

        Application app = new Application("Dark");
        app.displayUI();

        System.out.println();

        Application app2 = new Application("Light");
        app2.displayUI();
    }
}