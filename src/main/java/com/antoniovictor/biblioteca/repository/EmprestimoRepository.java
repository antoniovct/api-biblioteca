package com.antoniovictor.biblioteca.repository;


import com.antoniovictor.biblioteca.entities.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

}
