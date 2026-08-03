interface HomeMediator {

    void motionDetected();

    void doorForced();

    void smokeDetected();
}

class SmartHomeHub implements HomeMediator {

    private Light light;
    private Alarm alarm;
    private Camera camera;

    public void setLight(Light light) {
        this.light = light;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void motionDetected() {

        System.out.println("Hub: Motion detected");

        light.turnOn();
    }

    @Override
    public void doorForced() {

        System.out.println("Hub: Door forced");

        alarm.activate();

        camera.startRecording();
    }

    @Override
    public void smokeDetected() {

        System.out.println("Hub: Smoke detected");

        alarm.activate();

        System.out.println("Hub: Sending notification to owner");
    }
}

class MotionSensor {

    private HomeMediator mediator;

    public MotionSensor(HomeMediator mediator) {
        this.mediator = mediator;
    }

    public void detectMotion() {

        System.out.println("MotionSensor: Motion detected");

        mediator.motionDetected();
    }
}

class DoorSensor {

    private HomeMediator mediator;

    public DoorSensor(HomeMediator mediator) {
        this.mediator = mediator;
    }

    public void detectForcedDoor() {

        System.out.println("DoorSensor: Forced door");

        mediator.doorForced();
    }
}

class SmokeSensor {

    private HomeMediator mediator;

    public SmokeSensor(HomeMediator mediator) {
        this.mediator = mediator;
    }

    public void detectSmoke() {

        System.out.println("SmokeSensor: Smoke detected");

        mediator.smokeDetected();
    }
}

class Light {

    public void turnOn() {

        System.out.println("Light: ON");
    }
}

class Alarm {

    public void activate() {

        System.out.println("Alarm: ACTIVATED");
    }
}

class Camera {

    public void startRecording() {

        System.out.println("Camera: Recording...");
    }
}

public class Mediator {

    public static void main(String[] args) {

        SmartHomeHub hub = new SmartHomeHub();

        Light light = new Light();
        Alarm alarm = new Alarm();
        Camera camera = new Camera();

        hub.setLight(light);
        hub.setAlarm(alarm);
        hub.setCamera(camera);

        MotionSensor motion = new MotionSensor(hub);
        DoorSensor door = new DoorSensor(hub);
        SmokeSensor smoke = new SmokeSensor(hub);

        motion.detectMotion();

        System.out.println();

        door.detectForcedDoor();

        System.out.println();

        smoke.detectSmoke();
    }
}