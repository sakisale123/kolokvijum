package com.naplatnarampa.repository;

import com.naplatnarampa.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransakcijaRepository {

    public Transakcija save(Transakcija transakcija) {
        String sql = "INSERT INTO transakcija(id_vozila, id_kategorije, id_ulazne_stanice, id_radnika_ulaz, vreme_ulaza, zavrsena) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, transakcija.getVozilo().getId());
            pstmt.setLong(2, transakcija.getKategorijaVozila().getId());
            pstmt.setLong(3, transakcija.getUlaznaStanica().getId());
            pstmt.setLong(4, transakcija.getRadnikUlaz().getId());
            pstmt.setTimestamp(5, Timestamp.valueOf(transakcija.getVremeUlaza()));
            pstmt.setBoolean(6, false);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                transakcija.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transakcija;
    }
    
    public Transakcija update(Transakcija transakcija) {
        String sql = "UPDATE transakcija SET id_izlazne_stanice = ?, id_radnika_izlaz = ?, vreme_izlaza = ?, naplaceni_iznos = ?, zavrsena = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, transakcija.getIzlaznaStanica().getId());
            pstmt.setLong(2, transakcija.getRadnikIzlaz().getId());
            pstmt.setTimestamp(3, Timestamp.valueOf(transakcija.getVremeIzlaza()));
            pstmt.setFloat(4, transakcija.getNaplaceniIznos());
            pstmt.setBoolean(5, true);
            pstmt.setLong(6, transakcija.getId());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transakcija;
    }
    
    public Optional<Transakcija> findActiveByTablica(String tablica, VoziloRepository voziloRepo, StanicaRepository stanicaRepo, KategorijaVozilaRepository katRepo, KorisnikRepository korRepo) {
        String sql = "SELECT t.* FROM transakcija t JOIN vozilo v ON t.id_vozila = v.id WHERE v.registarska_tablica = ? AND t.zavrsena = false";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tablica);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Transakcija t = buildTransakcijaFromResultSet(rs, voziloRepo, stanicaRepo, katRepo, korRepo);
                return Optional.of(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Transakcija> findActive(VoziloRepository voziloRepo, StanicaRepository stanicaRepo, KategorijaVozilaRepository katRepo, KorisnikRepository korRepo) {
        String sql = "SELECT * FROM transakcija WHERE zavrsena = false";
        List<Transakcija> transakcije = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transakcije.add(buildTransakcijaFromResultSet(rs, voziloRepo, stanicaRepo, katRepo, korRepo));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transakcije;
    }

    public List<Transakcija> findAll(VoziloRepository voziloRepo, StanicaRepository stanicaRepo, KategorijaVozilaRepository katRepo, KorisnikRepository korRepo) {
        String sql = "SELECT * FROM transakcija";
        List<Transakcija> transakcije = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transakcije.add(buildTransakcijaFromResultSet(rs, voziloRepo, stanicaRepo, katRepo, korRepo));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transakcije;
    }

    
    private Transakcija buildTransakcijaFromResultSet(ResultSet rs, VoziloRepository voziloRepo, StanicaRepository stanicaRepo, KategorijaVozilaRepository katRepo, KorisnikRepository korRepo) throws SQLException {
        
        Vozilo vozilo = voziloRepo.findById(rs.getLong("id_vozila")).orElse(null);
        KategorijaVozila kategorija = katRepo.findById(rs.getLong("id_kategorije")).orElse(null);
        NaplatnaStanica ulaznaStanica = stanicaRepo.findById(rs.getLong("id_ulazne_stanice"));
        Radnik radnikUlaz = (Radnik) korRepo.findById(rs.getLong("id_radnika_ulaz"), stanicaRepo).orElse(null);

        
        Transakcija t = new Transakcija(vozilo, ulaznaStanica, radnikUlaz, kategorija);
        t.setId(rs.getLong("id"));
        t.setVremeUlaza(rs.getTimestamp("vreme_ulaza").toLocalDateTime());
        
        
        if (rs.getBoolean("zavrsena")) {
            NaplatnaStanica izlaznaStanica = stanicaRepo.findById(rs.getLong("id_izlazne_stanice"));
            Radnik radnikIzlaz = (Radnik) korRepo.findById(rs.getLong("id_radnika_izlaz"), stanicaRepo).orElse(null);
            LocalDateTime vremeIzlaza = rs.getTimestamp("vreme_izlaza") != null ? rs.getTimestamp("vreme_izlaza").toLocalDateTime() : null;
            
            t.setIzlaznaStanica(izlaznaStanica);
            t.setRadnikIzlaz(radnikIzlaz);
            t.setVremeIzlaza(vremeIzlaza);
            t.setNaplaceniIznos(rs.getFloat("naplaceni_iznos"));
            t.setZavrsena(true);
        }
        
        return t;
    }
}