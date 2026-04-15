package com.sistema.tradiciones.controller;

import com.sistema.tradiciones.dto.ReporteGraficoDTO;
import com.sistema.tradiciones.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public String mostrarPaginaReportes() {
        return "reportes"; 
    }

    // Endpoint para las CARDS (Ingresos, Gastos, Neto)
    @GetMapping("/api/totales")
    @ResponseBody
    public Map<String, BigDecimal> obtenerTotales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return reporteService.obtenerTotalesRango(inicio, fin);
    }

    // Endpoint para los GRÁFICOS (Financiero o Prendas)
    @GetMapping("/api/grafico")
    @ResponseBody
    public List<ReporteGraficoDTO> obtenerDatosParaGrafico(
            @RequestParam String tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return reporteService.obtenerDatosGrafico(tipo, inicio, fin);
    }
}