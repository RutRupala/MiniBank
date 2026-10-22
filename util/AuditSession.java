package util;

/** Small AutoCloseable used to demonstrate try-with-resources. */
public class AuditSession implements AutoCloseable {
    private final String name;

    public AuditSession(String name) {
        this.name = name;
        System.out.println("[session] opened " + name);
    }

    public void log(String msg) { System.out.println("[session " + name + "] " + msg); }

    @Override
    public void close() { System.out.println("[session] closed " + name); }
}
