package madstodolist.controller;

import madstodolist.model.Equipo;
import madstodolist.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import madstodolist.controller.exception.EquipoNotFoundException;
import madstodolist.model.Usuario;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping("/equipos")
    public String listadoEquipos(Model model) {
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        model.addAttribute("equipos", equipos);
        return "listaEquipos";
    }

    @GetMapping("/equipos/{id}")
    public String detalleEquipo(@PathVariable(value = "id") Long idEquipo, Model model) {
        Equipo equipo = equipoService.recuperarEquipo(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }

        List<Usuario> usuarios = equipoService.usuariosEquipo(idEquipo);
        model.addAttribute("equipo", equipo);
        model.addAttribute("usuarios", usuarios);
        return "detalleEquipo";
    }
}