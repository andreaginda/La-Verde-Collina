package com.example.demo.service;

import com.example.demo.entity.Rilevazione;
import com.example.demo.entity.Sensore;
import com.example.demo.enums.TipoSensore;
import com.example.demo.exception.RisorsaNonTrovataEccezione;
import com.example.demo.repository.RilevazioneRepository;
import com.example.demo.repository.SensoreRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Layer responsabile della logica di presentazione dei dati (Data Presentation).
 * Si occupa di aggregare, trasformare e normalizzare i dati grezzi provenienti dal DB
 * in strutture dati ottimizzate per la visualizzazione grafica frontend.
 */
@Service
public class RilevazioneServizio {

    private final SensoreRepository sensoreRepo;
    private final RilevazioneRepository rilevazioneRepo;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public RilevazioneServizio(SensoreRepository sensoreRepo, RilevazioneRepository rilevazioneRepo) {
        this.sensoreRepo = sensoreRepo;
        this.rilevazioneRepo = rilevazioneRepo;
    }

    /**
     * Recupera e formatta le serie storiche (Time Series) di Temperatura e Umidità.
     * Esegue un'operazione di pivot dei dati per adattarli alle specifiche della libreria Chart.js.
     *
     * @param campoId L'identificativo del campo monitorato.
     * @return Una Mappa contenente due chiavi principali:
     * - "labels": Lista temporale asse X.
     * - "datasets": Lista delle serie dati configurate per il rendering.
     */
    public Map<String, Object> seriesTempUmiditaByCampo(Long campoId) {
        // 1. **GESTIONE ECCEZIONE INIZIALE:** Verifica se il campo esiste prima di proseguire
        sensoreRepo.findByCampoIdAndAttivoTrue(campoId).stream()
                .findAny()
                .orElseThrow(() -> new RisorsaNonTrovataEccezione("Campo", campoId));
        // 2. Filtraggio Sensori: Selezione dei soli sensori di interesse per il grafico
        List<Sensore> sensoriCampo = sensoreRepo.findByCampoIdAndAttivoTrue(campoId).stream()
                .filter(s -> s.getTipo() == TipoSensore.TEMP_AIR || s.getTipo() == TipoSensore.HUMID_AIR)
                .toList();

        if (sensoriCampo.isEmpty()) {
            return Map.of("labels", List.of(), "datasets", List.of());
        }

        // 3. Data Fetching & Processing:
        // Recupero delle ultime 96 rilevazioni e inversione dell'ordine (da cronologico inverso a diretto).
        // Utilizzo di una Map per associare efficientemente i dati al sensore di origine.
        Map<Long, List<Rilevazione>> rilevazioniBySensore = sensoriCampo.stream()
                .collect(Collectors.toMap(
                        Sensore::getId,
                        s -> {
                            List<Rilevazione> recenti = rilevazioneRepo.findTop96BySensoreIdOrderByTimestampDesc(s.getId());
                            Collections.reverse(recenti);
                            return recenti;
                        }
                ));

        // 4. Estrazione Asse Temporale (Labels):
        // Si assume sincronia nel campionamento; si estraggono i timestamp dal primo dataset disponibile.
        List<String> labels = rilevazioniBySensore.values().stream()
                .findFirst()
                .orElse(List.of())
                .stream()
                .map(r -> r.getTimestamp().format(TIME_FORMATTER))
                .toList();

        // 5. Costruzione Datasets:
        // Mapping dei dati nel formato JSON-friendly per il frontend.
        List<Map<String, Object>> datasets = new ArrayList<>();

        for (Sensore sensore : sensoriCampo) {
            List<Rilevazione> rilevazioni = rilevazioniBySensore.get(sensore.getId());
            List<Double> dataPoints = rilevazioni.stream()
                    .map(Rilevazione::getValore)
                    .toList();

            boolean isTemp = sensore.getTipo() == TipoSensore.TEMP_AIR;
            String label = isTemp ? "Temperatura Aria (°C)" : "Umidità Aria (%)";

            // Configurazione dello stile grafico (colori e riempimento)
            String color = isTemp ? "rgb(255, 99, 132)" : "rgb(54, 162, 235)";

            datasets.add(Map.of(
                    "label", label,
                    "data", dataPoints,
                    "borderColor", color,
                    "backgroundColor", color,
                    "fill", false,
                    "tension", 0.1 // Smussamento della linea (spline interpolation)
            ));
        }

        return Map.of(
                "labels", labels,
                "datasets", datasets
        );
    }
}