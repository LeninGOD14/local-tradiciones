package com.sistema.tradiciones.service;

import com.sistema.tradiciones.model.Alquiler;
import com.sistema.tradiciones.model.Cliente;
import com.sistema.tradiciones.model.Prenda;
import com.sistema.tradiciones.repository.AlquilerRepository;
import com.sistema.tradiciones.repository.ClienteRepository;
import com.sistema.tradiciones.repository.PrendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AlquilerService {

    private final AlquilerRepository alquilerRepository;
    private final ClienteRepository clienteRepository;
    private final PrendaRepository prendaRepository;

    public AlquilerService(AlquilerRepository alquilerRepository, 
                          ClienteRepository clienteRepository, 
                          PrendaRepository prendaRepository) {
        this.alquilerRepository = alquilerRepository;
        this.clienteRepository = clienteRepository;
        this.prendaRepository = prendaRepository;
    }

    @Transactional
    public void registrarAlquiler(Alquiler alquiler, Cliente cliente) throws Exception {
        // 1. Manejo del Cliente: Buscamos si ya existe por cédula
        Cliente clienteFinal = clienteRepository.findByCedula(cliente.getCedula())
                .orElseGet(() -> clienteRepository.save(cliente));

        // 2. Obtener Prenda y validar
        Prenda prenda = prendaRepository.findById(alquiler.getPrenda().getId())
                .orElseThrow(() -> new Exception("La prenda no existe"));

        // 3. Validación de Precio (No menor al del inventario)
        if (alquiler.getValorCobrado().compareTo(prenda.getPrecioAlquiler()) < 0) {
            throw new Exception("El valor cobrado no puede ser menor a $" + prenda.getPrecioAlquiler());
        }

        // 4. Validar Stock
        if (prenda.getCantidad() <= 0) {
            throw new Exception("No hay stock disponible");
        }

        // 5. Configurar y guardar Alquiler
        alquiler.setCliente(clienteFinal);
        alquiler.setFechaAlquiler(LocalDate.now());
        alquilerRepository.save(alquiler);

        // 6. Actualizar Stock de la Prenda
        prenda.setCantidad(prenda.getCantidad() - 1);
        if (prenda.getCantidad() == 0) {
            prenda.setEstado("Agotado");
        }
        prendaRepository.save(prenda);
    }
}