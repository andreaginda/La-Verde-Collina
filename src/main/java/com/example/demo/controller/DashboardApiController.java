package com.example.demo.controller;

import com.example.demo.service.RilevazioneServizio; //
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final RilevazioneServizio rilevazioneServizio;

    public DashboardApiController(RilevazioneServizio rilevazioneServizio) {
        this.rilevazioneServizio = rilevazioneServizio;
    }

    /**
     * Endpoint API chiamato via AJAX dalla dashboard per popolare il grafico dinamico.
     * @param campoId L'ID del campo selezionato (proveniente dal frontend).
     * @return Dati formattati per Chart.js (Labels, Datasets).
     */
    @GetMapping("/temp-umidita")
    public Map<String, Object> getChartData(@RequestParam Long campoId) {
        // Chiama il nuovo servizio per ottenere i dati aggregati e formattati
        return rilevazioneServizio.seriesTempUmiditaByCampo(campoId);
    }
}

