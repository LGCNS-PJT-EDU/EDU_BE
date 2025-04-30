package com.education.takeit.diagnosis.controller;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    public String recommendByDiagnosis(List<DiagnosisAnswerRequest> answers) {
        return "ok";
    }
}
