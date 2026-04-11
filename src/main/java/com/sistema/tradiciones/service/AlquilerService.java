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
import java.util.List;

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

    public List<Alquiler> listarTodos() {
        return alquilerRepository.findAll();
    }

    public Alquiler buscarPorId(Long id) {
        return alquilerRepository.findById(id).orElse(null);
    }

    @Transactional
    public void registrarAlquiler(Alquiler alquiler, Cliente cliente) throws Exception {
        // 1. Manejo del Cliente (Si no existe por cédula, se guarda)
        Cliente clienteFinal = clienteRepository.findByCedula(cliente.getCedula())
                .orElseGet(() -> clienteRepository.save(cliente));

        // 2. Obtener Prenda y validar existencia
        Prenda prenda = prendaRepository.findById(alquiler.getPrenda().getId())
                .orElseThrow(() -> new Exception("La prenda no existe"));

        // 3. Validación de Precio base
        if (alquiler.getValorCobrado().compareTo(prenda.getPrecioAlquiler()) < 0) {
            throw new Exception("El valor cobrado no puede ser menor a $" + prenda.getPrecioAlquiler());
        }

        // 4. Configurar y guardar
        alquiler.setCliente(clienteFinal);
        alquiler.setFechaAlquiler(LocalDate.now());
        alquilerRepository.save(alquiler);
    }

    // --- NUEVO: MÉTODO PARA EDITAR ---
    @Transactional
    public void actualizarAlquiler(Long id, Alquiler alquilerActualizado) throws Exception {
        // 1. Verificar si el alquiler original existe
        Alquiler alquilerExistente = alquilerRepository.findById(id)
                .orElseThrow(() -> new Exception("El registro de alquiler no existe"));

        // 2. Validar la prenda (por si se cambió en el formulario)
        Prenda prenda = prendaRepository.findById(alquilerActualizado.getPrenda().getId())
                .orElseThrow(() -> new Exception("La prenda seleccionada no existe"));

        // 3. Validar el nuevo precio cobrado
        if (alquilerActualizado.getValorCobrado().compareTo(prenda.getPrecioAlquiler()) < 0) {
            throw new Exception("El nuevo valor no puede ser menor al precio base de $" + prenda.getPrecioAlquiler());
        }

        // 4. Actualizar datos (No solemos cambiar el cliente a menos que sea error de dedo, 
        // pero aquí actualizamos los campos principales)
        alquilerExistente.setPrenda(prenda);
        alquilerExistente.setValorCobrado(alquilerActualizado.getValorCobrado());
        alquilerExistente.setMetodoPago(alquilerActualizado.getMetodoPago());
        
        // La fecha de alquiler la solemos mantener, pero si necesitas cambiarla:
        // alquilerExistente.setFechaAlquiler(alquilerActualizado.getFechaAlquiler());

        alquilerRepository.save(alquilerExistente);
    }

    // --- NUEVO: MÉTODO PARA BORRAR ---
    @Transactional
    public void eliminarAlquiler(Long id) throws Exception {
        if (!alquilerRepository.existsById(id)) {
            throw new Exception("No se puede eliminar: El alquiler no existe");
        }
        alquilerRepository.deleteById(id);
    }
}