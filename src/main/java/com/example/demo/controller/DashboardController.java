package com.example.demo.controller;

import org.springframework.ui.Model;
import com.example.demo.service.DashboardServizio;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller dedicato alla gestione delle richieste HTTP per la visualizzazione
 * dell'interfaccia utente principale (Dashboard).
 * Utilizza Thymeleaf per il rendering.
 */
@Controller
public class DashboardController {

    /**
     * Servizio iniettato per l'accesso alla logica di business e ai dati aggregati.
     * Deve essere final per garantire l'immutabilità dopo l'inizializzazione.
     */
    private final DashboardServizio dashboardServizio;

    /**
     * Costruttore per l'iniezione delle dipendenze (Dependency Injection).
     * Spring Boot (tramite lo "Stereotype Controller") riconosce automaticamente
     * il costruttore e inietta l'istanza di DashboardServizio.
     * * @param dashboardServizio L'istanza del servizio di Dashboard.
     */
    public DashboardController(DashboardServizio dashboardServizio) {
        this.dashboardServizio = dashboardServizio;
    }

    /**
     * Gestisce la richiesta GET sulla root dell'applicazione ("http://localhost:8080/").
     *
     * 1. Recupera il DashboardDTO contenente i dati aggregati tramite DashboardServizio.
     * 2. Aggiunge questi dati all'oggetto Model per renderli accessibili al template.
     * * @param model L'oggetto Model per il trasferimento dei dati alla vista.
     * @return Il nome del template Thymeleaf da renderizzare (risolve in
     * src/main/resources/templates/dashboard.html).
     */
    @GetMapping("/")
    public String overview(Model model) {
        // Il dato recuperato sarà disponibile nel template come variabile 'data'
        model.addAttribute("data", dashboardServizio.overview());
        return "dashboard";
    }
}