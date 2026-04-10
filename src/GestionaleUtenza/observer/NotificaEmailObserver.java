package GestionaleUtenza.observer;

import GestionaleUtenza.model.Ordine;

/** Simula l'invio di una email di notifica all'utente */
public class NotificaEmailObserver implements OrdineObserver {
    @Override
    public void aggiorna(Ordine ordine, String nuovoStato) {
        System.out.printf("[EMAIL] Ordine #%d -> stato '%s'  (utente id:%d)%n",
                ordine.getIdOrdine(), nuovoStato, ordine.getFkUtente());
    }
}
