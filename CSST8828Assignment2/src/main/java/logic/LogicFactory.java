package logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class LogicFactory {

    private static final String PACKAGE = "logic.";
    private static final String SUFFIX = "Logic";

    private LogicFactory() {
    }

    // I think the correct way is: return getFor(type). 
    public static < T> T getFor(String entityName) {

        try {
            Class<T> type = (Class<T>) Class.forName(PACKAGE + entityName + SUFFIX);
            T newInstance = getFor(type);
            return newInstance;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);//exception wrapping, checked exception placed inside of a unchceked exception
        }
    }

    //again probably should just return declaredConstructor
    public static <T> T getFor(Class<T> type) {

        try {
            Constructor<T> declaredConstructor = type.getDeclaredConstructor();
            T newInstance = declaredConstructor.newInstance();
            return newInstance;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);//exception wrapping, checked exception placed inside of a unchceked exception
        }
    }
}
