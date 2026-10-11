package com.smartbite.repository;

import com.smartbite.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, String> {

    List<MenuItem> findByCategoryIgnoreCase(String category);

    @Query("SELECT m FROM MenuItem m WHERE " +
           "(:category IS NULL OR LOWER(m.category) = LOWER(:category)) AND " +
           "(:search IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<MenuItem> filterMenuItems(@Param("category") String category, @Param("search") String search);
}
