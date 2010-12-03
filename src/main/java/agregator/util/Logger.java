package agregator.util;

public class Logger {

    private Class clazz;

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    private Logger(Class clazz) {
        this.clazz = clazz;
    }

    public void debug(Object message) {
        System.out.println(System.currentTimeMillis() + " [DEBUG] [" + clazz.getName() + "] " + message);
    }

    public void error(Object message, Throwable t) {
        System.out.println(System.currentTimeMillis() + " [ERROR] [" + clazz.getName() + "] " + message);
        t.printStackTrace();
    }

    public void warn(Object message, Throwable t) {
        System.out.println(System.currentTimeMillis() + " [WARNING] [" + clazz.getName() + "] " + message);
        t.printStackTrace();
    }

    public void warn(Object message) {
        System.out.println(System.currentTimeMillis() + " [WARNING] [" + clazz.getName() + "] " + message);
    }


}
