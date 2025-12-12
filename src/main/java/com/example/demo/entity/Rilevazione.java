package com.example.demo.entity;

import com.example.demo.enums.QualitaDato;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = @Index(name="idx_sens_ts", columnList="sensore_id,timestamp"))
public class Rilevazione {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(optional=false) private Sensore sensore;
    private LocalDateTime timestamp;
    private Double valore;
    private String unita;
    @Enumerated(EnumType.STRING) private QualitaDato qualita;

}
