package com.example.demo.dto;

import com.example.demo.enums.StatoCampo;

/**
 * Data Transfer Object (DTO) immutabile (Record) che rappresenta
 * le statistiche riassuntive di un singolo campo.
 * Usato per popolare le "Card" nella pagina principale.
 *
 * @param nomeCampo Il nome visuale del campo.
 * @param stato Lo stato operativo (es. ATTIVO, RIPOSO).
 * @param temperaturaAriaRecente L'ultima temperatura registrata (KPI principale), può essere null.
 */
public record CampoStatsDTO(
        String nomeCampo,
        StatoCampo stato,
        Double temperaturaAriaRecente,
        Double produzioneKg,
        Double bilancioEconomico
) {}
