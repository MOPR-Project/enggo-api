package com.example.restapi.category.controller;

import com.example.restapi.category.model.Category;
import com.example.restapi.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {this.categoryService = categoryService;}

    @GetMapping("/categories")
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }
}
