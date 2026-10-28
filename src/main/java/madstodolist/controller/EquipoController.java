package madstodolist.controller;

import madstodolist.model.Equipo;
import madstodolist.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import madstodolist.controller.exception.EquipoNotFoundException;
import madstodolist.model.Usuario;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/equipos/nuevo")
    public String formNuevoEquipo() {
        return "formNuevoEquipo";
    }

    @PostMapping("/equipos/nuevo")
    public String crearEquipo(@RequestParam("nombre") String nombre) {
        equipoService.crearEquipo(nombre);
        return "redirect:/equipos";
    }

    @PostMapping("/equipos/{idEquipo}/usuarios/{idUsuario}")
    public String anadirUsuarioAEquipo(@PathVariable(value = "idEquipo") Long idEquipo,
                                       @PathVariable(value = "idUsuario") Long idUsuario) {
        equipoService.anadirUsuarioAEquipo(idEquipo, idUsuario);
        return "redirect:/equipos/" + idEquipo;
    }

    @DeleteMapping("/equipos/{idEquipo}/usuarios/{idUsuario}")
    public String eliminarUsuarioDeEquipo(@PathVariable(value = "idEquipo") Long idEquipo,
                                          @PathVariable(value = "idUsuario") Long idUsuario) {
        equipoService.eliminarUsuarioDeEquipo(idEquipo, idUsuario);
        return "redirect:/equipos/" + idEquipo;
    }

    @GetMapping("/equipos/{id}/editar")
    public String formEditarEquipo(@PathVariable(value = "id") Long idEquipo, Model model) {
        Equipo equipo = equipoService.recuperarEquipo(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }
        model.addAttribute("equipo", equipo);
        return "formEditarEquipo";
    }

    @PostMapping("/equipos/{id}/editar")
    public String modificarEquipo(@PathVariable(value = "id") Long idEquipo,
                                  @RequestParam("nombre") String nombre) {
        equipoService.modificarEquipo(idEquipo, nombre);
        return "redirect:/equipos";
    }

    @DeleteMapping("/equipos/{id}")
    public String eliminarEquipo(@PathVariable(value = "id") Long idEquipo) {
        equipoService.eliminarEquipo(idEquipo);
        return "redirect:/equipos";
    }


}