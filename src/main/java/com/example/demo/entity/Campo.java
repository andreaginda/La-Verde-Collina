package com.example.demo.entity;

import com.example.demo.enums.StatoCampo;
import com.example.demo.enums.TipoCampo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Campo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoCampo tipo; // PIENO, SERRA

    private Double superficie;

    @Enumerated(EnumType.STRING)
    private StatoCampo stato; // ATTIVO, RIPOSO

    private Double produzioneKg = 0.0;

    private Double costiAccumulati = 0.0;

    private Double ricaviStimati = 0.0;

}
