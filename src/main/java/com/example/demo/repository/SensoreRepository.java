package com.example.demo.repository;

import com.example.demo.entity.Sensore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensoreRepository extends JpaRepository<Sensore, Long> {
    List<Sensore> findByCampoIdAndAttivoTrue(Long campoId);
    Optional<Sensore> findByCodice(String codice);
}
