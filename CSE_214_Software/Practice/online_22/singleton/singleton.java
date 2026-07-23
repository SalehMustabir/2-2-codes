class Logger{

    private static Logger instance;

    private Logger(){
        System.out.println("Logger instance created");
    }

    public static Logger getInstance(){

        if(instance==null){
            instance=new Logger();
        }

        return instance;
    }

    public void log(String msg){

        System.out.println("Log: " + msg);
    }
}




public class singleton {
    public static void main(String[] args) {
        
        System.out.println("client 1......");
        Logger logger1= Logger.getInstance();
        logger1.log("client 1 asking for refund");

        System.out.println("client 2-------");
        Logger logger2=Logger.getInstance();
        logger2.log("client 2 asking for deposit");


        if(logger1==logger2){
            System.out.println("both using same logger");
        }else{
            System.out.println("different logger");
        }

        Logger l3 = Logger.getInstance();
        System.out.println(logger1 == l3);
    }
    
}
