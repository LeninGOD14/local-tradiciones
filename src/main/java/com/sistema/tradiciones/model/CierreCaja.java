package com.sistema.tradiciones.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cierres_caja")
public class CierreCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private BigDecimal totalIngresos;      // Suma total (Todo)
    private BigDecimal ingresosEfectivo;   // NUEVO: Solo efectivo
    private BigDecimal ingresosTransferencia; // NUEVO: Solo transferencia
    private BigDecimal totalGastos;        // Suma de gastos
    private BigDecimal saldoNeto;          // ingresosEfectivo - Gastos

    public CierreCaja() {
    }

    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(BigDecimal totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public BigDecimal getIngresosEfectivo() {
        return ingresosEfectivo;
    }

    public void setIngresosEfectivo(BigDecimal ingresosEfectivo) {
        this.ingresosEfectivo = ingresosEfectivo;
    }

    public BigDecimal getIngresosTransferencia() {
        return ingresosTransferencia;
    }

    public void setIngresosTransferencia(BigDecimal ingresosTransferencia) {
        this.ingresosTransferencia = ingresosTransferencia;
    }

    public BigDecimal getTotalGastos() {
        return totalGastos;
    }

    public void setTotalGastos(BigDecimal totalGastos) {
        this.totalGastos = totalGastos;
    }

    public BigDecimal getSaldoNeto() {
        return saldoNeto;
    }

    public void setSaldoNeto(BigDecimal saldoNeto) {
        this.saldoNeto = saldoNeto;
    }
}
