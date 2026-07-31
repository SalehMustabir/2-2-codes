// ==========================================
// 1. THE IMPLEMENTATION LAYER (Platform)
// ==========================================

// The bridge interface that all OS APIs must follow
interface OSAPI {
    void renderWindow(String title);
    void renderButton(String label);
}

// Concrete Implementation 1: Windows
class WindowsAPI implements OSAPI {
    @Override
    public void renderWindow(String title) {
        System.out.println("Rendering Windows-style window: " + title);
    }

    @Override
    public void renderButton(String label) {
        System.out.println("Drawing a sharp-cornered Windows button: [" + label + "]");
    }
}

// Concrete Implementation 2: MacOS
class MacAPI implements OSAPI {
    @Override
    public void renderWindow(String title) {
        System.out.println("Rendering Mac-style window with shadow: " + title);
    }

    @Override
    public void renderButton(String label) {
        System.out.println("Drawing a rounded Mac button: (" + label + ")");
    }
}

// ==========================================
// 2. THE ABSTRACTION LAYER (GUI Control)
// ==========================================

// The Base Abstraction
abstract class AppGUI {
    // THE BRIDGE: A reference to the implementation layer
    protected OSAPI osApi; 

    public AppGUI(OSAPI osApi) {
        this.osApi = osApi;
    }

    // High-level operation 
    public abstract void display(); 
}

// Refined Abstraction 1: Regular User GUI
class RegularUserGUI extends AppGUI {

    public RegularUserGUI(OSAPI osApi) {
        super(osApi);
    }

    @Override
    public void display() {
        System.out.println("\n--- Loading Regular User Dashboard ---");
        // Delegating the actual drawing across the bridge
        osApi.renderWindow("Welcome User");
        osApi.renderButton("View Profile");
    }
}

// Refined Abstraction 2: Admin GUI
class AdminGUI extends AppGUI {

    public AdminGUI(OSAPI osApi) {
        super(osApi);
    }

    @Override
    public void display() {
        System.out.println("\n--- Loading Admin Control Panel ---");
        osApi.renderWindow("Admin Dashboard - Root Access");
        osApi.renderButton("Delete User");
        osApi.renderButton("Server Settings");
    }
}

// ==========================================
// 3. THE CLIENT (Main Execution)
// ==========================================

public class BridgeTestDrive {
    public static void main(String[] args) {
        
        // --- Scenario 1: Regular User on Windows ---
        OSAPI windows = new WindowsAPI();
        AppGUI userAppOnWindows = new RegularUserGUI(windows);
        
        userAppOnWindows.display();
        
        
        // --- Scenario 2: Admin User on Mac ---
        OSAPI macOs = new MacAPI();
        AppGUI adminAppOnMac = new AdminGUI(macOs);
        
        adminAppOnMac.display();


        // --- Scenario 3: Admin User on Windows (Mixing and Matching!) ---
        AppGUI adminAppOnWindows = new AdminGUI(windows);
        
        adminAppOnWindows.display();
    }
}