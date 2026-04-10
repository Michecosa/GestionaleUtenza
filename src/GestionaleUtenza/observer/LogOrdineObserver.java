package GestionaleUtenza.observer;

import GestionaleUtenza.model.Ordine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Scrive su file (ordini.log) e su console ogni cambio di stato */
public class LogOrdineObserver implements OrdineObserver {

    private static final String LOG_FILE = "ordini.log";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void aggiorna(Ordine ordine, String nuovoStato) {
        String riga = String.format("[%s] Ordine #%d  utente:%d  %s -> %s  totale: EUR%.2f",
                LocalDateTime.now().format(FMT),
                ordine.getIdOrdine(),
                ordine.getFkUtente(),
                ordine.getStato(),
                nuovoStato,
                ordine.getPrezzoTotale());

        System.out.println("[LOG] " + riga);

        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            pw.println(riga);
        } catch (IOException e) {
            System.err.println("[LOG] Impossibile scrivere su " + LOG_FILE + ": " + e.getMessage());
        }
    }
}
