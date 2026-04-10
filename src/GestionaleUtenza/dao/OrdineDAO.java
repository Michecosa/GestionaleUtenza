package GestionaleUtenza.dao;

import GestionaleUtenza.db.DatabaseConnection;
import GestionaleUtenza.model.DettaglioOrdine;
import GestionaleUtenza.model.Ordine;
import GestionaleUtenza.model.TipoSpedizione;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdineDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public int insert(Ordine o) {
        String sql = "INSERT INTO Ordini (stato, prezzo_totale, sconto_applicato, " +
                     "indirizzo_spedizione, fk_utente, fk_tipo_spedizione) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, o.getStato());
            ps.setDouble(2, o.getPrezzoTotale());
            ps.setDouble(3, o.getScontoApplicato());
            ps.setString(4, o.getIndirizzoSpedizione());
            ps.setInt   (5, o.getFkUtente());
            if (o.getFkTipoSpedizione() != null) ps.setInt(6, o.getFkTipoSpedizione());
            else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
            ResultSet gen = ps.getGeneratedKeys();
            if (gen.next()) return gen.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public int insertDettaglio(DettaglioOrdine d) {
        String sql = "INSERT INTO Dettagli_Ordine " +
                     "(fk_ordine, fk_prodotto, descrizione_configurata, prezzo_unitario, quantita) " +
                     "VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt   (1, d.getFkOrdine());
            if (d.getFkProdotto() != null) ps.setInt(2, d.getFkProdotto());
            else ps.setNull(2, Types.INTEGER);
            ps.setString(3, d.getDescrizioneConfigurata());
            ps.setDouble(4, d.getPrezzoUnitario());
            ps.setInt   (5, d.getQuantita());
            ps.executeUpdate();
            ResultSet gen = ps.getGeneratedKeys();
            if (gen.next()) return gen.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void aggiornaStato(int idOrdine, String stato) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE Ordini SET stato=? WHERE id_ordine=?");
            ps.setString(1, stato);
            ps.setInt   (2, idOrdine);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Optional<Ordine> findById(int id) {
        String sql = "SELECT o.*, ts.nome AS ts_nome, ts.corriere, ts.costo, ts.giorni_stima " +
                     "FROM Ordini o " +
                     "LEFT JOIN Tipi_Spedizione ts ON o.fk_tipo_spedizione = ts.id_tipo_sped " +
                     "WHERE o.id_ordine = ?";
        try {
            PreparedStatement ps = getConn().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Ordine o = mapRow(rs);
                o.setDettagli(findDettagliByOrdine(id));
                return Optional.of(o);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    public List<Ordine> findByUtente(int idUtente) {
        List<Ordine> lista = new ArrayList<>();
        String sql = "SELECT o.*, ts.nome AS ts_nome, ts.corriere, ts.costo, ts.giorni_stima " +
                     "FROM Ordini o " +
                     "LEFT JOIN Tipi_Spedizione ts ON o.fk_tipo_spedizione = ts.id_tipo_sped " +
                     "WHERE o.fk_utente = ? ORDER BY o.data_creazione DESC";
        try {
            PreparedStatement ps = getConn().prepareStatement(sql);
            ps.setInt(1, idUtente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Ordine> findAll() {
        List<Ordine> lista = new ArrayList<>();
        String sql = "SELECT o.*, ts.nome AS ts_nome, ts.corriere, ts.costo, ts.giorni_stima " +
                     "FROM Ordini o " +
                     "LEFT JOIN Tipi_Spedizione ts ON o.fk_tipo_spedizione = ts.id_tipo_sped " +
                     "ORDER BY o.data_creazione DESC";
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<DettaglioOrdine> findDettagliByOrdine(int idOrdine) {
        List<DettaglioOrdine> lista = new ArrayList<>();
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "SELECT * FROM Dettagli_Ordine WHERE fk_ordine = ?");
            ps.setInt(1, idOrdine);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DettaglioOrdine d = new DettaglioOrdine();
                d.setIdDettaglio (rs.getInt("id_dettaglio"));
                d.setFkOrdine (rs.getInt("fk_ordine"));
                int fkP = rs.getInt("fk_prodotto");
                if (!rs.wasNull()) d.setFkProdotto(fkP);
                d.setDescrizioneConfigurata(rs.getString("descrizione_configurata"));
                d.setPrezzoUnitario (rs.getDouble("prezzo_unitario"));
                d.setQuantita (rs.getInt("quantita"));
                lista.add(d);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<TipoSpedizione> findTipiSpedizione() {
        List<TipoSpedizione> lista = new ArrayList<>();
        try {
            Statement st = getConn().createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Tipi_Spedizione ORDER BY id_tipo_sped");
            while (rs.next()) {
                TipoSpedizione t = new TipoSpedizione();
                t.setIdTipoSped (rs.getInt("id_tipo_sped"));
                t.setNome (rs.getString("nome"));
                t.setCorriere (rs.getString("corriere"));
                t.setCosto (rs.getDouble("costo"));
                t.setGiorniStima(rs.getInt("giorni_stima"));
                lista.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private Ordine mapRow(ResultSet rs) throws SQLException {
        Ordine o = new Ordine();
        o.setIdOrdine           (rs.getInt("id_ordine"));
        Timestamp ts = rs.getTimestamp("data_creazione");
        if (ts != null) o.setDataCreazione(ts.toLocalDateTime());
        o.setStato (rs.getString("stato"));
        o.setPrezzoTotale (rs.getDouble("prezzo_totale"));
        o.setScontoApplicato (rs.getDouble("sconto_applicato"));
        o.setIndirizzoSpedizione(rs.getString("indirizzo_spedizione"));
        o.setFkUtente  (rs.getInt("fk_utente"));
        int fkTs = rs.getInt("fk_tipo_spedizione");
        if (!rs.wasNull()) {
            o.setFkTipoSpedizione(fkTs);
            TipoSpedizione tipo = new TipoSpedizione();
            tipo.setIdTipoSped (fkTs);
            tipo.setNome (rs.getString("ts_nome"));
            tipo.setCorriere (rs.getString("corriere"));
            tipo.setCosto (rs.getDouble("costo"));
            tipo.setGiorniStima(rs.getInt("giorni_stima"));
            o.setTipoSpedizione(tipo);
        }
        return o;
    }
}