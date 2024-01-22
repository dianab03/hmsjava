import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "4201";
    private Connection connection;

    public Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
