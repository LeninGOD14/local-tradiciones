package com.sistema.tradiciones.controller;

import com.sistema.tradiciones.model.Alquiler;
import com.sistema.tradiciones.model.Cliente;
import com.sistema.tradiciones.repository.AlquilerRepository;
import com.sistema.tradiciones.repository.PrendaRepository;
import com.sistema.tradiciones.service.AlquilerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/alquileres")
public class AlquilerController {

    private final AlquilerRepository alquilerRepository;
    private final PrendaRepository prendaRepository;
    private final AlquilerService alquilerService;

    public AlquilerController(AlquilerRepository alquilerRepository, 
                               PrendaRepository prendaRepository,
                               AlquilerService alquilerService) {
        this.alquilerRepository = alquilerRepository;
        this.prendaRepository = prendaRepository;
        this.alquilerService = alquilerService;
    }

    // LISTADO PRINCIPAL
    @GetMapping
    public String listarAlquileres(Model model) {
        model.addAttribute("alquileres", alquilerRepository.findAll());
        return "lista-alquileres";
    }

    // FORMULARIO PARA NUEVO
    @GetMapping("/nuevo")
    public String formularioAlquiler(Model model) {
        model.addAttribute("alquiler", new Alquiler());
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("prendas", prendaRepository.findAll());
        return "formulario-alquiler";
    }

    // FORMULARIO PARA EDITAR (REUSA LA MISMA VISTA)
    @GetMapping("/editar/{id}")
    public String editarAlquiler(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Alquiler alquiler = alquilerService.buscarPorId(id);
        if (alquiler == null) {
            redirectAttributes.addFlashAttribute("error", "El alquiler no existe.");
            return "redirect:/alquileres";
        }
        model.addAttribute("alquiler", alquiler);
        model.addAttribute("cliente", alquiler.getCliente());
        model.addAttribute("prendas", prendaRepository.findAll());
        return "formulario-alquiler"; 
    }

    // PROCESAR GUARDADO (SIRVE PARA CREAR Y ACTUALIZAR)
    @PostMapping("/guardar")
    public String guardarAlquiler(@ModelAttribute("alquiler") Alquiler alquiler, 
                                  @ModelAttribute("cliente") Cliente cliente,
                                  RedirectAttributes redirectAttributes) {
        try {
            // El service detecta si el alquiler ya tiene ID para actualizarlo
            alquilerService.registrarAlquiler(alquiler, cliente);
            
            String mensaje = (alquiler.getId() != null) ? "Alquiler actualizado con éxito." : "Alquiler registrado correctamente.";
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            // Si hay error, regresamos al formulario (ya sea modo edición o nuevo)
            return "redirect:/alquileres/nuevo";
        }
        return "redirect:/alquileres";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminarAlquiler(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            alquilerService.eliminarAlquiler(id);
            redirectAttributes.addFlashAttribute("success", "Registro eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el registro.");
        }
        return "redirect:/alquileres";
    }
}