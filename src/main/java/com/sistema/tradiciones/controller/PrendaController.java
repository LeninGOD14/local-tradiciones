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

    @GetMapping
    public String listarPrendas(Model model) {
        model.addAttribute("prendas", prendaRepository.findAll());
        return "lista-prendas";
    }

    @GetMapping("/nuevo")
    public String formularioNuevaPrenda(Model model) {
        model.addAttribute("prenda", new Prenda());
        return "formulario-prenda";
    }

    @PostMapping("/guardar")
    public String guardarPrenda(@ModelAttribute("prenda") Prenda prenda) {
        prendaRepository.save(prenda);
        return "redirect:/prendas";
    }
}