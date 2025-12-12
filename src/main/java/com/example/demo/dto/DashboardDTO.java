package com.example.demo.dto;

import com.example.demo.enums.StatoCampo;

import java.util.List;

public record DashboardDTO(List<CampoStatsDTO> campi) {}

