//package CSE_214_Software.Practice.Structural.Composite;

import java.util.ArrayList;
import java.util.List;

// BASE INTERFACE: Everything (Menus and MenuItems) can do this.
interface MenuComponent {
    String getName();
    String getDescription();
    void print();
}

// COMPOSITE INTERFACE: Only Menus (Nodes) can do this.
interface CompositeMenu extends MenuComponent {
    void add(MenuComponent component);
    void remove(MenuComponent component);
    MenuComponent getChild(int index);
}

class MenuItem implements MenuComponent {
    String name;
    String description;
    double price;

    public MenuItem(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public String getName() { return name; }
    
    @Override
    public String getDescription() { return description; }

    public double getPrice() { return price; } // Specific to Leaves

    @Override
    public void print() {
        System.out.println("  " + getName() + ", $" + getPrice());
        System.out.println("     -- " + getDescription());
    }
}



class Menu implements CompositeMenu {
    String name;
    String description;
    // We store the base interface here so it can hold BOTH Menus and MenuItems
    List<MenuComponent> menuComponents = new ArrayList<>();

    public Menu(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // --- Composite-Specific Methods ---
    @Override
    public void add(MenuComponent component) {
        menuComponents.add(component);
    }

    @Override
    public void remove(MenuComponent component) {
        menuComponents.remove(component);
    }

    @Override
    public MenuComponent getChild(int index) {
        return menuComponents.get(index);
    }

    // --- Base Component Methods ---
    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }

    @Override
    public void print() {
        System.out.println("\n" + getName() + ", " + getDescription());
        System.out.println("---------------------");
        
        // Polymorphic traversal remains intact!
        for (MenuComponent component : menuComponents) {
            component.print(); 
        }
    }
}

public class MenuTestDrive {
    public static void main(String[] args) {
        // We MUST declare these as CompositeMenu so the compiler lets us use .add()
        CompositeMenu pancakeHouseMenu = new Menu("PANCAKE HOUSE MENU", "Breakfast");
        CompositeMenu dinerMenu = new Menu("DINER MENU", "Lunch");
        CompositeMenu allMenus = new Menu("ALL MENUS", "All menus combined");

        // Build the tree
        allMenus.add(pancakeHouseMenu);
        allMenus.add(dinerMenu);
        
        // Add leaves (MenuItems only implement MenuComponent, which is perfectly valid to add)
        pancakeHouseMenu.add(new MenuItem("Regular Pancake Breakfast", "Pancakes with fried eggs", 2.99));
        dinerMenu.add(new MenuItem("Hot Dog", "A hot dog, with sauerkraut", 3.05));
        
        // --- THE PAYOFF ---
        // Even though we built it securely, we can still treat the entire tree as a single base component.
        // A Waitress object only needs a MenuComponent to print the whole tree.
        MenuComponent rootNode = allMenus;
        rootNode.print();
        
        // Error handling payoff:
        MenuItem myHotDog = new MenuItem("Hot Dog", "Plain", 2.00);
        // myHotDog.add(dinerMenu); // <-- COMPILER ERROR! Safety achieved.

    }
} 
