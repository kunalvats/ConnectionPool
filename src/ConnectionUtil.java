import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {
    private static String url = "jdbc:postgresql://localhost:5433/media_service";
    private static String driverName = "org.postgresql.Driver";
    private static String username = "username";
    private static String password = "password";
    private static java.sql.Connection con;
    private static String urlstring;

    public static java.sql.Connection getConnection() {
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(url, username, password);
            } catch (SQLException ex) {
                // log an exception. fro example:
                System.out.println("Failed to create the database connection.");
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found.");
        }
        return con;
    }
}

