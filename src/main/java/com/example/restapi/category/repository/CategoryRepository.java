    package com.example.restapi.category.repository;

    import com.example.restapi.category.model.Category;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.Optional;

    @Repository
    public interface CategoryRepository extends JpaRepository<Category, Long> {
        Optional<Category> findById(Long id);
    }
