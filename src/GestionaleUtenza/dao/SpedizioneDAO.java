package GestionaleUtenza.dao;

import GestionaleUtenza.db.DatabaseConnection;
import GestionaleUtenza.model.Spedizione;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpedizioneDAO {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public int insert(Spedizione s) throws SQLException {
        String sql = """
                INSERT INTO Spedizioni (fk_ordine, codice_tracking, stato)
                VALUES (?, ?, ?)""";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, s.getFkOrdine());
            ps.setString(2, s.getCodiceTracking());
            ps.setString(3, s.getStato());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public void aggiornaStato(int idSpedizione, String stato) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE Spedizioni SET stato=? WHERE id_spedizione=?")) {
            ps.setString(1, stato);
            ps.setInt   (2, idSpedizione);
            ps.executeUpdate();
        }
    }

    public Optional<Spedizione> findByOrdine(int idOrdine) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM Spedizioni WHERE fk_ordine=?")) {
            ps.setInt(1, idOrdine);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public List<Spedizione> findAll() throws SQLException {
        List<Spedizione> lista = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Spedizioni ORDER BY id_spedizione DESC")) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    private Spedizione mapRow(ResultSet rs) throws SQLException {
        Spedizione s = new Spedizione();
        s.setIdSpedizione (rs.getInt("id_spedizione"));
        s.setFkOrdine (rs.getInt("fk_ordine"));
        s.setCodiceTracking (rs.getString("codice_tracking"));
        s.setStato (rs.getString("stato"));
        Timestamp tp = rs.getTimestamp("data_partenza");
        if (tp != null) s.setDataPartenza(tp.toLocalDateTime());
        Timestamp tc = rs.getTimestamp("data_consegna");
        if (tc != null) s.setDataConsegna(tc.toLocalDateTime());
        return s;
    }
}
