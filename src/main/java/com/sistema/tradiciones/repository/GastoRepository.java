package com.sistema.tradiciones.repository;

import com.sistema.tradiciones.model.Gasto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByFecha(LocalDate fecha);
}
