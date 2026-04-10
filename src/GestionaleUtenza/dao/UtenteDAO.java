package GestionaleUtenza.dao;

import GestionaleUtenza.db.DatabaseConnection;
import GestionaleUtenza.model.Ruolo;
import GestionaleUtenza.model.Utente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public Utente findByEmail(String email) {
        String sql = "SELECT u.*, r.nome_ruolo, r.livello " +
                     "FROM Utenti u JOIN Ruoli r ON u.fk_ruolo = r.id_ruolo " +
                     "WHERE u.email = ?";
        try {
            PreparedStatement ps = getConn().prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Utente findById(int id) {
        String sql = "SELECT u.*, r.nome_ruolo, r.livello " +
                     "FROM Utenti u JOIN Ruoli r ON u.fk_ruolo = r.id_ruolo " +
                     "WHERE u.id_utente = ?";
        try {
            PreparedStatement ps = getConn().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Utente> findAll() {
        List<Utente> lista = new ArrayList<>();
        String sql = "SELECT u.*, r.nome_ruolo, r.livello " +
                     "FROM Utenti u JOIN Ruoli r ON u.fk_ruolo = r.id_ruolo " +
                     "ORDER BY u.id_utente";
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public int insert(Utente u) {
        String sql = "INSERT INTO Utenti (nome, email, password_hash, tipo_account, fk_ruolo, attivo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getTipoAccount());
            ps.setInt(5, u.getFkRuolo());
            ps.setBoolean(6, u.isAttivo());
            ps.executeUpdate();
            ResultSet gen = ps.getGeneratedKeys();
            if (gen.next()) return gen.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void update(Utente u) {
        String sql = "UPDATE Utenti SET nome=?, email=?, tipo_account=?, fk_ruolo=?, attivo=? " +
                     "WHERE id_utente=?";
        try {
            PreparedStatement ps = getConn().prepareStatement(sql);
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getTipoAccount());
            ps.setInt(4, u.getFkRuolo());
            ps.setBoolean(5, u.isAttivo());
            ps.setInt(6, u.getIdUtente());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void aggiornaRuolo(int idUtente, int idRuolo) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE Utenti SET fk_ruolo=? WHERE id_utente=?");
            ps.setInt(1, idRuolo);
            ps.setInt(2, idUtente);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void disabilita(int idUtente) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE Utenti SET attivo=FALSE WHERE id_utente=?");
            ps.setInt(1, idUtente);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Ruolo> findAllRuoli() {
        List<Ruolo> lista = new ArrayList<>();
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Ruoli ORDER BY livello");
            while (rs.next()) {
                lista.add(new Ruolo(
                        rs.getInt("id_ruolo"),
                        rs.getString("nome_ruolo"),
                        rs.getInt("livello")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private Utente mapRow(ResultSet rs) throws SQLException {
        Utente u = new Utente();
        u.setIdUtente(rs.getInt("id_utente"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setTipoAccount(rs.getString("tipo_account"));
        u.setFkRuolo(rs.getInt("fk_ruolo"));
        u.setAttivo(rs.getBoolean("attivo"));
        Ruolo r = new Ruolo(
                rs.getInt("fk_ruolo"),
                rs.getString("nome_ruolo"),
                rs.getInt("livello"));
        u.setRuolo(r);
        return u;
    }
}
