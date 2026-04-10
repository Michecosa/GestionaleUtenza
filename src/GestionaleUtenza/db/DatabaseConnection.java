package GestionaleUtenza.db;

import java.sql.*;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private String url = "jdbc:mysql://localhost:3306/gestionale_db";
    private String user = "root";
    private String pass = "password";

    // Costruttore privato
    private DatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Punto di accesso unico
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Metodo per ottenere la connessione pronta all'uso
    public Connection getConnection() {
        return connection;
    }
}