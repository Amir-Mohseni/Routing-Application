package dbTables;

public class dbCredentials {
    public static final String HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    public static final String PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306"; // Default MySQL port
    public static final String USERNAME = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "root";
    public static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";
    public static final String DATABASE_NAME = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "defaultdb";

    // Optional: Method to print the current configuration
    public static void printConfig() {
        System.out.println("DB_HOST: " + HOST);
        System.out.println("DB_PORT: " + PORT);
        System.out.println("DB_USERNAME: " + USERNAME);
        System.out.println("DB_PASSWORD: " + PASSWORD);
        System.out.println("DB_NAME: " + DATABASE_NAME);
    }
}
