package service;

import model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Generic repository: stores any entity type T under the value of its @Id field. */
public class Repository<T> {
    private final Map<String, T> store = new HashMap<>();

    public T save(T entity) {
        store.put(idOf(entity), entity);
        return entity;
    }

    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) save(item);
    }

    public T findById(String id) { return store.get(id); }

    public ArrayList<T> findAll() { return new ArrayList<>(store.values()); }

    public boolean delete(String id) { return store.remove(id) != null; }

    public int count() { return store.size(); }

    /** Finds the field marked @Id by reflection (searching superclasses too) and reads its value. */
    private static String idOf(Object entity) {
        for (Class<?> c = entity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (f.isAnnotationPresent(Id.class)) {
                    try {
                        f.setAccessible(true);
                        return String.valueOf(f.get(entity));
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Cannot read @Id field", e);
                    }
                }
            }
        }
        throw new IllegalArgumentException(entity.getClass().getSimpleName() + " has no @Id field");
    }

    /** Bounded type parameter: only Number subclasses are accepted. */
    public static <N extends Number> double average(N[] values) {
        double sum = 0;
        for (N v : values) sum += v.doubleValue();
        return values.length == 0 ? 0 : sum / values.length;
    }

    /** Wildcard example: works for a collection of anything. */
    public static void printAll(Collection<?> items) {
        for (Object o : items) System.out.println("  " + o);
    }
}
