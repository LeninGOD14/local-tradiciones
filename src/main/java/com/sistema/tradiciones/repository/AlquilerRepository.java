package com.sistema.tradiciones.repository;

import com.sistema.tradiciones.dto.ReporteGraficoDTO;
import com.sistema.tradiciones.model.Alquiler;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
    
    List<Alquiler> findByFechaAlquiler(LocalDate fecha);
    
    List<Alquiler> findByDevueltoFalse();

    List<Alquiler> findByDevueltoTrue();
    
    // CORRECCIÓN: Enviamos 0 para ingresos y gastos para que el DTO de 4 parámetros funcione siempre igual
    @Query("SELECT new com.sistema.tradiciones.dto.ReporteGraficoDTO(p.nombre, 0.0, 0.0, CAST(COUNT(a.id) AS bigdecimal)) " +
           "FROM Alquiler a " +
           "JOIN a.prenda p " +
           "WHERE a.fechaAlquiler BETWEEN :inicio AND :fin " +
           "GROUP BY p.nombre " +
           "ORDER BY COUNT(a.id) DESC")
    List<ReporteGraficoDTO> obtenerPrendasMasAlquiladas(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}