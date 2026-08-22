package Offline_3;

import java.util.*;

enum AlertCategory {
    EARTHQUAKE,
    FLOOD,
    FIRE
}

class Alert {
    private String title;
    private AlertCategory category;
    private String affectedLocation;
    private String severityLevel;
    private String safetyInstructions;

    private Alert(Builder builder) {
        this.title = builder.title;
        this.category = builder.category;
        this.affectedLocation = builder.affectedLocation;
        this.severityLevel = builder.severityLevel;
        this.safetyInstructions = builder.safetyInstructions;
    }

    public String getTitle() {
        return title;
    }

    public AlertCategory getCategory() {
        return category;
    }

    public String getAffectedLocation() {
        return affectedLocation;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public String getSafetyInstructions() {
        return safetyInstructions;
    }

    @Override
    public String toString() {
        return """
                Alert Details
                -------------------------
                Title: %s
                Category: %s
                Location: %s
                Severity: %s
                Safety Instructions: %s
                """.formatted(
                title,
                category,
                affectedLocation,
                severityLevel,
                safetyInstructions);
    }

    // Builder class for Alert
    public static class Builder {
        private String title;
        private AlertCategory category;
        private String affectedLocation;
        private String severityLevel;
        private String safetyInstructions;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setCategory(AlertCategory category) {
            this.category = category;
            return this;
        }

        public Builder setAffectedLocation(String affectedLocation) {
            this.affectedLocation = affectedLocation;
            return this;
        }

        public Builder setSeverityLevel(String severityLevel) {
            this.severityLevel = severityLevel;
            return this;
        }

        public Builder setSafetyInstructions(String safetyInstructions) {
            this.safetyInstructions = safetyInstructions;
            return this;
        }

        public Alert build() {
            return new Alert(this);
        }
    }

}

interface Observer {
    void update(Alert alert);
}

interface Subject {
    void subscribe(AlertCategory category, Observer observer);

    void unsubscribe(AlertCategory category, Observer observer);

    void notifyObservers(Alert alert);
}

class Citizen implements Observer {
    private String name;
    private List<Alert> receivedAlerts;

    public Citizen(String name) {
        this.name = name;
        this.receivedAlerts = new ArrayList<>();
    }

    @Override
    public void update(Alert alert) {
        receivedAlerts.add(alert);

        System.out.println(
                "Notification sent to " + name +
                        ": " + alert.getTitle());
    }

    public String getName() {
        return name;
    }

    public void displayNotifications() {

        System.out.println("\n=================================");
        System.out.println("Notifications for " + name);
        System.out.println("=================================");

        if (receivedAlerts.isEmpty()) {
            System.out.println("No notifications received.");
            return;
        }

        for (int i = 0; i < receivedAlerts.size(); i++) {

            Alert alert = receivedAlerts.get(i);

            System.out.println("\nNotification #" + (i + 1));
            System.out.println(alert);
        }
    }

}

class BDAlertSystem implements Subject {

    private Map<AlertCategory, List<Observer>> subscribers;
    private List<Citizen> citizens;

    public BDAlertSystem() {
        subscribers = new EnumMap<>(AlertCategory.class);
        citizens = new ArrayList<>();

        for (AlertCategory category : AlertCategory.values()) {
            subscribers.put(category, new ArrayList<>());
        }
    }

    public void registerCitizen(Citizen citizen) {
        if (!citizens.contains(citizen)) {
            citizens.add(citizen);

            System.out.println(
                    citizen.getName() + " registered successfully.");
        }
    }

    @Override
    public void subscribe(
            AlertCategory category,
            Observer observer) {
        List<Observer> observers = subscribers.get(category);

        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unsubscribe(
            AlertCategory category,
            Observer observer) {
        subscribers.get(category).remove(observer);
    }

    @Override
    public void notifyObservers(Alert alert) {

        List<Observer> observers = subscribers.get(alert.getCategory());

        for (Observer observer : observers) {
            observer.update(alert);
        }
    }

    public void publishAlert(Alert alert) {

        System.out.println("\nPublishing Alert:");
        System.out.println(alert);

        notifyObservers(alert);
    }
}

public class ObserverPattern {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("       BD ALERT SYSTEM STARTED       ");
        System.out.println("====================================");

        // Create BDAlertSystem instance
        BDAlertSystem bdAlertSystem = new BDAlertSystem();

        // Register citizens

        Citizen citizen1 = new Citizen("Alice");
        Citizen citizen2 = new Citizen("Bob");
        Citizen citizen3 = new Citizen("Charlie");
        Citizen citizen4 = new Citizen("David");

        bdAlertSystem.registerCitizen(citizen1);
        bdAlertSystem.registerCitizen(citizen2);
        bdAlertSystem.registerCitizen(citizen3);
        bdAlertSystem.registerCitizen(citizen4);

        // subscribe citizens to alert categories

        // citizen1 subscribes to EARTHQUAKE and FLOOD alerts
        bdAlertSystem.subscribe(AlertCategory.EARTHQUAKE, citizen1);
        bdAlertSystem.subscribe(AlertCategory.FLOOD, citizen1);

        // citizern2 subscribes to FIRE alerts
        bdAlertSystem.subscribe(AlertCategory.FIRE, citizen2);

        // citizen3 subscribes to EARTHQUAKE and FIRE alerts
        bdAlertSystem.subscribe(AlertCategory.EARTHQUAKE, citizen3);
        bdAlertSystem.subscribe(AlertCategory.FIRE, citizen3);

        // citizen4 subscribes to FLOOD alerts
        bdAlertSystem.subscribe(AlertCategory.FLOOD, citizen4);

        // printing who are subscribed to which alert categories
        System.out.println("\n====================================");
        System.out.println("       SUBSCRIPTION DETAILS          ");
        System.out.println("====================================");
        System.out.println("Citizen: " + citizen1.getName() + " | Subscribed to: EARTHQUAKE, FLOOD");
        System.out.println("Citizen: " + citizen2.getName() + " | Subscribed to: FIRE");
        System.out.println("Citizen: " + citizen3.getName() + " | Subscribed to: EARTHQUAKE, FIRE");
        System.out.println("Citizen: " + citizen4.getName() + " | Subscribed to: FLOOD");

        // publishing alerts

        // EARTHQUAKE alert
        Alert earthquakeAlert = new Alert.Builder()
                .setTitle("Earthquake Alert")
                .setCategory(AlertCategory.EARTHQUAKE)
                .setAffectedLocation("Dhaka, Bangladesh")
                .setSeverityLevel("High")
                .setSafetyInstructions("Evacuate immediately and seek open spaces.")
                .build();

        bdAlertSystem.publishAlert(earthquakeAlert);

        // flood alert
        Alert floodAlert = new Alert.Builder()
                .setTitle("Flood Alert")
                .setCategory(AlertCategory.FLOOD)
                .setAffectedLocation("Chittagong, Bangladesh")
                .setSeverityLevel("Moderate")
                .setSafetyInstructions("Move to higher ground and avoid waterlogged areas.")
                .build();
        bdAlertSystem.publishAlert(floodAlert);

        // fire alert
        Alert fireAlert = new Alert.Builder()
                .setTitle("Fire Alert")
                .setCategory(AlertCategory.FIRE)
                .setAffectedLocation("Sylhet, Bangladesh")
                .setSeverityLevel("Severe")
                .setSafetyInstructions("Evacuate the area and call emergency services.")
                .build();
        bdAlertSystem.publishAlert(fireAlert);

        // updating subscriptions
        System.out.println("\n====================================");
        System.out.println("      UPDATING SUBSCRIPTIONS");
        System.out.println("====================================");

        // citizer2 subscibes to flood alerts
        System.out.println("\n" + citizen2.getName() + " is subscribing to FLOOD alerts.");
        bdAlertSystem.subscribe(AlertCategory.FLOOD, citizen2);

        // citizen3 unsubscribes from FIRE alerts
        System.out.println("\n" + citizen3.getName() + " is unsubscribing from FIRE alerts.");
        bdAlertSystem.unsubscribe(AlertCategory.FIRE, citizen3);

        // publiushing another flood alert
        Alert floodAlert2 = new Alert.Builder()
                .setTitle("Flood Alert 2")
                .setCategory(AlertCategory.FLOOD)
                .setAffectedLocation("Barisal, Bangladesh")
                .setSeverityLevel("High")
                .setSafetyInstructions("Move to higher ground and avoid waterlogged areas.")
                .build();
        bdAlertSystem.publishAlert(floodAlert2);

        // publishing another fire alert
        Alert fireAlert2 = new Alert.Builder()
                .setTitle("Fire Alert 2")
                .setCategory(AlertCategory.FIRE)
                .setAffectedLocation("Khulna, Bangladesh")
                .setSeverityLevel("Severe")
                .setSafetyInstructions("Evacuate the area and call emergency services.")
                .build();
        bdAlertSystem.publishAlert(fireAlert2);

        // displaying notifications for each citizen
        System.out.println("\n\n====================================");
        System.out.println("      NOTIFICATION HISTORY");
        System.out.println("====================================");

        citizen1.displayNotifications();
        citizen2.displayNotifications();
        citizen3.displayNotifications();
        citizen4.displayNotifications();

        System.out.println("\n====================================");
        System.out.println("       BD ALERT SYSTEM ENDED");
        System.out.println("====================================");
    }

}