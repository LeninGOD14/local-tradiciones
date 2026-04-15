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

    // LISTADO PRINCIPAL (MODIFICADO)
    @GetMapping
    public String listarAlquileres(Model model) {
        // Ahora mandamos dos listas distintas para las pestañas
        model.addAttribute("alquileresActivos", alquilerService.listarActivos());
        model.addAttribute("alquileresDevueltos", alquilerService.listarDevueltos());
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

    // FORMULARIO PARA EDITAR
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

    // PROCESAR GUARDADO
    @PostMapping("/guardar")
    public String guardarAlquiler(@ModelAttribute("alquiler") Alquiler alquiler, 
                                  @ModelAttribute("cliente") Cliente cliente,
                                  RedirectAttributes redirectAttributes) {
        try {
            alquilerService.registrarAlquiler(alquiler, cliente);
            String mensaje = (alquiler.getId() != null) ? "Alquiler actualizado con éxito." : "Alquiler registrado correctamente.";
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/alquileres/nuevo";
        }
        return "redirect:/alquileres";
    }

    // --- NUEVO: MARCAR COMO DEVUELTO ---
    @PostMapping("/devolver/{id}")
    public String devolverAlquiler(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            alquilerService.marcarComoDevuelto(id);
            redirectAttributes.addFlashAttribute("success", "¡Genial! La prenda se ha marcado como devuelta.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la devolución: " + e.getMessage());
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