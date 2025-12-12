package com.example.demo.repository;


import com.example.demo.entity.Campo;
import com.example.demo.entity.Rilevazione;
import com.example.demo.entity.Sensore;
import com.example.demo.enums.StatoCampo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CampoRepository extends JpaRepository<Campo, Long> {
    List<Campo> findByStato(StatoCampo stato);
}

