package com.naplatnarampa.repository;

import com.naplatnarampa.model.Kvar;
import com.naplatnarampa.model.NaplatnaTraka;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KvarRepository {

    public Kvar save(Kvar kvar) {
        String sql = "INSERT INTO kvar(id_trake, opis, vreme_prijave) VALUES(?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, kvar.getNaplatnaTraka().getId());
            pstmt.setString(2, kvar.getOpis());
            pstmt.setTimestamp(3, Timestamp.valueOf(kvar.getVremePrijave()));
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                kvar.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kvar;
    }

    public List<Kvar> findAll(TrakaRepository trakaRepo, StanicaRepository stanicaRepo) {
        String sql = "SELECT * FROM kvar";
        List<Kvar> kvarovi = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                
                Long idTrake = rs.getLong("id_trake");
                NaplatnaTraka traka = trakaRepo.findById(idTrake, stanicaRepo).orElse(null);
                
                if (traka != null) {
                    String opis = rs.getString("opis");
                    LocalDateTime vremePrijave = rs.getTimestamp("vreme_prijave").toLocalDateTime();
                    
                    Kvar kvar = new Kvar(traka, opis);
                    kvar.setId(rs.getLong("id"));
                    kvar.setVremePrijave(vremePrijave); 
                    
                    kvarovi.add(kvar);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kvarovi;
    }
}