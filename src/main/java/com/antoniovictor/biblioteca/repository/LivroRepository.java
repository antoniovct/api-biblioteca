package com.antoniovictor.biblioteca.repository;

import com.antoniovictor.biblioteca.entities.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Optional<Livro>> findAllByTituloContaining(String nome);

    List<Optional<Livro>> findAllByCategoriaContaining(String categoria);
}
