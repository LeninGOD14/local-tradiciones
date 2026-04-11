package com.sistema.tradiciones.controller;

import com.sistema.tradiciones.model.Gasto;
import com.sistema.tradiciones.repository.GastoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/gastos")
public class GastoController {

    private final GastoRepository gastoRepository;

    public GastoController(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    @PostMapping("/guardar-rapido")
    public String guardarGasto(@RequestParam String descripcion, 
                               @RequestParam Double monto,
                               @RequestParam(required = false) String fechaGasto) {
        Gasto gasto = new Gasto();
        gasto.setDescripcion(descripcion);
        gasto.setMonto(BigDecimal.valueOf(monto));
        
        // Si viene la fecha del formulario la usamos, si no, la de hoy
        if (fechaGasto != null && !fechaGasto.isEmpty()) {
            gasto.setFecha(LocalDate.parse(fechaGasto));
        } else {
            gasto.setFecha(LocalDate.now());
        }
        
        gastoRepository.save(gasto);
        return "redirect:/caja/resumen-hoy?fecha=" + gasto.getFecha();
    }

    // LÓGICA PARA EL BOTÓN ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminarGasto(@PathVariable Long id, RedirectAttributes flash) {
        Optional<Gasto> gastoOpt = gastoRepository.findById(id);
        if (gastoOpt.isPresent()) {
            LocalDate fechaRegistro = gastoOpt.get().getFecha();
            gastoRepository.deleteById(id);
            flash.addFlashAttribute("success", "Gasto eliminado correctamente.");
            // Redireccionamos manteniendo la fecha que se estaba viendo
            return "redirect:/caja/resumen-hoy?fecha=" + fechaRegistro;
        }
        return "redirect:/caja/resumen-hoy";
    }
}