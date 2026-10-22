package service;

import util.Command;

/** Fixed-size array buffer shared by a producer and a consumer thread (wait/notify). */
public class TransactionBuffer {
    private final Command[] items = new Command[4];
    private int count = 0, in = 0, out = 0;

    public synchronized void put(Command c) throws InterruptedException {
        while (count == items.length) wait();
        items[in] = c;
        in = (in + 1) % items.length;
        count++;
        notifyAll();
    }

    public synchronized Command take() throws InterruptedException {
        while (count == 0) wait();
        Command c = items[out];
        out = (out + 1) % items.length;
        count--;
        notifyAll();
        return c;
    }
}
