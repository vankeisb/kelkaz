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
        System.out.println(System.currentTimeMillis() + " [" + clazz.getName() + "] " + message);
    }

    public void error(Object message, Throwable t) {
        debug(message);
        t.printStackTrace();
    }
}
