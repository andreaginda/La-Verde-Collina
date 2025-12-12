package com.example.demo.service;

import com.example.demo.entity.Campo;
import com.example.demo.entity.Sensore;
import com.example.demo.enums.StatoCampo;
import com.example.demo.enums.TipoCampo;
import com.example.demo.enums.TipoSensore;
import com.example.demo.repository.CampoRepository;
import com.example.demo.repository.SensoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servizio di bootstrap eseguito all'avvio dell'applicazione.
 * Popola il database con lo scenario fittizio dell'azienda agricola "La Verde Collina".
 * Crea 3 campi (con diverse configurazioni) e i relativi sensori IoT.
 */
@Service
public class DatiInizialiServizio implements CommandLineRunner {

    private final CampoRepository campoRepo;
    private final SensoreRepository sensoreRepo;

    // Iniezione delle dipendenze tramite costruttore
    public DatiInizialiServizio(CampoRepository campoRepo, SensoreRepository sensoreRepo) {
        this.campoRepo = campoRepo;
        this.sensoreRepo = sensoreRepo;
    }

    @Override
    @Transactional // Garantisce che l'intera operazione sia atomica
    public void run(String... args) throws Exception {

        List<Campo> campiEsistenti = campoRepo.findAll();
        boolean dirty = false;

        for (Campo campo : campiEsistenti) {
            if (campo.getProduzioneKg() == null) {
                campo.setProduzioneKg(0.0);
                campo.setCostiAccumulati(0.0);
                campo.setRicaviStimati(0.0);
                campoRepo.save(campo);
                dirty = true;
            }
        }

        if (dirty) {
            System.out.println("### [DATA FIX] Dati finanziari/produzione sanificati a 0.0 per record esistenti. ###");
        }

        if (campoRepo.count() > 0)
        {
            return;
        }
        if (campoRepo.count() > 0)
        {
            // Se ci sono già campi, l'inizializzazione è già avvenuta.
            return;
        }

        // 1. CREAZIONE DEI CAMPI 3 campi in totale, 1 serra, 1 riposo

        // Campo 1: Attivo, Pieno
        Campo campo1 = new Campo(null,"Campo Ulivo", TipoCampo.PIENO, 20.5, StatoCampo.ATTIVO, 0.0, 0.0, 0.0);

        // Campo 2: Attivo, Serra
        Campo campo2 = new Campo(null,"Serra Grande", TipoCampo.SERRA, 5.0, StatoCampo.ATTIVO, 0.0, 0.0, 0.0);

        // Campo 3: Riposo, Pieno (Simulazione rotazione colturale)
        Campo campo3 = new Campo(null,"Campo Grano", TipoCampo.PIENO, 30.0, StatoCampo.RIPOSO, 0.0, 0.0, 0.0);

        campoRepo.saveAll(List.of(campo1, campo2, campo3));

        // 2. CREAZIONE DEI SENSORI nei campi ATTIVI

        // Sensori per Campo 1 (Pieno)
        creaSensoriStandard(campo1, 10);

        // Sensori per Campo 2 (Serra)
        creaSensoriStandard(campo2, 1);

        // Il Campo 3 è a RIPOSO, non ha sensori attivi.

        System.out.println("🌱 Dati iniziali (Campi e Sensori) creati con successo.");
    }

    // Metodo helper per creare un set standard di sensori
    private void creaSensoriStandard(Campo campo, int codiceBase) {
        // I sensori 'TEMP_SOIL' e 'SOIL_MOISTURE' hanno una profondità
        // Il sensore 'NDVI_SAT' è un dato satellitare, la posizione non è rilevante

        sensoreRepo.saveAll(List.of(
                creaSensore(campo, "A-T" + codiceBase, TipoSensore.TEMP_AIR, 0.0),
                creaSensore(campo, "A-H" + codiceBase, TipoSensore.HUMID_AIR, 0.0),
                creaSensore(campo, "S-T" + codiceBase, TipoSensore.TEMP_SOIL, 0.3), // 30 cm
                creaSensore(campo, "S-M" + codiceBase, TipoSensore.SOIL_MOISTURE, 0.3), // 30 cm
                creaSensore(campo, "N-V" + codiceBase, TipoSensore.NDVI_SAT, null)
        ));
    }

    // Metodo helper per la creazione di un singolo Sensore
    private Sensore creaSensore(Campo campo, String codice, TipoSensore tipo, Double profondita) {
        Sensore s = new Sensore();
        s.setCampo(campo);
        s.setCodice(codice);
        s.setTipo(tipo);
        s.setAttivo(true);
        s.setFrequenzaMinuti(15); // Default, rilevazione ogni 15 minuti
        s.setProfondita(profondita);

        // Assegna coordinate fittizie (semplificate)
        s.setLatitudine(41.0 + (campo.getId() * 0.1));
        s.setLongitudine(13.0 + (campo.getId() * 0.1));

        return s;

    }
}
