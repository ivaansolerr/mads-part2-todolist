package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoAutorizadoException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    private UsuarioData comprobarAdmin() {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            throw new UsuarioNoAutorizadoException();
        }
        UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
        if (usuario == null || usuario.getAdmin() == null || !usuario.getAdmin()) {
            throw new UsuarioNoAutorizadoException();
        }
        return usuario;
    }

    @GetMapping("/registrados")
    public String listadoUsuariosRegistrados(Model model) {
        UsuarioData usuarioAdmin = comprobarAdmin();

        model.addAttribute("usuario", usuarioAdmin);

        List<UsuarioData> usuarios = usuarioService.allUsuarios();
        model.addAttribute("usuarios", usuarios);

        return "registrados";
    }

    @GetMapping("/registrados/{id}")
    public String descripcionUsuario(@PathVariable(value = "id") Long id, Model model) {
        UsuarioData usuarioAdmin = comprobarAdmin();

        UsuarioData usuarioConsultado = usuarioService.findById(id);
        if (usuarioConsultado == null) {
            throw new UsuarioNotFoundException();
        }

        model.addAttribute("usuario", usuarioAdmin);
        model.addAttribute("usuarioConsultado", usuarioConsultado);

        return "descripcionUsuario";
    }

    @PostMapping("/registrados/{id}/bloquear")
    public String bloquearUsuario(@PathVariable(value = "id") Long id) {
        comprobarAdmin();
        usuarioService.cambiarEstadoBloqueo(id, true);
        return "redirect:/registrados";
    }

    @PostMapping("/registrados/{id}/desbloquear")
    public String desbloquearUsuario(@PathVariable(value = "id") Long id) {
        comprobarAdmin();
        usuarioService.cambiarEstadoBloqueo(id, false);
        return "redirect:/registrados";
    }
}