package GestionaleUtenza.dao;

import GestionaleUtenza.db.DatabaseConnection;
import GestionaleUtenza.model.Ruolo;
import GestionaleUtenza.model.Utente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtenteDAO {

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // findByEmail 
    public Optional<Utente> findByEmail(String email) throws SQLException {
        String sql = """
                SELECT u.*, r.nome_ruolo, r.livello
                FROM Utenti u
                JOIN Ruoli r ON u.fk_ruolo = r.id_ruolo
                WHERE u.email = ?""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    // findById 
    public Optional<Utente> findById(int id) throws SQLException {
        String sql = """
                SELECT u.*, r.nome_ruolo, r.livello
                FROM Utenti u
                JOIN Ruoli r ON u.fk_ruolo = r.id_ruolo
                WHERE u.id_utente = ?""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    // findAll 
    public List<Utente> findAll() throws SQLException {
        List<Utente> lista = new ArrayList<>();
        String sql = """
                SELECT u.*, r.nome_ruolo, r.livello
                FROM Utenti u
                JOIN Ruoli r ON u.fk_ruolo = r.id_ruolo
                ORDER BY u.id_utente""";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    // insert 
    public int insert(Utente u) throws SQLException {
        String sql = """
                INSERT INTO Utenti (nome, email, password_hash, tipo_account, fk_ruolo, attivo)
                VALUES (?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getTipoAccount());
            ps.setInt(5, u.getFkRuolo());
            ps.setBoolean(6, u.isAttivo());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    // update (aggiorna nome, email, tipo, ruolo, attivo) 
    public void update(Utente u) throws SQLException {
        String sql = """
                UPDATE Utenti
                SET nome=?, email=?, tipo_account=?, fk_ruolo=?, attivo=?
                WHERE id_utente=?""";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getTipoAccount());
            ps.setInt   (4, u.getFkRuolo());
            ps.setBoolean(5, u.isAttivo());
            ps.setInt   (6, u.getIdUtente());
            ps.executeUpdate();
        }
    }

    // updatePassword 
    public void updatePassword(int idUtente, String nuovoHash) throws SQLException {
        String sql = "UPDATE Utenti SET password_hash=? WHERE id_utente=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, nuovoHash);
            ps.setInt   (2, idUtente);
            ps.executeUpdate();
        }
    }

    // disabilita (soft delete) 
    public void disabilita(int idUtente) throws SQLException {
        String sql = "UPDATE Utenti SET attivo=FALSE WHERE id_utente=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.executeUpdate();
        }
    }

    // promuovi ruolo 
    public void aggiornaRuolo(int idUtente, int idRuolo) throws SQLException {
        String sql = "UPDATE Utenti SET fk_ruolo=? WHERE id_utente=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idRuolo);
            ps.setInt(2, idUtente);
            ps.executeUpdate();
        }
    }

    // tutti i ruoli 
    public List<Ruolo> findAllRuoli() throws SQLException {
        List<Ruolo> lista = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM Ruoli ORDER BY livello")) {
            while (rs.next()) {
                Ruolo r = new Ruolo();
                r.setIdRuolo (rs.getInt("id_ruolo"));
                r.setNomeRuolo(rs.getString("nome_ruolo"));
                r.setLivello (rs.getInt("livello"));
                lista.add(r);
            }
        }
        return lista;
    }

    // mapping ResultSet → Utente 
    private Utente mapRow(ResultSet rs) throws SQLException {
        Utente u = new Utente();
        u.setIdUtente (rs.getInt("id_utente"));
        u.setNome (rs.getString("nome"));
        u.setEmail (rs.getString("email"));
        u.setPasswordHash (rs.getString("password_hash"));
        u.setTipoAccount (rs.getString("tipo_account"));
        u.setFkRuolo (rs.getInt("fk_ruolo"));
        u.setAttivo (rs.getBoolean("attivo"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());

        Ruolo r = new Ruolo();
        r.setIdRuolo  (rs.getInt("fk_ruolo"));
        r.setNomeRuolo(rs.getString("nome_ruolo"));
        r.setLivello  (rs.getInt("livello"));
        u.setRuolo(r);
        return u;
    }
}
