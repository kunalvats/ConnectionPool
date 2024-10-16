import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Main {

    BlockingQueue<Connection> createConnectionPool(int capacity) {
        BlockingQueue<Connection> pool = new BlockingQueue<>(capacity);
        for (int i = 0; i < capacity; i++) {
            try {
                pool.put(ConnectionUtil.getConnection());
            } catch (InterruptedException e) {
                System.out.println("Unable to create connection pool!");
                throw new RuntimeException(e);
            }
        }
        return pool;
    }

    public void executeQ(Connection connection, int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("Select * from country where id = " + id + ";");
        ResultSet res = statement.executeQuery();
        res.next();
        System.out.println(res.getString(1) + "\t" + res.getString(2) + "\t" + res.getString(3) + "\t" + res.getString(4));
    }

    void triggerBlockingRequests(BlockingQueue<Connection> pool){
        List<Thread> threads = new ArrayList<>();
        for (int i=0; i<1000; i++) {
            System.out.println("Trigger Request: " + i);
            int finalI = i;
            Runnable task = () -> {
                Connection connection = null;
                try {
                    connection = pool.take();
                    executeQ(connection, (finalI % 7) + 1);
                    pool.put(connection);
                } catch (InterruptedException | SQLException e) {
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

    void triggerNonBlockingRequests(){
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            System.out.println("Trigger Non Blocking Request: " + i);
            int finalI = i;
            Runnable task = () -> {
                Connection connection = null;
                try {
                    connection = ConnectionUtil.getConnection();
                    executeQ(connection, (finalI % 7) + 1);
                } catch (SQLException e) {
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
        BlockingQueue<Connection> pool = main.createConnectionPool(50);
        Long start = System.currentTimeMillis();
        main.triggerBlockingRequests(pool);
//        main.triggerNonBlockingRequests();
        System.out.println((System.currentTimeMillis() - start)/1000);
    }
}