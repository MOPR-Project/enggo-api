package com.example.restapi.lession.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Sentences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long level;

    @Column(nullable = false, unique = true)
    private String sentences;

    @ElementCollection
    private List<String> words;
}
