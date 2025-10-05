package com.naplatnarampa.repository;

import com.naplatnarampa.model.KategorijaVozila;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KategorijaVozilaRepository {

    public KategorijaVozila save(KategorijaVozila kategorija) {
        String sql = "INSERT INTO kategorija_vozila(naziv, koeficijent_cene) VALUES(?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, kategorija.getNaziv());
            pstmt.setFloat(2, kategorija.getKoeficijentCene());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                kategorija.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategorija;
    }

    public List<KategorijaVozila> findAll() {
        String sql = "SELECT id, naziv, koeficijent_cene FROM kategorija_vozila";
        List<KategorijaVozila> kategorije = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                KategorijaVozila kategorija = new KategorijaVozila();
                kategorija.setId(rs.getLong("id"));
                kategorija.setNaziv(rs.getString("naziv"));
                kategorija.setKoeficijentCene(rs.getFloat("koeficijent_cene"));
                kategorije.add(kategorija);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategorije;
    }
    
    public Optional<KategorijaVozila> findById(Long id) {
        String sql = "SELECT id, naziv, koeficijent_cene FROM kategorija_vozila WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                KategorijaVozila kategorija = new KategorijaVozila();
                kategorija.setId(rs.getLong("id"));
                kategorija.setNaziv(rs.getString("naziv"));
                kategorija.setKoeficijentCene(rs.getFloat("koeficijent_cene"));
                return Optional.of(kategorija);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public void deleteById(Long id) {
        String sql = "DELETE FROM kategorija_vozila WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public KategorijaVozila update(KategorijaVozila kategorija) {
        String sql = "UPDATE kategorija_vozila SET naziv = ?, koeficijent_cene = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kategorija.getNaziv());
            pstmt.setFloat(2, kategorija.getKoeficijentCene());
            pstmt.setLong(3, kategorija.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategorija;
    }
}