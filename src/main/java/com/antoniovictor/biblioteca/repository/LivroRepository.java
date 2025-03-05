package com.antoniovictor.biblioteca.repository;

import com.antoniovictor.biblioteca.entities.Categoria;
import com.antoniovictor.biblioteca.entities.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    Page<Livro> findAllByTituloContaining(String nome, Pageable pageable);

    Page<Livro> findAllByCategoria(Categoria categoria, Pageable pageable);
}
