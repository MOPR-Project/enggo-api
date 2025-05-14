package com.example.restapi.lession.controller;

import com.example.restapi.lession.service.SentencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;   

@RestController
@RequestMapping("/api/lession")
public class SentencesController {
    private SentencesService sentencesService;
    @Autowired
    public SentencesController(SentencesService sentencesService) {this.sentencesService = sentencesService;}

    @PostMapping("/enter-lession")
    public ResponseEntity<?> enterSentencesLession(@RequestParam Long level) {
        return sentencesService.loadSentencesByLevel(level);
    }

    @PostMapping("/submit-sentences")
    public ResponseEntity<?> submitSentences(@RequestBody Map<String, Object> requestBody) {
        try {
            Long sentenceId = Long.valueOf(requestBody.get("sentenceId").toString());
            List<String> userWords = (List<String>) requestBody.get("userWords");

            return sentencesService.checkSentencesAnswer(sentenceId, userWords);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid request data!"));
        }
    }
}
