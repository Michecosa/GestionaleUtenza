package GestionaleUtenza.main;

import GestionaleUtenza.dao.*;
import GestionaleUtenza.db.DatabaseConnection;
import GestionaleUtenza.decorator.*;
import GestionaleUtenza.model.*;
import GestionaleUtenza.observer.*;
import GestionaleUtenza.strategy.*;

import java.sql.SQLException;
import java.util.*;

public class Main {

  public static void main(String[] args) throws SQLException {
    if (DatabaseConnection.getInstance().getConnection() == null) {
      System.out.println("Connessione al database fallita");
      return;
    }

    try (Scanner sc = new Scanner(System.in)) {
      // Inizializzazione DAO
      UtenteDAO utenteDAO = new UtenteDAO();
      ProdottoDAO prodottoDAO = new ProdottoDAO();
      OrdineDAO ordineDAO = new OrdineDAO();
      SpedizioneDAO spedizioneDAO = new SpedizioneDAO();
      PagamentoDAO pagamentoDAO = new PagamentoDAO();

      // Configurazione Gestore Ordini con Observer
      GestoreOrdini gestore = new GestoreOrdini(ordineDAO);
      gestore.aggiungiObserver(new LogOrdineObserver());
      gestore.aggiungiObserver(new NotificaEmailObserver());
      gestore.aggiungiObserver(new AggiornaStockObserver(prodottoDAO));

      while (true) {
        System.out.println("\n--- GESTIONALE UTENZA ---");
        System.out.println("1. Login\n2. Registrati\n0. Esci");
        System.out.print("Scelta: ");

        int scelta = Integer.parseInt(sc.nextLine());
        if (scelta == 0)
          break;

        Utente utente = null;

        if (scelta == 1) {
          utente = eseguiLogin(sc, utenteDAO);
        } else if (scelta == 2) {
          utente = eseguiRegistrazione(sc, utenteDAO);
        }

        if (utente == null)
          continue;

        // Routing Menu in base al ruolo
        if (utente.getLivelloRuolo() >= 1) {
          menuAdmin(sc, utente, utenteDAO, prodottoDAO, ordineDAO, spedizioneDAO, pagamentoDAO, gestore);
        } else {
          menuUtente(sc, utente, prodottoDAO, ordineDAO, pagamentoDAO, gestore);
        }
      }
    }
  }

  // --- LOGICA DI AUTENTICAZIONE ---

  private static Utente eseguiLogin(Scanner sc, UtenteDAO utenteDAO) throws SQLException {
    System.out.print("Email: ");
    String em = sc.nextLine();
    System.out.print("Pass: ");
    String pw = sc.nextLine();

    Optional<Utente> opt = utenteDAO.findByEmail(em);
    if (opt.isPresent() && opt.get().isAttivo() && opt.get().getPasswordHash().equals(pw)) {
      return opt.get();
    } else {
      System.out.println("Errore login: credenziali errate o account disabilitato.");
      return null;
    }
  }

  private static Utente eseguiRegistrazione(Scanner sc, UtenteDAO utenteDAO) throws SQLException {
    Utente u = new Utente();
    System.out.print("Nome: ");
    u.setNome(sc.nextLine());
    System.out.print("Email: ");
    u.setEmail(sc.nextLine());
    System.out.print("Pass: ");
    u.setPasswordHash(sc.nextLine());
    u.setTipoAccount("Normal");
    u.setFkRuolo(1);
    u.setAttivo(true);

    if (utenteDAO.insert(u) != -1) {
      return utenteDAO.findByEmail(u.getEmail()).orElse(null);
    }
    return null;
  }

  // --- MENU UTENTE ---

  static void menuUtente(Scanner sc, Utente u, ProdottoDAO pDAO, OrdineDAO oDAO, PagamentoDAO pagDAO,
      GestoreOrdini gest) throws SQLException {
    while (true) {
      System.out.println("\n--- MENU UTENTE [" + u.getNome() + "] ---");
      System.out.println("1. Visualizza Prodotti\n2. Crea Nuovo Ordine\n3. I Miei Ordini\n0. Logout");
      int s = Integer.parseInt(sc.nextLine());

      if (s == 0)
        break;

      switch (s) {
        case 1 -> pDAO.findAllAttivi().forEach(System.out::println);
        case 2 -> gestisciCreazioneOrdine(sc, u, pDAO, oDAO, pagDAO, gest);
        case 3 -> oDAO.findByUtente(u.getIdUtente()).forEach(System.out::println);
      }
    }
  }

  private static void gestisciCreazioneOrdine(Scanner sc, Utente u, ProdottoDAO pDAO, OrdineDAO oDAO,
      PagamentoDAO pagDAO, GestoreOrdini gest) throws SQLException {
    List<Riga> carrello = new ArrayList<>();

    // Selezione Prodotti
    while (true) {
      pDAO.findAllAttivi().forEach(System.out::println);
      System.out.print("ID Prodotto (0 per terminare): ");
      int id = Integer.parseInt(sc.nextLine());
      if (id == 0)
        break;

      Prodotto p = pDAO.findById(id).orElse(null);
      if (p == null)
        continue;

      // Decorator Pattern per Garanzia
      ProdottoConfigurabile c = new ProdottoBase(p);
      System.out.print("Aggiungere Garanzia? (s/n): ");
      if (sc.nextLine().equalsIgnoreCase("s")) {
        c = new AggiuntaGaranzia(c);
      }

      System.out.print("Quantità: ");
      int q = Integer.parseInt(sc.nextLine());
      carrello.add(new Riga(c, p.getIdProdotto(), q));
    }

    if (carrello.isEmpty())
      return;

    // Selezione Spedizione
    List<TipoSpedizione> tipi = oDAO.findTipiSpedizione();
    tipi.forEach(System.out::println);
    System.out.print("Seleziona ID Spedizione: ");
    int idT = Integer.parseInt(sc.nextLine());
    TipoSpedizione ts = tipi.stream().filter(t -> t.getIdTipoSped() == idT).findFirst().orElse(null);

    // Calcolo Totale e Strategia Sconto
    double lordo = (ts != null) ? ts.getCosto() : 0;
    for (Riga r : carrello)
      lordo += r.c.getPrezzo() * r.q;

    ScontoStrategy strat = "Pro".equals(u.getTipoAccount()) ? new ScontoProUtente() : new ScontoNessuno();
    double tot = lordo - strat.calcola(lordo);

    // Salvataggio Ordine
    Ordine ord = new Ordine();
    ord.setFkUtente(u.getIdUtente());
    ord.setStato("Creato");
    ord.setPrezzoTotale(tot);
    int idO = oDAO.insert(ord);

    // Salvataggio Dettagli
    for (Riga r : carrello) {
      DettaglioOrdine d = new DettaglioOrdine();
      d.setFkOrdine(idO);
      d.setFkProdotto(r.idP);
      d.setDescrizioneConfigurata(r.c.getDescrizione());
      d.setPrezzoUnitario(r.c.getPrezzo());
      d.setQuantita(r.q);
      oDAO.insertDettaglio(d);
    }

    // Pagamento
    System.out.println("Ordine #" + idO + " creato con successo. Totale: " + tot + " EUR. Pagare ora? (s/n)");
    if (sc.nextLine().equalsIgnoreCase("s")) {
      Pagamento p = new Pagamento();
      p.setFkOrdine(idO);
      p.setImporto(tot);
      p.setMetodo("Carta");
      p.setStato("OK");
      pagDAO.insert(p);
      gest.cambiaStato(ord, "Pagato");
    }
  }

  // --- MENU ADMIN ---

  static void menuAdmin(Scanner sc, Utente a, UtenteDAO uDAO, ProdottoDAO pDAO, OrdineDAO oDAO, SpedizioneDAO sDAO,
      PagamentoDAO pagDAO, GestoreOrdini gest) throws SQLException {
    while (true) {
      System.out.println("\n--- MENU AMMINISTRATORE ---");
      System.out.println("1. Gestione Prodotti\n2. Gestione Ordini\n0. Logout");
      int s = Integer.parseInt(sc.nextLine());

      if (s == 0)
        break;

      if (s == 1) {
        pDAO.findAll().forEach(System.out::println);
        System.out.print("1. Aggiungi Prodotto, 0. Torna indietro: ");
        if (sc.nextLine().equals("1")) {
          Prodotto p = new Prodotto();
          System.out.print("Nome: ");
          p.setNome(sc.nextLine());
          System.out.print("Prezzo Base: ");
          p.setPrezzoBase(Double.parseDouble(sc.nextLine()));
          System.out.print("Stock: ");
          p.setStock(Integer.parseInt(sc.nextLine()));
          p.setAttivo(true);
          pDAO.insert(p);
        }
      } else if (s == 2) {
        oDAO.findAll().forEach(System.out::println);
        System.out.print("ID ordine per cambio stato: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("Inserisci Nuovo stato (In Spedizione, Consegnato): ");
        String nuovoStato = sc.nextLine();

        Optional<Ordine> ordOpt = oDAO.findById(id);
        if (ordOpt.isPresent()) {
          gest.cambiaStato(ordOpt.get(), nuovoStato);
        } else {
          System.out.println("Ordine non trovato.");
        }
      }
    }
  }

  // --- CLASSI DI SUPPORTO ---

  static class Riga {
    ProdottoConfigurabile c;
    int idP, q;

    Riga(ProdottoConfigurabile c, int idP, int q) {
      this.c = c;
      this.idP = idP;
      this.q = q;
    }
  }
}