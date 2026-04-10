package GestionaleUtenza.observer;

import GestionaleUtenza.model.Ordine;

/** Observer: riceve notifiche sui cambi di stato degli ordini */
public interface OrdineObserver {
    void aggiorna(Ordine ordine, String nuovoStato);
}
