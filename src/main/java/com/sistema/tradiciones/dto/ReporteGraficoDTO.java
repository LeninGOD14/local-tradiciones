package com.sistema.tradiciones.dto;

import java.math.BigDecimal;

public class ReporteGraficoDTO {
    private String etiqueta;
    private BigDecimal ingreso;
    private BigDecimal gasto;
    private BigDecimal valor; // Representa el NETO en finanzas o la CANTIDAD en prendas

    public ReporteGraficoDTO() {}

    public ReporteGraficoDTO(Object etiqueta, Object ingreso, Object gasto, Object valor) {
        this.etiqueta = (etiqueta != null) ? etiqueta.toString() : "";
        this.ingreso = (ingreso instanceof BigDecimal) ? (BigDecimal) ingreso : BigDecimal.ZERO;
        this.gasto = (gasto instanceof BigDecimal) ? (BigDecimal) gasto : BigDecimal.ZERO;
        this.valor = (valor instanceof BigDecimal) ? (BigDecimal) valor : BigDecimal.ZERO;
    }

    // Getters y Setters idénticos a los que ya tienes...
    public String getEtiqueta() { return etiqueta; }
    public BigDecimal getIngreso() { return ingreso; }
    public BigDecimal getGasto() { return gasto; }
    public BigDecimal getValor() { return valor; }
}