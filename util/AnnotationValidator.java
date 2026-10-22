package util;

import model.annotation.MaxLength;
import model.annotation.Positive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotationValidator {

    /** Returns only the error messages. */
    public static String[] validate(Object obj) {
        return collect(obj, false).toArray(new String[0]);
    }

    /** Same, but each message is prefixed with the field name (supplementary). */
    public static String[] validateWithFieldNames(Object obj) {
        return collect(obj, true).toArray(new String[0]);
    }

    private static List<String> collect(Object obj, boolean withName) {
        List<String> errors = new ArrayList<>();
        // walk up the class hierarchy so inherited fields (Account) are checked too
        for (Class<?> c = obj.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true);
                Object value;
                try {
                    value = f.get(obj);
                } catch (IllegalAccessException e) {
                    continue;
                }
                String prefix = withName ? f.getName() + ": " : "";
                if (f.isAnnotationPresent(Positive.class) && value instanceof Number n && n.longValue() <= 0) {
                    errors.add(prefix + f.getAnnotation(Positive.class).message());
                }
                if (f.isAnnotationPresent(MaxLength.class) && value instanceof String s) {
                    int max = f.getAnnotation(MaxLength.class).value();
                    if (s.length() > max) errors.add(prefix + "length must be <= " + max);
                }
            }
        }
        return errors;
    }
}
