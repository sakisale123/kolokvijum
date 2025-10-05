package com.naplatnarampa.repository;

import com.naplatnarampa.model.NaplatnaStanica;
import com.naplatnarampa.model.NaplatnaTraka;
import com.naplatnarampa.model.enums.Status;
import com.naplatnarampa.model.enums.TrenutniRezim;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrakaRepository {

    public NaplatnaTraka save(NaplatnaTraka traka) {
        String sql = "INSERT INTO naplatna_traka(id_stanice, rezim, status) VALUES(?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, traka.getNaplatnaStanica().getId());
            pstmt.setString(2, traka.getTrenutniRezim().toString());
            pstmt.setString(3, traka.getStatus().toString());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                traka.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return traka;
    }

    public List<NaplatnaTraka> findByStanicaId(Long stanicaId, StanicaRepository stanicaRepo) {
        String sql = "SELECT id, rezim, status, id_stanice FROM naplatna_traka WHERE id_stanice = ?";
        List<NaplatnaTraka> trake = new ArrayList<>();
        NaplatnaStanica stanica = stanicaRepo.findById(stanicaId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, stanicaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                if (stanica != null) {
                    NaplatnaTraka traka = new NaplatnaTraka(
                        stanica,
                        TrenutniRezim.valueOf(rs.getString("rezim")),
                        Status.valueOf(rs.getString("status"))
                    );
                    traka.setId(rs.getLong("id"));
                    trake.add(traka);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trake;
    }
    
    
    public Optional<NaplatnaTraka> findById(Long id, StanicaRepository stanicaRepo) {
        String sql = "SELECT * FROM naplatna_traka WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Long idStanice = rs.getLong("id_stanice");
                NaplatnaStanica stanica = stanicaRepo.findById(idStanice);
                
                if (stanica != null) {
                    NaplatnaTraka traka = new NaplatnaTraka(
                        stanica,
                        TrenutniRezim.valueOf(rs.getString("rezim")),
                        Status.valueOf(rs.getString("status"))
                    );
                    traka.setId(rs.getLong("id"));
                    return Optional.of(traka);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    

    public void update(NaplatnaTraka traka) {
        String sql = "UPDATE naplatna_traka SET rezim = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, traka.getTrenutniRezim().toString());
            pstmt.setString(2, traka.getStatus().toString());
            pstmt.setLong(3, traka.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}