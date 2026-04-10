package GestionaleUtenza.dao;

import GestionaleUtenza.db.DatabaseConnection;
import GestionaleUtenza.model.Spedizione;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpedizioneDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public int insert(Spedizione s) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "INSERT INTO Spedizioni (fk_ordine, codice_tracking, stato) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt   (1, s.getFkOrdine());
            ps.setString(2, s.getCodiceTracking());
            ps.setString(3, s.getStato());
            ps.executeUpdate();
            ResultSet gen = ps.getGeneratedKeys();
            if (gen.next()) return gen.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void aggiornaStato(int idSpedizione, String stato) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE Spedizioni SET stato=? WHERE id_spedizione=?");
            ps.setString(1, stato);
            ps.setInt   (2, idSpedizione);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Spedizione> findAll() {
        List<Spedizione> lista = new ArrayList<>();
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Spedizioni ORDER BY id_spedizione DESC");
            while (rs.next()) {
                Spedizione s = new Spedizione();
                s.setIdSpedizione  (rs.getInt("id_spedizione"));
                s.setFkOrdine      (rs.getInt("fk_ordine"));
                s.setCodiceTracking(rs.getString("codice_tracking"));
                s.setStato         (rs.getString("stato"));
                Timestamp tp = rs.getTimestamp("data_partenza");
                if (tp != null) s.setDataPartenza(tp.toLocalDateTime());
                Timestamp tc = rs.getTimestamp("data_consegna");
                if (tc != null) s.setDataConsegna(tc.toLocalDateTime());
                lista.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}