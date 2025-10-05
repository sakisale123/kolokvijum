package com.naplatnarampa.repository;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
   
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "naplatna_rampa_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; 

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?userSSL=false&serverTimezone=UTC";
    
   
    public static Connection getConnection() throws SQLException {
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
           
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("GREŠKA: MySQL JDBC drajver nije pronađen!");
            e.printStackTrace();
            
            throw new SQLException("MySQL JDBC drajver nije pronađen.", e);
        } catch (SQLException e) {
            System.err.println("GREŠKA: Neuspešna konekcija na bazu! Proveri podatke i da li je MySQL server pokrenut.");
            JOptionPane.showMessageDialog(null, "Nije moguće povezati se sa bazom podataka.\nProverite da li je MySQL server pokrenut i da li su podaci za konekciju ispravni.", "Greška Baze", JOptionPane.ERROR_MESSAGE);
            throw e; 
        }
    }

    
    public static void createTables() {
        String[] tablesSQL = {
            "CREATE TABLE IF NOT EXISTS naplatna_stanica (id BIGINT AUTO_INCREMENT PRIMARY KEY, naziv VARCHAR(255) NOT NULL, lokacija VARCHAR(255) NOT NULL);",
            "CREATE TABLE IF NOT EXISTS kategorija_vozila (id BIGINT AUTO_INCREMENT PRIMARY KEY, naziv VARCHAR(255) NOT NULL, koeficijent_cene FLOAT NOT NULL);",
            "CREATE TABLE IF NOT EXISTS osoba (id BIGINT AUTO_INCREMENT PRIMARY KEY, ime VARCHAR(255), prezime VARCHAR(255), korisnicko_ime VARCHAR(255) UNIQUE NOT NULL, lozinka VARCHAR(255), uloga VARCHAR(50), id_stanice BIGINT, FOREIGN KEY (id_stanice) REFERENCES naplatna_stanica(id) ON DELETE SET NULL);",
            "CREATE TABLE IF NOT EXISTS naplatna_traka (id BIGINT AUTO_INCREMENT PRIMARY KEY, id_stanice BIGINT NOT NULL, rezim VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL, FOREIGN KEY (id_stanice) REFERENCES naplatna_stanica(id) ON DELETE CASCADE);",
            "CREATE TABLE IF NOT EXISTS cenovnik (id BIGINT AUTO_INCREMENT PRIMARY KEY, id_ulazne_stanice BIGINT, id_izlazne_stanice BIGINT, kilometraza DOUBLE, cena_po_km FLOAT, FOREIGN KEY (id_ulazne_stanice) REFERENCES naplatna_stanica(id) ON DELETE CASCADE, FOREIGN KEY (id_izlazne_stanice) REFERENCES naplatna_stanica(id) ON DELETE CASCADE);",
            "CREATE TABLE IF NOT EXISTS vozilo (id BIGINT AUTO_INCREMENT PRIMARY KEY, registarska_tablica VARCHAR(255) UNIQUE NOT NULL);",
            "CREATE TABLE IF NOT EXISTS transakcija (id BIGINT AUTO_INCREMENT PRIMARY KEY, id_vozila BIGINT NOT NULL, id_kategorije BIGINT NOT NULL, id_ulazne_stanice BIGINT NOT NULL, id_izlazne_stanice BIGINT, id_radnika_ulaz BIGINT, id_radnika_izlaz BIGINT, vreme_ulaza DATETIME, vreme_izlaza DATETIME, naplaceni_iznos FLOAT, zavrsena BOOLEAN, FOREIGN KEY (id_vozila) REFERENCES vozilo(id), FOREIGN KEY (id_kategorije) REFERENCES kategorija_vozila(id), FOREIGN KEY (id_ulazne_stanice) REFERENCES naplatna_stanica(id), FOREIGN KEY (id_izlazne_stanice) REFERENCES naplatna_stanica(id), FOREIGN KEY (id_radnika_ulaz) REFERENCES osoba(id), FOREIGN KEY (id_radnika_izlaz) REFERENCES osoba(id));",
            "CREATE TABLE IF NOT EXISTS kvar (id BIGINT AUTO_INCREMENT PRIMARY KEY, id_trake BIGINT NOT NULL, opis TEXT, vreme_prijave DATETIME, FOREIGN KEY (id_trake) REFERENCES naplatna_traka(id) ON DELETE CASCADE);"
        };

        
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : tablesSQL) {
                stmt.execute(sql);
            }
            System.out.println("Sve tabele su uspešno kreirane/proverene.");
        } catch (SQLException e) {
            System.err.println("Greška pri kreiranju tabela: " + e.getMessage());
        }
    }
}