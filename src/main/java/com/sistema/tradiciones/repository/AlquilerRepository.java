package com.sistema.tradiciones.repository;

import com.sistema.tradiciones.model.Alquiler;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
    List<Alquiler> findByFechaAlquiler(LocalDate fecha);
}