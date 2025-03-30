package com.example.restapi.lession.service;

import com.example.restapi.lession.model.Sentences;
import com.example.restapi.lession.repository.SentencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SentencesService {
    @Autowired
    private SentencesRepository sentencesRepository;

    public ResponseEntity<?> loadSentencesByLevel(Long level) {
        List<Sentences> sentencesList = sentencesRepository.findByLevel(level);

        if (sentencesList.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "No sentences found for this level!"));
        }

        return ResponseEntity.ok(Map.of("status", "success", "sentences", sentencesList));
    }

    public ResponseEntity<?> checkSentencesAnswer(Long sentenceId, List<String> userWords) {
        if (userWords == null || userWords.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "userWords cannot be empty!"));
        }

        Optional<Sentences> sentenceOpt = sentencesRepository.findById(sentenceId);
        if (sentenceOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Sentence not found!"));
        }

        Sentences sentence = sentenceOpt.get();
        List<String> correctWords = sentence.getWords();

        List<String> normalizedUserWords = userWords.stream()
                .map(String::trim)
                .toList();

        List<String> normalizedCorrectWords = correctWords.stream()
                .map(String::trim)
                .toList();

        boolean isCorrect = normalizedCorrectWords.equals(normalizedUserWords);

        return ResponseEntity.ok(Map.of(
                "status", isCorrect ? "correct" : "incorrect",
                "userWords", userWords,
                "correctWords", correctWords
        ));
    }

}
