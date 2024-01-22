import java.io.File;

public class Main {

    private static Application application = new Application();
    private static DatabaseConnector databaseConnector = new DatabaseConnector();

    public static void main(String[] args) {
        createDirectoryIfNotExists("patients");
        createDirectoryIfNotExists("doctors");
        createDirectoryIfNotExists("appointments");

        application.run(args);
        databaseConnector.closeConnection();
    }

    private static void createDirectoryIfNotExists(String directoryName) {
        File directory = new File(directoryName);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("Directory '" + directoryName + "' created.");
            } else {
                System.out.println("Failed to create directory '" + directoryName + "'.");
            }
        }
    }
}
