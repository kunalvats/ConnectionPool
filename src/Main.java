import java.util.ArrayList;
import java.util.List;

public class Main {

    BlockingQueue<Connection> createConnectionPool(int capacity) {
        BlockingQueue<Connection> pool = new BlockingQueue<>(capacity);
        for (int i = 0; i < capacity; i++) {
            try {
                pool.put(new Connection());
            } catch (InterruptedException e) {
                System.out.println("Unable to create connection pool!");
                throw new RuntimeException(e);
            }
        }
        return pool;
    }

    void triggerRequests(BlockingQueue<Connection> pool){
        List<Thread> threads = new ArrayList<>();
        for (int i=0; i<10; i++) {
            System.out.println("Trigger Request: " + i);
            Runnable task = () -> {
                Connection connection = null;
                try {
                    connection = pool.take();
                    connection.executeQuery();
                    pool.put(connection);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e){

        }
    }


    public static void main(String[] args) {
        Main main = new Main();
        Long start = System.currentTimeMillis();
        BlockingQueue<Connection> pool = main.createConnectionPool(10);
        main.triggerRequests(pool);
        System.out.println((System.currentTimeMillis() - start)/1000);
    }
}