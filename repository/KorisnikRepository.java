package com.naplatnarampa.repository;

import com.naplatnarampa.model.*;
import com.naplatnarampa.model.enums.Uloga;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KorisnikRepository {

    public Osoba save(Osoba osoba) {
        String sql = "INSERT INTO osoba(ime, prezime, korisnicko_ime, lozinka, uloga, id_stanice) VALUES(?,?,?,?,?,?)";
        
        
        Connection conn = null;
        try {
            
            conn = DatabaseConnection.getConnection();
            
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, osoba.getIme());
                pstmt.setString(2, osoba.getPrezime());
                pstmt.setString(3, osoba.getKorisnickoIme());
                pstmt.setString(4, osoba.getLozinka());
                pstmt.setString(5, osoba.getUloga().toString());

                if (osoba instanceof Radnik && ((Radnik) osoba).getRadnoMesto() != null) {
                    pstmt.setLong(6, ((Radnik) osoba).getRadnoMesto().getId());
                } else {
                    pstmt.setNull(6, Types.BIGINT);
                }

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Kreiranje korisnika nije uspelo, nijedan red nije dodat.");
                }

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        osoba.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Kreiranje korisnika nije uspelo, ID nije dobijen.");
                    }
                }
            }
            
            
            conn.commit();
            System.out.println("Uspešno sačuvan i commit-ovan korisnik: " + osoba.getKorisnickoIme());
            return osoba;

        } catch (SQLException e) {
            System.err.println("SQL Greška! Pokrećem rollback... -> " + e.getMessage());
            
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback nije uspeo: " + ex.getMessage());
                }
            }
            
            return null;
        } finally {
           
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    public List<Osoba> findAll(StanicaRepository stanicaRepo) {
        String sql = "SELECT id FROM osoba";
        List<Osoba> osobe = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                findById(rs.getLong("id"), stanicaRepo).ifPresent(osobe::add);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return osobe;
    }

    public Optional<Osoba> findByUsernameAndPassword(String username, String password, StanicaRepository stanicaRepo) {
        String sql = "SELECT id FROM osoba WHERE korisnicko_ime = ? AND lozinka = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return findById(rs.getLong("id"), stanicaRepo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Osoba> findById(Long id, StanicaRepository stanicaRepo) {
        String sql = "SELECT * FROM osoba WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Uloga uloga = Uloga.valueOf(rs.getString("uloga"));
                Osoba osoba = null;
                long idStanice = rs.getLong("id_stanice");
                String ime = rs.getString("ime");
                String prezime = rs.getString("prezime");
                String korIme = rs.getString("korisnicko_ime");
                String lozinka = rs.getString("lozinka");

                switch (uloga) {
                    case ADMIN:
                        osoba = new Admin(ime, prezime, korIme, lozinka);
                        break;
                    case RADNIK:
                        NaplatnaStanica stanica = null;
                        if (idStanice != 0) {
                            stanica = stanicaRepo.findById(idStanice);
                        }
                        osoba = new Radnik(ime, prezime, korIme, lozinka, stanica);
                        break;
                    case MENADZER:
                        osoba = new Menadzer(ime, prezime, korIme, lozinka, stanicaRepo.findAll());
                        break;
                }

                if (osoba != null) {
                    osoba.setId(rs.getLong("id"));
                    return Optional.of(osoba);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM osoba WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}