package com.example.demo.repository;

import com.example.demo.entity.Rilevazione;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Interfaccia di persistenza per l'entità Rilevazione.
 * Estende JpaRepository per ereditare le operazioni CRUD standard e la paginazione.
 */
public interface RilevazioneRepository extends JpaRepository<Rilevazione, Long> {

    /**
     * Recupera le ultime 96 rilevazioni storiche per un determinato sensore.
     * <p>
     * Utilizza il meccanismo di "Query Derivata" di Spring Data JPA:
     * - Top96: Applica un limit SQL ai primi 96 record.
     * - OrderByTimestampDesc: Garantisce che i dati siano ordinati dal più recente al più vecchio.
     * <p>
     * 96 rilevazioni corrispondono a una finestra temporale di 24 ore
     * con una frequenza di campionamento di 15 minuti.
     *
     * @param sensoreId L'identificativo univoco del sensore.
     * @return Lista delle rilevazioni trovate.
     */
    List<Rilevazione> findTop96BySensoreIdOrderByTimestampDesc(Long sensoreId);
}