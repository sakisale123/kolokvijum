package com.naplatnarampa.repository;

import com.naplatnarampa.model.Cenovnik;
import com.naplatnarampa.model.NaplatnaStanica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CenovnikRepository {

    public Cenovnik save(Cenovnik cenovnik) {
        String sql;
        if (cenovnik.isDeonica()) {
            sql = "INSERT INTO cenovnik(id_ulazne_stanice, id_izlazne_stanice, kilometraza) VALUES(?,?,?)";
        } else {
            sql = "INSERT INTO cenovnik(cena_po_km) VALUES(?)";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            if (cenovnik.isDeonica()) {
                pstmt.setLong(1, cenovnik.getUlaznaStanica().getId());
                pstmt.setLong(2, cenovnik.getIzlaznaStanica().getId());
                pstmt.setDouble(3, cenovnik.getKilometraza());
            } else {
                pstmt.setFloat(1, cenovnik.getCenaPoKilometru());
            }
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                cenovnik.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cenovnik;
    }

    public List<Cenovnik> findAll(StanicaRepository stanicaRepo) {
        String sql = "SELECT * FROM cenovnik";
        List<Cenovnik> cenovnici = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                Cenovnik cenovnik;
                long idUlazne = rs.getLong("id_ulazne_stanice");
                if (idUlazne != 0) { 
                    NaplatnaStanica ulaz = stanicaRepo.findById(idUlazne);
                    NaplatnaStanica izlaz = stanicaRepo.findById(rs.getLong("id_izlazne_stanice"));
                    cenovnik = new Cenovnik(ulaz, izlaz, rs.getDouble("kilometraza"));
                } else { 
                    cenovnik = new Cenovnik(rs.getFloat("cena_po_km"));
                }
                cenovnik.setId(rs.getLong("id"));
                cenovnici.add(cenovnik);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cenovnici;
    }
    
    public void deleteById(Long id) {
        String sql = "DELETE FROM cenovnik WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}