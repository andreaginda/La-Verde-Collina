package com.example.demo.service;

import com.example.demo.dto.CampoStatsDTO;
import com.example.demo.dto.DashboardDTO;
import com.example.demo.entity.Campo;
import com.example.demo.entity.Rilevazione;
import com.example.demo.enums.TipoSensore;
import com.example.demo.repository.CampoRepository;
import com.example.demo.repository.RilevazioneRepository;
import com.example.demo.repository.SensoreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service che aggrega i dati per la vista principale (Dashboard Overview).
 * Fornisce un riassunto dello stato di tutti i campi.
 */
@Service
public class DashboardServizio {
    private final CampoRepository campoRepo;
    private final SensoreRepository sensRepo;
    private final RilevazioneRepository rilRepo;

    public DashboardServizio(CampoRepository c, SensoreRepository s, RilevazioneRepository r) {
        this.campoRepo = c; this.sensRepo = s; this.rilRepo = r;
    }

    /**
     * Genera il DTO principale per la dashboard.
     * Per ogni campo, calcola lo stato attuale e recupera l'ultima temperatura rilevata.
     */
    public DashboardDTO overview() {
        List<Campo> campi = campoRepo.findAll();

        // Trasforma ogni Entità Campo in un DTO leggero con le statistiche chiave
        List<CampoStatsDTO> stats = campi.stream().map(c -> {
            // 1. Recupera i sensori attivi per questo campo
            var sensori = sensRepo.findByCampoIdAndAttivoTrue(c.getId());

            // 2. Cerca specificamente il sensore di Temperatura Aria (KPI principale)
            var tempAirSensore = sensori.stream()
                    .filter(s -> s.getTipo() == TipoSensore.TEMP_AIR)
                    .findFirst();

            // 3. Estrae l'ultima rilevazione disponibile per quel sensore
            // Usa una catena di Optional per gestire in sicurezza l'assenza di dati
            Double tempVal = tempAirSensore
                    .map(s -> rilRepo.findTop96BySensoreIdOrderByTimestampDesc(s.getId())) // Prende la lista storica
                    .flatMap(list -> list.stream().findFirst()) // Prende il primo elemento (il più recente)
                    .map(Rilevazione::getValore) // Estrae il valore numerico
                    .orElse(null); // Se non c'è sensore o dati, ritorna null

            // 4. Calcolo del bilancio economico
            // Definisce il bilancio come Ricavi stimati - Costi accumulati
            Double bilancioEconomico = c.getRicaviStimati() - c.getCostiAccumulati();

            return new CampoStatsDTO(c.getNome(), c.getStato(), tempVal, c.getProduzioneKg(), bilancioEconomico);
        }).toList();

        return new DashboardDTO(stats);
    }
}

