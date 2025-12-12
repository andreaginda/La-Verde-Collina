package com.example.demo.service;

import com.example.demo.entity.Campo;
import com.example.demo.entity.Rilevazione;
import com.example.demo.entity.Sensore;
import com.example.demo.enums.QualitaDato;
import com.example.demo.enums.StatoCampo;
import com.example.demo.enums.TipoSensore;
import com.example.demo.repository.CampoRepository;
import com.example.demo.repository.RilevazioneRepository;
import com.example.demo.repository.SensoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Componente Core del sistema IoT simulato.
 * Agisce come un "Gemello Digitale" (Digital Twin) dell'ecosistema agricolo,
 * generando flussi di dati sintetici realistici per validare la piattaforma di monitoraggio.
 *
 * Integra la generazione di telemetria ambientale (sensori) con la simulazione
 * dei parametri economici e produttivi (campi).
 */
@Service
public class SimulatoreServizio {

    private final SensoreRepository sensoreRepo;
    private final RilevazioneRepository rilevazioneRepo;
    private final CampoRepository campoRepo;

    // Generatore stocastico per introdurre varianza realistica nei dati simulati
    private final Random random = new Random();

    public SimulatoreServizio(SensoreRepository sensoreRepo, RilevazioneRepository rilevazioneRepo, CampoRepository campoRepo) {
        this.sensoreRepo = sensoreRepo;
        this.rilevazioneRepo = rilevazioneRepo;
        this.campoRepo = campoRepo;
    }

    /**
     * Motore di simulazione principale (Heartbeat).
     * L'annotazione @Scheduled configura l'esecuzione ciclica del metodo ogni 15 minuti (900000 ms),
     * frequenza standard per il campionamento agronomico.
     *
     * L'annotazione @Transactional garantisce l'atomicità: l'aggiornamento dei sensori
     * e dei parametri economici avviene in un'unica transazione database.
     */
    @Scheduled(fixedRate = 900000)
    @Transactional
    public void generaNuoveRilevazioni() {
        // 1. Fase Telemetria: Generazione dati ambientali
        simulaSensoriAmbientali();

        // 2. Fase Economica: Aggiornamento produzione e bilancio
        simulaEconomiaCampi();
    }

    /**
     * Genera e persiste una nuova rilevazione per ogni sensore attivo.
     * I valori sono generati algoritmicamente per simulare condizioni reali.
     */
    private void simulaSensoriAmbientali() {
        List<Sensore> sensoriAttivi = sensoreRepo.findAll().stream()
                .filter(Sensore::getAttivo)
                .toList();

        LocalDateTime now = LocalDateTime.now();

        for (Sensore sensore : sensoriAttivi) {
            double valoreSimulato = generaValoreSimulato(sensore.getTipo());

            Rilevazione ril = new Rilevazione();
            ril.setSensore(sensore);
            ril.setTimestamp(now);
            ril.setValore(valoreSimulato);
            ril.setUnita(getUnita(sensore.getTipo()));
            ril.setQualita(QualitaDato.VALIDO);

            rilevazioneRepo.save(ril);
        }
    }

    /**
     * Simula l'evoluzione temporale dei KPI economici e produttivi dei campi.
     * Implementa logiche differenziate in base allo stato operativo (ATTIVO vs RIPOSO).
     *
     * Nota: Include controlli di null-safety (Safe Unboxing) per prevenire errori a runtime
     * su dati storici parzialmente inizializzati.
     */
    private void simulaEconomiaCampi() {
        List<Campo> campi = campoRepo.findAll();

        for (Campo campo : campi) {
            // Estrazione sicura dei valori correnti (Gestione Null Pointer Exception)
            double produzioneAttuale = campo.getProduzioneKg() != null ? campo.getProduzioneKg() : 0.0;
            double costiAttuali = campo.getCostiAccumulati() != null ? campo.getCostiAccumulati() : 0.0;

            if (campo.getStato() == StatoCampo.ATTIVO) {
                // Logica CAMPO ATTIVO: Incremento Produzione e Costi Operativi

                // 1. Simulazione crescita produttiva (0.5kg - 2.0kg per ciclo)
                double incrementoProduzione = 0.5 + (random.nextDouble() * 1.5);
                campo.setProduzioneKg(produzioneAttuale + incrementoProduzione);

                // 2. Simulazione costi operativi variabili (Acqua, Fertilizzanti, Energia)
                double costiOperativi = 2.0 + (random.nextDouble() * 3.0);
                campo.setCostiAccumulati(costiAttuali + costiOperativi);

                // 3. Calcolo Ricavi Stimati (Prezzo mercato ipotetico: 3.50€/Kg)
                campo.setRicaviStimati(campo.getProduzioneKg() * 3.50);

            } else {
                // Logica CAMPO A RIPOSO: Solo Costi fissi di manutenzione
                double costiManutenzione = 0.5 + (random.nextDouble() * 1.0);
                campo.setCostiAccumulati(costiAttuali + costiManutenzione);
                // La produzione e i ricavi rimangono invariati (statici)
            }

            // Persistenza dello stato aggiornato
            campoRepo.save(campo);
        }
    }

    /**
     * Algoritmo di simulazione stocastica basato su Distribuzione Normale (Gaussiana).
     * Genera valori che oscillano realisticamente attorno a una media stagionale,
     * simulando la variabilità naturale dei fenomeni microclimatici.
     *
     * @param tipo Il tipo di sensore per determinare media e deviazione standard.
     * @return Il valore simulato normalizzato a due cifre decimali.
     */
    private double generaValoreSimulato(TipoSensore tipo) {
        double media;
        double deviazioneStandard;

        switch (tipo) {
            case TEMP_AIR:
                media = 25.0; deviazioneStandard = 2.0; break;
            case TEMP_SOIL:
                media = 20.0; deviazioneStandard = 1.5; break;
            case HUMID_AIR:
                media = 70.0; deviazioneStandard = 5.0; break;
            case SOIL_MOISTURE:
                media = 40.0; deviazioneStandard = 8.0; break;
            case NDVI_SAT:
                media = 0.65; deviazioneStandard = 0.05; break;
            default:
                return 0.0;
        }

        // Formula: Valore = Media + (Gaussiana * Deviazione)
        double valore = media + random.nextGaussian() * deviazioneStandard;

        // Arrotondamento matematico
        return Math.round(valore * 100.0) / 100.0;
    }

    /**
     * Risolve l'unità di misura standard in base alla tipologia del sensore.
     * @param tipo Tipo di sensore.
     * @return Stringa rappresentante l'unità (es. "°C", "%").
     */
    private String getUnita(TipoSensore tipo) {
        return switch (tipo) {
            case TEMP_AIR, TEMP_SOIL -> "°C";
            case HUMID_AIR, SOIL_MOISTURE -> "%";
            case NDVI_SAT -> "Index";
        };
    }
}