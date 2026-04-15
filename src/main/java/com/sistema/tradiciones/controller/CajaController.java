package com.sistema.tradiciones.controller;

import com.sistema.tradiciones.model.Alquiler;
import com.sistema.tradiciones.model.CierreCaja;
import com.sistema.tradiciones.model.Gasto;
import com.sistema.tradiciones.repository.AlquilerRepository;
import com.sistema.tradiciones.repository.CierreCajaRepository;
import com.sistema.tradiciones.repository.GastoRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/caja")
public class CajaController {

    private final AlquilerRepository alquilerRepository;
    private final GastoRepository gastoRepository;
    private final CierreCajaRepository cierreCajaRepository;

    public CajaController(AlquilerRepository alquilerRepository, 
                          GastoRepository gastoRepository, 
                          CierreCajaRepository cierreCajaRepository) {
        this.alquilerRepository = alquilerRepository;
        this.gastoRepository = gastoRepository;
        this.cierreCajaRepository = cierreCajaRepository;
    }

    /**
     * Muestra el historial de cierres (lista-cierres.html)
     */
    @GetMapping
    public String historialCierres(Model model) {
        // Obtenemos todos los cierres para el historial
        model.addAttribute("cierres", cierreCajaRepository.findAll());
        return "lista-cierres";
    }

    /**
     * Resumen diario con filtrado por fecha (cierre-hoy.html)
     */
    @GetMapping("/resumen-hoy")
    public String resumenCaja(@RequestParam(name = "fecha", required = false) 
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha, 
                              Model model) {
        
        // Si no hay fecha en el URL, usamos el día actual
        LocalDate fechaBusqueda = (fecha == null) ? LocalDate.now() : fecha;
        
        // Verificamos si ya existe un cierre para esta fecha
        Optional<CierreCaja> cierreExistente = cierreCajaRepository.findByFecha(fechaBusqueda);
        model.addAttribute("yaCerrado", cierreExistente.isPresent());
        
        List<Alquiler> alquileres = alquilerRepository.findByFechaAlquiler(fechaBusqueda);
        List<Gasto> gastosHoy = gastoRepository.findByFecha(fechaBusqueda);

        // 1. Total bruto (Efectivo + Transferencias)
        BigDecimal ingresosTotales = alquileres.stream()
                .map(Alquiler::getValorCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Desglose: Efectivo
        BigDecimal ingresosEfectivo = alquileres.stream()
                .filter(a -> a.getMetodoPago() != null && "EFECTIVO".equalsIgnoreCase(a.getMetodoPago()))
                .map(Alquiler::getValorCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Desglose: Transferencias
        BigDecimal ingresosTransferencia = alquileres.stream()
                .filter(a -> a.getMetodoPago() != null && "TRANSFERENCIA".equalsIgnoreCase(a.getMetodoPago()))
                .map(Alquiler::getValorCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Suma de Gastos
        BigDecimal totalGastos = gastosHoy.stream()
                .map(Gasto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Saldo Neto (Dinero real en caja física: Efectivo - Gastos)
        BigDecimal saldoCajaFisica = ingresosEfectivo.subtract(totalGastos);

        // Atributos para las "Stat Cards" y el formulario
        model.addAttribute("ingresos", ingresosTotales); 
        model.addAttribute("ingresosEfectivo", ingresosEfectivo);
        model.addAttribute("ingresosTransferencia", ingresosTransferencia);
        model.addAttribute("gastos", totalGastos);
        model.addAttribute("saldo", saldoCajaFisica);
        
        // Atributos para las tablas
        model.addAttribute("alquileresHoy", alquileres);
        model.addAttribute("gastosHoy", gastosHoy);
        
        // Para que el input date mantenga la fecha seleccionada
        model.addAttribute("fechaSeleccionada", fechaBusqueda);
        
        return "cierre-hoy";
    }

    /**
     * Procesa el guardado del cierre de caja
     */
    @PostMapping("/cerrar")
    public String procesarCierre(@RequestParam BigDecimal ingresosTotales, 
                                 @RequestParam BigDecimal ingresosEfectivo,
                                 @RequestParam BigDecimal ingresosTransferencia,
                                 @RequestParam BigDecimal gastos, 
                                 @RequestParam BigDecimal saldo,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCierre,
                                 RedirectAttributes flash) {
        
        // Validación de seguridad: Verificar si ya existe un cierre para esa fecha
        if (cierreCajaRepository.findByFecha(fechaCierre).isPresent()) {
            flash.addFlashAttribute("error", "Ya existe un cierre registrado para la fecha: " + fechaCierre);
            return "redirect:/caja/resumen-hoy?fecha=" + fechaCierre;
        }
        
        CierreCaja cierre = new CierreCaja();
        cierre.setFecha(fechaCierre);
        cierre.setTotalIngresos(ingresosTotales);
        cierre.setIngresosEfectivo(ingresosEfectivo);
        cierre.setIngresosTransferencia(ingresosTransferencia);
        cierre.setTotalGastos(gastos);
        cierre.setSaldoNeto(saldo);
        
        cierreCajaRepository.save(cierre);
        
        flash.addFlashAttribute("success", "Cierre de caja guardado exitosamente.");
        
        // Redirige al historial para ver el nuevo registro
        return "redirect:/caja";
    }

    /**
     * Elimina un registro de cierre de caja
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarCierre(@PathVariable Long id, RedirectAttributes flash) {
        try {
            cierreCajaRepository.deleteById(id);
            flash.addFlashAttribute("success", "Cierre eliminado correctamente. La edición para esa fecha ha sido habilitada.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar el cierre de caja.");
        }
        return "redirect:/caja";
    }
}