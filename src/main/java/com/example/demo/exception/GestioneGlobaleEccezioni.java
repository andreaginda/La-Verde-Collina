package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * ControllerAdvice gestisce le eccezioni globali in tutta l'applicazione,
 * permettendo di standardizzare la risposta HTTP.
 */
@ControllerAdvice
public class GestioneGlobaleEccezioni {

    /**
     * Cattura l'eccezione RisorsaNonTrovataEccezione e la mappa a una risposta HTTP 404 Not Found.
     * * @param ex L'eccezione lanciata.
     * @return ResponseEntity con lo stato 404 e un corpo JSON descrittivo.
     */
    @ExceptionHandler(RisorsaNonTrovataEccezione.class)
    public ResponseEntity<Map<String, Object>> gestisciRisorsaNonTrovata(RisorsaNonTrovataEccezione ex) {

        // Struttura JSON per la risposta 404
        Map<String, Object> body = Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "message", ex.getMessage(), // Il messaggio creato nel costruttore dell'eccezione
                "path", "/api/dashboard/temp-umidita"
        );

        // Ritorna la risposta con lo stato 404
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
