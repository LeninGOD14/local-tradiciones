package com.sistema.tradiciones.controller;

import com.sistema.tradiciones.model.Gasto;
import com.sistema.tradiciones.repository.GastoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/gastos")
public class GastoController {

    private final GastoRepository gastoRepository;

    public GastoController(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    @PostMapping("/guardar-rapido")
    public String guardarGasto(@RequestParam String descripcion, @RequestParam Double monto) {
        Gasto gasto = new Gasto();
        gasto.setDescripcion(descripcion);
        gasto.setMonto(java.math.BigDecimal.valueOf(monto));
        gasto.setFecha(LocalDate.now());
        gastoRepository.save(gasto);
        return "redirect:/caja/resumen-hoy";
    }
}