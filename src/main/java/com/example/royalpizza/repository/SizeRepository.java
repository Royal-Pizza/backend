package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

    // Récupérer une taille par son nom
    List<Size> findByNameSize(String name);


}
