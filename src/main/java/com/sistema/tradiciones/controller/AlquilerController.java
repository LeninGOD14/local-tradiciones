package com.sistema.tradiciones.controller;

import com.sistema.tradiciones.model.Alquiler;
import com.sistema.tradiciones.model.Cliente;
import com.sistema.tradiciones.model.Prenda;
import com.sistema.tradiciones.repository.AlquilerRepository;
import com.sistema.tradiciones.repository.ClienteRepository;
import com.sistema.tradiciones.repository.PrendaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/alquileres")
public class AlquilerController {

    private final AlquilerRepository alquilerRepository;
    private final ClienteRepository clienteRepository;
    private final PrendaRepository prendaRepository;

    public AlquilerController(AlquilerRepository alquilerRepository, 
                              ClienteRepository clienteRepository, 
                              PrendaRepository prendaRepository) {
        this.alquilerRepository = alquilerRepository;
        this.clienteRepository = clienteRepository;
        this.prendaRepository = prendaRepository;
    }

    @GetMapping
    public String listarAlquileres(Model model) {
        model.addAttribute("alquileres", alquilerRepository.findAll());
        return "lista-alquileres";
    }

    @GetMapping("/nuevo")
    public String formularioAlquiler(Model model) {
        model.addAttribute("alquiler", new Alquiler());
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("prendas", prendaRepository.findAll());
        return "formulario-alquiler";
    }

    @PostMapping("/guardar")
    public String guardarAlquiler(@ModelAttribute("alquiler") Alquiler alquiler, 
                                  @ModelAttribute("cliente") Cliente cliente,
                                  RedirectAttributes redirectAttributes) {
        
        // 1. Buscar o Crear Cliente por Cédula
        Optional<Cliente> clienteExistente = clienteRepository.findByCedula(cliente.getCedula());
        Cliente clienteFinal;
        if (clienteExistente.isPresent()) {
            clienteFinal = clienteExistente.get();
        } else {
            clienteFinal = clienteRepository.save(cliente);
        }

        // 2. Obtener la Prenda completa de la DB
        Prenda prenda = prendaRepository.findById(alquiler.getPrenda().getId()).orElse(null);

        if (prenda != null) {
            // 3. Validación de Precio (No inferior al precio de inventario)
            if (alquiler.getValorCobrado().compareTo(prenda.getPrecioAlquiler()) < 0) {
                redirectAttributes.addFlashAttribute("error", "El valor cobrado no puede ser menor al precio de alquiler ($" + prenda.getPrecioAlquiler() + ")");
                return "redirect:/alquileres/nuevo";
            }

            // 4. Validar Stock
            if (prenda.getCantidad() <= 0) {
                redirectAttributes.addFlashAttribute("error", "No hay stock disponible de esta prenda.");
                return "redirect:/alquileres/nuevo";
            }

            // 5. Configurar Alquiler y Guardar
            alquiler.setCliente(clienteFinal);
            alquiler.setFechaAlquiler(LocalDate.now());
            alquilerRepository.save(alquiler);

            // 6. Restar stock a la prenda
            prenda.setCantidad(prenda.getCantidad() - 1);
            if (prenda.getCantidad() == 0) {
                prenda.setEstado("Agotado");
            }
            prendaRepository.save(prenda);
        }

        return "redirect:/alquileres";
    }
}