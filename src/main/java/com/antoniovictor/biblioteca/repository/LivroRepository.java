package com.antoniovictor.biblioteca.repository;

import com.antoniovictor.biblioteca.entities.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    Page<Optional<Livro>> findAllByTituloContaining(String nome, Pageable pageable);

    Page<Optional<Livro>> findAllByCategoriaContainingIgnoreCase(String categoria, Pageable pageable);
}
