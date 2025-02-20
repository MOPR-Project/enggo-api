package com.example.restapi.course.service;

import com.example.restapi.category.model.Category;
import com.example.restapi.category.repository.CategoryRepository;
import com.example.restapi.course.model.Course;
import com.example.restapi.course.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Course> getCoursesByCategory(Long categoryID) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryID);
        Category category = optionalCategory.get();
        return courseRepository.findByCategory(category);
    }

    public List<Course> getTop10ByPurchaseCount() {
        return courseRepository.findTop10ByOrderByPurchaseCountDesc();
    }

    public List<Course> getTop10CreatedInLast7Days() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return courseRepository.findTop10CreatedInLast7Days(sevenDaysAgo);
    }
}
