package com.example.royalpizza.repository;

import com.example.royalpizza.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

    // Récupérer une taille par son nom
    Optional<Size> findTopByNameSize(String name);

    List<Size> findAllByOrderByCoeffAsc();


}
