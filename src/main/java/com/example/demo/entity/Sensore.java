package com.example.demo.entity;

import com.example.demo.enums.TipoSensore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
public class Sensore {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true) private String codice;
    @Enumerated(EnumType.STRING) private TipoSensore tipo;
    @ManyToOne(optional=false) private Campo campo;
    private Boolean attivo = true;
    private Integer frequenzaMinuti;
    private Double latitudine, longitudine, profondita;

    @OneToMany(mappedBy = "sensore") private List<Rilevazione> rilevazioni = new ArrayList<>();

}
