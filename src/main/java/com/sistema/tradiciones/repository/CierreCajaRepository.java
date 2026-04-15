package com.sistema.tradiciones.repository;

import com.sistema.tradiciones.model.CierreCaja;
import com.sistema.tradiciones.dto.ReporteGraficoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CierreCajaRepository extends JpaRepository<CierreCaja, Long> {

    Optional<CierreCaja> findByFecha(LocalDate fecha);

    @Query("SELECT SUM(c.totalIngresos) FROM CierreCaja c WHERE c.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumarIngresosRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT SUM(c.totalGastos) FROM CierreCaja c WHERE c.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumarGastosRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT new com.sistema.tradiciones.dto.ReporteGraficoDTO(c.fecha, c.totalIngresos, c.totalGastos, (c.totalIngresos - c.totalGastos)) " +
           "FROM CierreCaja c WHERE c.fecha BETWEEN :inicio AND :fin ORDER BY c.fecha ASC")
    List<ReporteGraficoDTO> obtenerDatosGrafico(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}