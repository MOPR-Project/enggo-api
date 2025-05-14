package com.example.restapi.lession.repository;

import com.example.restapi.lession.model.Sentences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentencesRepository extends JpaRepository<Sentences, Long> {
    List<Sentences> findByLevel(Long level);
    List<Sentences> findBySentencesContaining(String keyword);

    Sentences findBySentences(String sentence);
    boolean existsBySentences(String sentence);

    @Query(value = "SELECT * FROM sentences WHERE level = :level ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Sentences> findRandomSentencesByLevel(@Param("level") Long level);
}
