package com.example.restapi.course.controller;

import com.example.restapi.course.model.Course;
import com.example.restapi.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    private CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public List<Course> getCoursesByCategory(@RequestParam("categoryId") Long categoryId) {
        return courseService.getCoursesByCategory(categoryId);
    }

    @GetMapping("/courses/top10/purchase-count")
    public List<Course> getTop10ByPurchaseCount() {
        return courseService.getTop10ByPurchaseCount();
    }

    @GetMapping("/courses/top10/7-days")
    public List<Course> getTop10CreatedInLast7Days() {
        return courseService.getTop10CreatedInLast7Days();
    }
}
