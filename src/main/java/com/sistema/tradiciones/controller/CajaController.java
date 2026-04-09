package com.sistema.tradiciones.controller;

import com.sistema.tradiciones.model.Alquiler;
import com.sistema.tradiciones.model.CierreCaja;
import com.sistema.tradiciones.model.Gasto;
import com.sistema.tradiciones.repository.AlquilerRepository;
import com.sistema.tradiciones.repository.CierreCajaRepository;
import com.sistema.tradiciones.repository.GastoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @GetMapping
    public String historialCierres(Model model) {
        model.addAttribute("cierres", cierreCajaRepository.findAll());
        return "lista-cierres";
    }

    @GetMapping("/resumen-hoy")
    public String resumenHoy(Model model) {
        LocalDate hoy = LocalDate.now();
        
        List<Alquiler> alquileresHoy = alquilerRepository.findByFechaAlquiler(hoy);
        List<Gasto> gastosHoy = gastoRepository.findByFecha(hoy);

        // Suma total de todos los alquileres (Efectivo + Transferencia)
        BigDecimal ingresosTotales = alquileresHoy.stream()
                .map(Alquiler::getValorCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // FILTRO: Solo lo que entró en Efectivo
        BigDecimal ingresosEfectivo = alquileresHoy.stream()
                .filter(a -> "EFECTIVO".equalsIgnoreCase(a.getMetodoPago()))
                .map(Alquiler::getValorCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // FILTRO: Solo lo que entró por Transferencia
        BigDecimal ingresosTransferencia = alquileresHoy.stream()
                .filter(a -> "TRANSFERENCIA".equalsIgnoreCase(a.getMetodoPago()))
                .map(Alquiler::getValorCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Suma de gastos (asumimos que salen del efectivo de la caja)
        BigDecimal gastos = gastosHoy.stream()
                .map(Gasto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // El SALDO NETO de caja es: Lo que entró en efectivo menos los gastos
        BigDecimal saldoCajaFisica = ingresosEfectivo.subtract(gastos);

        model.addAttribute("ingresos", ingresosTotales); // Para mostrar el éxito total
        model.addAttribute("ingresosEfectivo", ingresosEfectivo);
        model.addAttribute("ingresosTransferencia", ingresosTransferencia);
        model.addAttribute("gastos", gastos);
        model.addAttribute("saldo", saldoCajaFisica); // Lo que DEBE haber en el cajón
        
        model.addAttribute("alquileresHoy", alquileresHoy);
        model.addAttribute("gastosHoy", gastosHoy);
        
        return "cierre-hoy";
    }

    @PostMapping("/cerrar")
    public String procesarCierre(@RequestParam BigDecimal ingresos, 
                                 @RequestParam BigDecimal gastos, 
                                 @RequestParam BigDecimal saldo) {
        CierreCaja cierre = new CierreCaja();
        cierre.setFecha(LocalDate.now());
        
        // Aquí guardamos los valores finales
        // Nota: 'ingresos' aquí debería ser el efectivo si eso es lo que quieres auditar
        cierre.setTotalIngresos(ingresos); 
        cierre.setTotalGastos(gastos);
        cierre.setSaldoNeto(saldo);
        
        cierreCajaRepository.save(cierre);
        return "redirect:/caja";
    }
}