package com.naplatnarampa.repository;

import com.naplatnarampa.model.Vozilo;
import java.sql.*;
import java.util.Optional;

public class VoziloRepository {
    
    public Vozilo saveOrUpdate(Vozilo vozilo) {
        Optional<Vozilo> postojeceVozilo = findByTablica(vozilo.getRegistarskaTablica());
        if (postojeceVozilo.isPresent()) {
            return postojeceVozilo.get();
        }

        String sql = "INSERT INTO vozilo(registarska_tablica) VALUES(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, vozilo.getRegistarskaTablica());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                vozilo.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vozilo;
    }

    public Optional<Vozilo> findByTablica(String tablica) {
        String sql = "SELECT id, registarska_tablica FROM vozilo WHERE registarska_tablica = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tablica);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Vozilo vozilo = new Vozilo(rs.getString("registarska_tablica"));
                vozilo.setId(rs.getLong("id"));
                return Optional.of(vozilo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    
    public Optional<Vozilo> findById(Long id) {
        String sql = "SELECT id, registarska_tablica FROM vozilo WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Vozilo vozilo = new Vozilo(rs.getString("registarska_tablica"));
                vozilo.setId(rs.getLong("id"));
                return Optional.of(vozilo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}