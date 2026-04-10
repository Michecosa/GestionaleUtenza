package GestionaleUtenza.strategy;

/** Strategy: algoritmo di calcolo dello sconto */
public interface ScontoStrategy {
    double calcola(double prezzoLordo);
    String descrizione();
}
