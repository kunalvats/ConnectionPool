import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {

    private Queue<T> queue;
    private ReentrantLock lock;

    private Condition isEmpty;
    private Condition isFull;

    private int capacity;

    public BlockingQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
        this.lock = new ReentrantLock();
        this.isEmpty = lock.newCondition();
        this.isFull = lock.newCondition();
    }

    public void put(T obj) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                isFull.await();
            }
            queue.add(obj);
            isEmpty.signal();
        } catch (IllegalMonitorStateException e){
            System.out.println("No Awaiting condition!");
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        T obj = null;
        try {
            while (queue.isEmpty()) {
                isEmpty.await();
            }
            obj = queue.remove();
            isFull.signal();
        } catch (IllegalMonitorStateException e){
            System.out.println("No Awaiting condition!");
        } finally {
            lock.unlock();
        }
        return obj;
    }

}
