package com.example.restapi.course.model;

import com.example.restapi.category.model.Category;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseName;

    @Column
    private String description;

    @Column(nullable = false)
    private int duration;

    @ManyToOne
    private Category category;

    @Column
    private LocalDateTime createdDate;

    @Column
    private int purchaseCount;
}
