package com.naplatnarampa.repository;

import com.naplatnarampa.model.NaplatnaStanica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StanicaRepository {

    public NaplatnaStanica save(NaplatnaStanica stanica) {
        String sql = "INSERT INTO naplatna_stanica(naziv, lokacija) VALUES(?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, stanica.getNaziv());
            pstmt.setString(2, stanica.getLokacija());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                stanica.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stanica;
    }

    public List<NaplatnaStanica> findAll() {
        String sql = "SELECT id, naziv, lokacija FROM naplatna_stanica";
        List<NaplatnaStanica> stanice = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                NaplatnaStanica stanica = new NaplatnaStanica(rs.getString("naziv"), rs.getString("lokacija"));
                stanica.setId(rs.getLong("id"));
                stanice.add(stanica);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stanice;
    }
    
    public NaplatnaStanica findById(Long id) {
        String sql = "SELECT id, naziv, lokacija FROM naplatna_stanica WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                NaplatnaStanica stanica = new NaplatnaStanica(rs.getString("naziv"), rs.getString("lokacija"));
                stanica.setId(rs.getLong("id"));
                return stanica;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public void deleteById(Long id) {
        String sql = "DELETE FROM naplatna_stanica WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            
            System.err.println("Brisanje stanice nije uspelo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}