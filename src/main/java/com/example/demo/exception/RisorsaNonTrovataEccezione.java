package com.example.demo.exception;

/**
 * Eccezione custom lanciata quando una risorsa (es. Campo, Sensore)
 * cercata per ID non viene trovata nel database.
 * Estende RuntimeException per evitare la gestione obbligatoria (checked).
 */
public class RisorsaNonTrovataEccezione extends RuntimeException {

    // Costruttore che accetta il tipo di risorsa e l'ID che ha fallito
    public RisorsaNonTrovataEccezione(String nomeRisorsa, Long id) {
        // Chiama il costruttore della classe base (RuntimeException) con un messaggio descrittivo
        super(nomeRisorsa + " non trovata con ID: " + id);
    }
}
