interface Notification
{
    void notifyUser();
}

class SMS implements Notification
{
    public void notifyUser()
    {
        System.out.println("Notification sms");
    }
}
class Email implements Notification
{
    public void notifyUser()
    {
        System.out.println("Notification email");
    }
}
class PushNotification implements Notification
{
    public void notifyUser()
    {
        System.out.println("Notification push");
    }
}

class NotiFactory
{
    public static Notification build(String s)
    {
        if(s.equalsIgnoreCase("sms")) return new SMS();
        else if(s.equalsIgnoreCase("email")) return new Email();
        else if(s.equalsIgnoreCase("push notification")) return new PushNotification();
        else return null;
    }
}

public class Factory {
    public static void main(String[] args) {
        Notification sms = NotiFactory.build("SMS");
        sms.notifyUser();
    }
}
