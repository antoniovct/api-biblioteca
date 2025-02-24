package com.antoniovictor.biblioteca.repository;

import com.antoniovictor.biblioteca.entities.Reserva;
import com.antoniovictor.biblioteca.entities.StatusReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByStatusAndExpiracaoGreaterThanEqual(StatusReserva status, LocalDateTime data);

    Page<Reserva> findAllByStatusOrderByData(StatusReserva status, Pageable pageable);
}
