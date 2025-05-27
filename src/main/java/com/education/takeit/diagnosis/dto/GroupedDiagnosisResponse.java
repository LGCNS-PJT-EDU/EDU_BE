package com.education.takeit.diagnosis.dto;

import java.util.List;

public record GroupedDiagnosisResponse(
    List<DiagnosisResponse> COMMON, List<DiagnosisResponse> BE, List<DiagnosisResponse> FE, boolean roadmap_exist) {}
