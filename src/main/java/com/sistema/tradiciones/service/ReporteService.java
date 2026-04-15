package com.sistema.tradiciones.service;

import com.sistema.tradiciones.repository.CierreCajaRepository;
import com.sistema.tradiciones.repository.AlquilerRepository;
import com.sistema.tradiciones.dto.ReporteGraficoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    @Autowired
    private CierreCajaRepository cierreCajaRepository;

    @Autowired
    private AlquilerRepository alquilerRepository;

    public Map<String, BigDecimal> obtenerTotalesRango(LocalDate inicio, LocalDate fin) {
        BigDecimal bIngresos = cierreCajaRepository.sumarIngresosRango(inicio, fin);
        BigDecimal bGastos = cierreCajaRepository.sumarGastosRango(inicio, fin);
        
        bIngresos = (bIngresos != null) ? bIngresos : BigDecimal.ZERO;
        bGastos = (bGastos != null) ? bGastos : BigDecimal.ZERO;
        
        Map<String, BigDecimal> totales = new HashMap<>();
        totales.put("ingresos", bIngresos);
        totales.put("gastos", bGastos);
        totales.put("neto", bIngresos.subtract(bGastos));
        
        return totales;
    }

    public List<ReporteGraficoDTO> obtenerDatosGrafico(String tipo, LocalDate inicio, LocalDate fin) {
        if ("PRENDAS_TOP".equals(tipo)) {
            return alquilerRepository.obtenerPrendasMasAlquiladas(inicio, fin);
        }
        return cierreCajaRepository.obtenerDatosGrafico(inicio, fin);
    }
}