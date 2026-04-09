package com.sistema.tradiciones.controller;

import com.sistema.tradiciones.model.Prenda;
import com.sistema.tradiciones.repository.PrendaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/prendas")
public class PrendaController {

    private final PrendaRepository prendaRepository;

    public PrendaController(PrendaRepository prendaRepository) {
        this.prendaRepository = prendaRepository;
    }

    // Listar todas las prendas
    @GetMapping
    public String listarPrendas(Model model) {
        model.addAttribute("prendas", prendaRepository.findAll());
        return "lista-prendas";     }

    // Mostrar formulario de nueva prenda
    @GetMapping("/nuevo")
    public String formularioNuevaPrenda(Model model) {
        model.addAttribute("prenda", new Prenda());
        return "formulario-prenda"; 
    }

    // Guardar la prenda
    @PostMapping("/guardar")
    public String guardarPrenda(@ModelAttribute("prenda") Prenda prenda) {
        // Lógica simple de estado basada en cantidad
        if (prenda.getCantidad() != null && prenda.getCantidad() > 0) {
            prenda.setEstado("Disponible");
        } else {
            prenda.setEstado("Agotado");
        }
        
        prendaRepository.save(prenda);
        return "redirect:/prendas";
    }
}