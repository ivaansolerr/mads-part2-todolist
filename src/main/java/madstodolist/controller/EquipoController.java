package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.EquipoNotFoundException;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @Autowired(required = false)
    private UsuarioService usuarioService;

    @Autowired(required = false)
    private ManagerUserSession managerUserSession;

    private void cargarDatosUsuarioEnModelo(Model model) {
        Long idUsuarioLogeado = (managerUserSession != null) ? managerUserSession.usuarioLogeado() : null;
        if (idUsuarioLogeado != null && usuarioService != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            model.addAttribute("usuario", usuario);
            model.addAttribute("esAdmin", usuario != null && Boolean.TRUE.equals(usuario.isAdmin()));
        } else {
            model.addAttribute("usuario", null);
            model.addAttribute("esAdmin", false);
        }
    }

    @GetMapping("/equipos")
    public String listadoEquipos(Model model) {
        cargarDatosUsuarioEnModelo(model);
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        model.addAttribute("equipos", equipos != null ? equipos : new ArrayList<>());
        return "listaEquipos";
    }

    @GetMapping("/equipos/{id}")
    public String detalleEquipo(@PathVariable(value = "id") Long idEquipo, Model model) {
        Equipo equipo = equipoService.recuperarEquipo(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }

        cargarDatosUsuarioEnModelo(model);

        List<Usuario> usuarios = equipoService.usuariosEquipo(idEquipo);
        if (usuarios == null) {
            usuarios = new ArrayList<>();
        }

        Long idUsuarioLogeado = (managerUserSession != null) ? managerUserSession.usuarioLogeado() : null;

        model.addAttribute("equipo", equipo);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("idUsuarioLogeado", idUsuarioLogeado);

        boolean esMiembro = false;
        if (idUsuarioLogeado != null) {
            esMiembro = usuarios.stream().anyMatch(u -> u != null && idUsuarioLogeado.equals(u.getId()));
        }
        model.addAttribute("esMiembro", esMiembro);

        return "detalleEquipo";
    }

    @GetMapping("/equipos/nuevo")
    public String formNuevoEquipo(Model model) {
        cargarDatosUsuarioEnModelo(model);
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

    // Endpoint DELETE para pruebas automatizadas (MockMvc)
    @DeleteMapping("/equipos/{idEquipo}/usuarios/{idUsuario}")
    public String eliminarUsuarioDeEquipo(@PathVariable(value = "idEquipo") Long idEquipo,
                                          @PathVariable(value = "idUsuario") Long idUsuario) {
        equipoService.eliminarUsuarioDeEquipo(idEquipo, idUsuario);
        return "redirect:/equipos";
    }

    // Endpoint POST para la interacción web desde el navegador
    @PostMapping("/equipos/{idEquipo}/usuarios/{idUsuario}/eliminar")
    public String eliminarUsuarioDeEquipoPost(@PathVariable(value = "idEquipo") Long idEquipo,
                                              @PathVariable(value = "idUsuario") Long idUsuario) {
        equipoService.eliminarUsuarioDeEquipo(idEquipo, idUsuario);
        return "redirect:/equipos";
    }

    @GetMapping("/equipos/{id}/editar")
    public String formEditarEquipo(@PathVariable(value = "id") Long idEquipo, Model model) {
        Equipo equipo = equipoService.recuperarEquipo(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }
        cargarDatosUsuarioEnModelo(model);
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