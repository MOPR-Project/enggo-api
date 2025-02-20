package com.example.restapi.course.repository;

import com.example.restapi.category.model.Category;
import com.example.restapi.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCategory(Category category);
    List<Course> findTop10ByOrderByPurchaseCountDesc();
    @Query("SELECT c FROM Course c WHERE c.createdDate >= :sevenDaysAgo ORDER BY c.createdDate DESC")
    List<Course> findTop10CreatedInLast7Days(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
}
