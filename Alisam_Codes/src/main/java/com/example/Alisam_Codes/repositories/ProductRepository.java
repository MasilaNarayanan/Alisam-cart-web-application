package com.example.Alisam_Codes.repositories;

import com.example.Alisam_Codes.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByAddedByManagerId(Long addedByManagerId);
}
